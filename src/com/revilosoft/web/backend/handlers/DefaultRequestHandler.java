package com.revilosoft.web.backend.handlers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.revilosoft.web.backend.state.GameState;
import com.revilosoft.web.backend.state.HighScores;
import com.revilosoft.web.backend.state.Score;
import com.revilosoft.web.backend.state.SessionData;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class DefaultRequestHandler implements HttpHandler {
	
	private static boolean debug = false;
	
	public enum ActionType {
		LOGIN, POST_SCORE, GET_HIGH_SCORE
	}
	
	public static final String USER_ID_PARAM = "userid";
	public static final String LEVEL_ID_PARAM = "levelid";
	public static final String SESSION_KEY_PARAM = "sessionkey";
	public static final String BODY_PARAM = "body";

	private final Object loginLock = new Object();
	private final Object postScoreLock = new Object();
	
	static class Action {
		Action(ActionType actionType) {
			type = actionType;
			params = new HashMap<String, String>();
		}
		
		ActionType type;
		Map<String, String> params;
		
		@Override
		public String toString() {
			return "Action [type=" + type + ", params=" + params + "]";
		}
	}
	
	public DefaultRequestHandler(boolean isDebugEnabled) {
		debug = isDebugEnabled;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		StringBuilder body = new StringBuilder();
		
	    try (InputStreamReader is = new InputStreamReader(exchange.getRequestBody(), "UTF-8")) {
	        char[] buffer = new char[256];
	        int read;
	        while ((read = is.read(buffer)) != -1) {
	            body.append(buffer, 0, read);
	        }
	    }
	    
	    exchange.getResponseHeaders().put("Content-Type", new ArrayList<String>(List.of("text/plain")));
		OutputStream os = exchange.getResponseBody();
		String response;
		try {
			Action action = getActionAndParams(exchange.getRequestURI());
			if (debug) {
				System.out.println("Action : " + action.toString() + "\n");
			}
			// TEST: sending non-valid request types
			switch (action.type) {
				case LOGIN:
					if (!"GET".equals(exchange.getRequestMethod())) {
						throw new UnsupportedOperationException("only supports GET");
					}
					response = handleLogin(action);
					break;
				case POST_SCORE:
					if (!"POST".equals(exchange.getRequestMethod())) {
						throw new UnsupportedOperationException("only supports POST");
					}
					response = handlePostScore(action, body.toString());
					break;
				case GET_HIGH_SCORE:
					if (!"GET".equals(exchange.getRequestMethod())) {
						throw new UnsupportedOperationException("only supports GET");
					}
					response = handleGetHighScore(action);
					break;
				default:
					throw new UnsupportedOperationException("invalid action type");
			}
			exchange.sendResponseHeaders(200, response.getBytes().length);
		} catch (Exception e) {
			response = "Error! ";
			response += e.getMessage();
			exchange.sendResponseHeaders(400, response.getBytes().length);
		}
		os.write(response.getBytes());
		exchange.getRequestBody().close();
		os.close();
	}

	private String handleLogin(Action action) {
		
		int userId = Integer.parseInt(action.params.get(USER_ID_PARAM));
		
		SessionData sessionData;
		
		synchronized (loginLock) {
			sessionData = GameState.sessionMapByUserId.get(userId);
			if (sessionData == null || sessionData.getTimeout().before(new Date())) {
				String sessionId = getRandomSessionId();
				sessionData = new SessionData(
						userId,
						sessionId,
						new Date(System.currentTimeMillis() + 10*60*1000));
				GameState.sessionMapByUserId.put(userId, sessionData);
				GameState.sessionMapBySessionId.put(sessionId, sessionData);
			}
		}
		
		if (debug) {
			System.out.println("session data: " + sessionData);
		}
		
		return sessionData.getSessionKey();
	}

	private String handlePostScore(Action action, String body) {
		
		// check if session expired
		SessionData sessionData = GameState.sessionMapBySessionId.get(action.params.get(SESSION_KEY_PARAM));
		
		if (sessionData.getTimeout().before(new Date())) {
			throw new UnsupportedOperationException("session expired");
		}
		
		int score = Integer.parseInt(body);
		int levelId = Integer.parseInt(action.params.get(LEVEL_ID_PARAM));
		
		HighScores highScoresForLevel;
		
		synchronized (postScoreLock) {
			highScoresForLevel = GameState.highScores.get(levelId);
			
			if (highScoresForLevel == null) {
				highScoresForLevel = new HighScores();
				GameState.highScores.put(levelId, highScoresForLevel);
			}
		}
		
		highScoresForLevel.addScore(new Score(score, sessionData.getUserId()));
		
		return "";
	}
	
	private String handleGetHighScore(Action action) {
		int levelId = Integer.parseInt(action.params.get(LEVEL_ID_PARAM));
		HighScores highScores = GameState.highScores.get(levelId);
		
		if (highScores == null) {
			return "";
		}
		
		return highScores.getScoresCsv();
	}

	static Action getActionAndParams(URI requestURI) throws UnsupportedEncodingException, UnsupportedOperationException {
		
		String[] parts = requestURI.getPath().split("/");
		
		if (parts.length != 3) {
			throw new UnsupportedOperationException("incorrect parts in URI");
		}
		
		if (parts[1].matches(".*[^\\d].*")) {
			throw new UnsupportedOperationException("first path param is not a number");
		}
		
		Action action;
		switch (parts[2]) {
			case "login":
				action = new Action(ActionType.LOGIN);
				action.params.put(USER_ID_PARAM, parts[1]);
				break;
			case "score":
				action = new Action(ActionType.POST_SCORE);
				action.params = splitQuery(requestURI);
				if (!action.params.containsKey(SESSION_KEY_PARAM) ||
						action.params.get(SESSION_KEY_PARAM).matches(".*[^\\w].*")) {
					throw new UnsupportedOperationException("session ID contains non-word characters");
				}
				action.params.put(LEVEL_ID_PARAM, parts[1]);
				break;
			case "highscorelist":
				action = new Action(ActionType.GET_HIGH_SCORE);
				action.params.put(LEVEL_ID_PARAM, parts[1]);
				break;
			default:
				throw new UnsupportedOperationException("invalid action type");
		}
		
		if (debug) {
			System.out.println(requestURI.getPath());
			for (int i = 0; i < parts.length; i++) {
				System.out.println(i + ": " + parts[i]);
			}
			Map<String, String> params = splitQuery(requestURI);
			if (params != null) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					System.out.println("entry: " + entry.getKey() + ", " + entry.getValue());
				}
			}
			System.out.println("query: " + requestURI.getQuery());
		}

		return action;
	}
	
	static Map<String, String> splitQuery(URI uri) throws UnsupportedEncodingException {
	    String query = uri.getQuery();
	    if (query == null) {
	    	return null;
	    } else {
	    	Map<String, String> query_pairs = new HashMap<String, String>();
	    	String[] pairs = query.split("&");
	 	    for (String pair : pairs) {
	 	        int idx = pair.indexOf("=");
	 	        query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	 	    }
	 	   return query_pairs;
	    }
	}
	
	static String getRandomSessionId() {
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder sessionId = new StringBuilder();
        Random rand = new Random();
        
        while (sessionId.length() < 8) { // length of the random string.
            int index = (int) (rand.nextFloat() * CHARS.length());
            sessionId.append(CHARS.charAt(index));
        }
        
        String sessionIdStr = sessionId.toString();
        
        return sessionIdStr;
    }
	
}
