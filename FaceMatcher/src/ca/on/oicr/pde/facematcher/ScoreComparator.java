package ca.on.oicr.pde.facematcher;

import java.util.Comparator;

/*
 * Comparator to be used with Score object (use with Collections to sort ArrayLists)
 */
public class ScoreComparator implements Comparator<Score> {
	public int compare(Score score1, Score score2) {
		return Integer.valueOf(score2.getScore()).compareTo(
			   Integer.valueOf(score1.getScore()));
	}
};
