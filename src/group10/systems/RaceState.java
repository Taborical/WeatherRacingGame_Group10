package group10.systems;

import group10.enums.ScreenState;

import java.awt.image.BufferedImage;

public class RaceState {

	// FLAGS
	public boolean isRunning = false;
	public boolean isPaused = false;
	public boolean countdownActive = false;

	// COUNTDOWN
	public int countdownValue = 3;
	public int countdownTick = 0;
	public String countdownText = "";

	// WINS COUNTER
	public int player1Wins = 0;
	public int player2Wins = 0;
	public int roundsPlayed = 0;
	public String statusMessage = "Welcome to the race!";

	// FINISH LINE
	public int finishY = 1000;

	// SUMMARY SNAPSHOT
	public BufferedImage summarySnapshot = null;

	// WINNER CONSTANTS
	public static final int NO_WINNER = 0;
	public static final int P1_WINS   = 1;
	public static final int P2_WINS   = 2;
	public static final int TIE       = 3;

	public boolean isPlayingLevel(ScreenState s) {
		return s == ScreenState.LEVEL1 || s == ScreenState.LEVEL2
				|| s == ScreenState.LEVEL3 || s == ScreenState.LEVEL4;
	}

	private void updateTimer() {
		if (!countdownActive) return;
		countdownTick++;
		if (countdownTick >= 60) {
			countdownTick = 0;
			countdownValue--;
		}
	}

	public void updateCountdown() {
		updateTimer();

		if (countdownValue > 0) {
			countdownText = String.valueOf(countdownValue);
		} else if (countdownValue == 0) {
			countdownText = "GO!";
		} else {
			countdownActive = false;
			isRunning = true;
			countdownText = "";
			statusMessage = "Race in progress...";
		}
	}

	public void startCountdown() {
		countdownActive = true;
		isRunning = false;
		countdownValue = 3;
		countdownTick = 0;
		countdownText = "3";
		statusMessage = "Get ready...";
	}

	public void resetScores() {
		player1Wins = 0;
		player2Wins = 0;
		roundsPlayed = 0;
	}

	public void updateFinishY(int levelSelection) {
		switch (levelSelection) {
		case 1 -> finishY = 20000;
		case 2 -> finishY = 40000;
		case 3 -> finishY = 60000;
		case 4 -> finishY = 80000;
		}
	}

	
	public int checkWinner(int p1CameraY, int p2CameraY) {
		if (p1CameraY >= finishY || p2CameraY >= finishY) {
			isRunning = false;
			countdownActive = false;
			roundsPlayed++;
			if (p1CameraY >= finishY && p2CameraY >= finishY) {
				statusMessage = "It's a tie! Both players reached the finish line.";
				return TIE;
			} else if (p1CameraY >= finishY) {
				player1Wins++;
				statusMessage = "Player 1 wins this round!";
				return P1_WINS;
			} else {
				player2Wins++;
				statusMessage = "Player 2 wins this round!";
				return P2_WINS;
			}
		}
		return NO_WINNER;
	}

	public void togglePause(ScreenState s) {
		if (!isPlayingLevel(s)) return;
		isPaused = !isPaused;
	}
}
