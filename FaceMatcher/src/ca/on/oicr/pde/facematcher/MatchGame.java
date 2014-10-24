package ca.on.oicr.pde.facematcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


/*
 * Class to be used by activity for making game fragments
 */

public class MatchGame {
    // Only valid entries get into gameData
	private OicrPerson[] gameData;
	private Integer[] randomizedIndexes;
	private int cursor;
	private Random r;
	
	
	public MatchGame(OicrPerson [] data) {
		this.gameData = data;
		this.cursor = 0;
		this.r = new Random();
		this.randomizedIndexes = randomizeIndexes(this.gameData.length);
	}
	
	/*
	 * Load data and generate randomized array of indexes 
	 */
	public void setup() {
		
		this.randomizedIndexes = new Integer[this.gameData.length];
		for (int i = 0; i < this.randomizedIndexes.length; i++) {
			this.randomizedIndexes[i] = i;
		}	
		Collections.shuffle(Arrays.asList(this.randomizedIndexes), this.r);
	}
	
	
	public OicrPerson [] getNextSet (int size) {
		return null;
	}
	
	public GameSet getNextGameSet() {
		return null;
	}
	
	private Integer[] randomizeIndexes(int length) {
		return null;
	}
	
	/*
	 * A set of N entries for setting up one fragment
	 */
	class GameSet {
		String [] peopleNames;
		String [] URLs;
		int indexMe;
		
		GameSet() {
			this.peopleNames = new String[FaceMatchActivity.OPTIONS_COUNT];
			this.URLs        = new String[FaceMatchActivity.OPTIONS_COUNT];
			this.indexMe = r.nextInt(FaceMatchActivity.OPTIONS_COUNT);
			//TODO add actual data
		}
	}	
}
