package com.revilosoft.web.backend.state;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GameState {
	
	public static ConcurrentMap<Integer, SessionData> sessionMapByUserId;
	
	public static ConcurrentMap<String, SessionData> sessionMapBySessionId;
	
	public static ConcurrentMap<Integer, HighScores> highScores;
	
	static {
		sessionMapByUserId = new ConcurrentHashMap<Integer, SessionData>();
		sessionMapBySessionId = new ConcurrentHashMap<String, SessionData>();
		highScores = new ConcurrentHashMap<Integer, HighScores>();
	}
	
	private GameState() {}	
}
