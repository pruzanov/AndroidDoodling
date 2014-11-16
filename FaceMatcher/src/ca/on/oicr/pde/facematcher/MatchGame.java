package ca.on.oicr.pde.facematcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Class to be used by activity for making game fragments
 */

public class MatchGame {
	// Only valid entries get into gameData
	private OicrPerson[] gameData;
	private Integer[] randomizedIndexes;
	private int cursor;
	private int counter;
	private int currentRightAnswer;

	public int getCurrentRightAnswer() {
		return currentRightAnswer;
	}

	private void setCurrentRightAnswer(int currentRightAnswer) {
		this.currentRightAnswer = currentRightAnswer;
	}

	public MatchGame(OicrPerson[] data) {
		this.gameData = data;
		this.setup();
	}

	/**
	 * Load data and generate randomised array of indexes
	 */
	public void setup() {
		this.randomizedIndexes = randomizeIndexes(this.gameData.length);
		this.cursor = 0;
		this.counter = 0;
	}

	public GameSet getNextGameSet() {
		this.counter++;
		return new GameSet();
	}

	public int getCounter() {
		return counter;
	}

	/**
	 * Static function for randomising indexes in N element array
	 * returns randomised array of indexes
	 * @param length - the size of the array requested
	 */
	private static Integer[] randomizeIndexes(int length) {
		Integer[] randomized = new Integer[length];
		Random r = new Random();
		for (int i = 0; i < randomized.length; i++) {
			randomized[i] = i;
		}
		Collections.shuffle(Arrays.asList(randomized), r);
		return randomized;
	}

	/**
	 * A set of FaceMatchActivity.OPTIONS_COUNT entries 
	 * for setting up one fragment 
	 */
	class GameSet {
		String[] peopleNames;
		String[] URLs;
		int indexMe; // index of the Right answer

		GameSet() {
			Random r = new Random();
			this.peopleNames = new String[FaceMatchActivity.OPTIONS_COUNT];
			this.URLs = new String[FaceMatchActivity.OPTIONS_COUNT];
			this.indexMe = r.nextInt(FaceMatchActivity.OPTIONS_COUNT);
			MatchGame.this.setCurrentRightAnswer(indexMe);
			if (MatchGame.this.cursor + FaceMatchActivity.OPTIONS_COUNT > MatchGame.this.randomizedIndexes.length)
				MatchGame.this.setup();

			for (int c = 0; c < FaceMatchActivity.OPTIONS_COUNT; c++) {
				this.peopleNames[c] = gameData[MatchGame.this.randomizedIndexes[c + MatchGame.this.cursor]]
						.getName();
				this.URLs[c] = gameData[MatchGame.this.randomizedIndexes[c + MatchGame.this.cursor]]
						.getImageURL();
			}
			MatchGame.this.cursor += FaceMatchActivity.OPTIONS_COUNT;
		}
	}
}
