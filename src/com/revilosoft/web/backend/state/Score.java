package com.revilosoft.web.backend.state;

public class Score {
	
	private int score;
	private int userId;
	
	public Score(int score, int userId) {
		this.score = score;
		this.userId = userId;
	}

	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "Score [score=" + score + ", userId=" + userId + "]";
	}
}
