package com.revilosoft.web.backend.state;

import java.util.Date;

public class SessionData {
	
	private int userId;
	private String sessionKey;
	private Date timeout;
	
	public SessionData(int userId, String sessionKey, Date timeout) {
		this.userId = userId;
		this.sessionKey = sessionKey;
		this.timeout = timeout;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getSessionKey() {
		return sessionKey;
	}
	
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	
	public Date getTimeout() {
		return timeout;
	}
	
	public void setTimeout(Date timeout) {
		this.timeout = timeout;
	}

	@Override
	public String toString() {
		return "SessionData [userId=" + userId + ", sessionKey=" + sessionKey + ", timeout=" + timeout + "]";
	}
	
	
}
