package com.revilosoft.web.backend.state;

import java.util.LinkedList;
import java.util.ListIterator;

public class HighScores {

	LinkedList<Score> highScoresList = new LinkedList<Score>();
	
	Score lowestScore;
	
	public synchronized void addScore(Score score) {
		
		if (lowestScore == null) {
			highScoresList.add(score);
			lowestScore = score;
		} else if (highScoresList.size() < 15 || score.getScore() > lowestScore.getScore()) {
			ListIterator<Score> it = highScoresList.listIterator();
			int insertIndex = highScoresList.size();
			boolean shouldInsert = true;
			boolean foundInsertion = false;
			while (it.hasNext()) {
				Score nextScore = it.next();
				// if we ever find the same value, break immediately, and don't modify
				if (score.getScore() == nextScore.getScore()) {
					shouldInsert = false;
					break;
				}
				if (!foundInsertion && score.getScore() > nextScore.getScore()) {
					insertIndex = it.previousIndex();
					foundInsertion = true;
				}
				if (score.getUserId() == nextScore.getUserId()) {
					if (score.getScore() > nextScore.getScore()) {
						it.remove();
					} else {
						shouldInsert = false;
						break;
					}
				}
			}
			if (shouldInsert)
				highScoresList.add(insertIndex, score);
			while (highScoresList.size() > 15)
				highScoresList.removeLast();
			lowestScore = highScoresList.getLast();
		}
	}
	
	public synchronized String getScoresCsv() {
		StringBuilder scoresCsv = new StringBuilder();
		ListIterator<Score> it = highScoresList.listIterator();
		
		while (it.hasNext()) {
			Score nextScore = it.next();
			scoresCsv.append(nextScore.getUserId() + "=" + nextScore.getScore());
			if (it.hasNext())
				scoresCsv.append(",");
		}
		
		return scoresCsv.toString();
	}

	@Override
	public String toString() {
		return "HighScores [highScoresList=" + highScoresList + "]";
	}
	
}
