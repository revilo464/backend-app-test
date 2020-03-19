package com.revilosoft.web.backend.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HighScoresTest {
	
	HighScores highScores;

	@BeforeEach
	void setUp() throws Exception {
		highScores = new HighScores();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testSameUserLowerScore() {
		Score score1 = new Score(25, 0);
		Score score2 = new Score(20, 0);
		highScores.addScore(score1);
		highScores.addScore(score2);
		assertEquals(1, highScores.highScoresList.size());
		assertEquals(25, highScores.highScoresList.getFirst().getScore());
		assertEquals(25, highScores.lowestScore.getScore());
	}
	
	@Test
	void testSameUserHigherScore() {
		Score score1 = new Score(20, 0);
		Score score2 = new Score(25, 0);
		highScores.addScore(score1);
		highScores.addScore(score2);
		assertEquals(1, highScores.highScoresList.size());
		assertEquals(25, highScores.highScoresList.getFirst().getScore());
		assertEquals(25, highScores.lowestScore.getScore());
	}
	
	@Test
	void listIsAlways15Max() {
		var rand = new Random();
		for (int i = 0; i < 100; i++) {
			highScores.addScore(new Score(rand.nextInt(100), rand.nextInt(100)));
		}
		assertEquals(15, highScores.highScoresList.size());
	}
	
	@Test
	void usersOnlyAppearOnce() {
		var rand = new Random();
		for (int i = 0; i < 1000; i++) {
			highScores.addScore(new Score(rand.nextInt(100), rand.nextInt(10)));
		}
		assertEquals(10, highScores.highScoresList.size());
	}
	
	@Test
	void testGetCsv() {
		for (int i = 0; i < 10; i++) {
			highScores.addScore(new Score(i, i));
		}
		assertEquals("9=9,8=8,7=7,6=6,5=5,4=4,3=3,2=2,1=1,0=0",
				highScores.getScoresCsv());
	}

}
