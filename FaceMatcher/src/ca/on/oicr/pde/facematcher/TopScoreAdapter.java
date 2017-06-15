package ca.on.oicr.pde.facematcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TopScoreAdapter extends ArrayAdapter<Score> {

	public static final String SCORE_SET_PREFIX = "game_scores_";
	public static final String delimiter = ":";

	private ArrayList<Score> list;
	private int mGameType;
	private int showRows;
	private Context mContext;
	private static LayoutInflater inflater = null;
	private static final String EMPTY = "Empty";
	private final Comparator<Score> SCORECOMPARATOR = new ScoreComparator();
	
	public TopScoreAdapter(Context c, int resource, int type, int shown_rows) {
		super(c, resource);
		this.mContext = c;
		this.mGameType = type;
		this.showRows = shown_rows;
		inflater = LayoutInflater.from(this.mContext);
		this.list = new ArrayList<Score>();
		this.list = this.readScores(SCORE_SET_PREFIX + this.mGameType);
		while (this.list.size() < this.showRows)
			this.list.add(new Score(EMPTY,0));
	}

	@Override
	public void clear() {
		this.list.clear();
	}

	@Override
	public int getCount() {
		return this.showRows;
	}

	@Override
	public Score getItem(int position) {
		return this.list.get(position);
	}

	
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View newView = convertView;
        Score currentScore = this.getItem(position);
        
		if (null == convertView) {
			newView = inflater.inflate(R.layout.score, null);
		}
		// Set player name adding position, highlighting #1
		TextView player = (TextView) newView.findViewById(R.id.lb_player_name);
		String nameToShow  = "-- empty --";
		String scoreToShow = "###";
		
		if (null != currentScore && !currentScore.getName().equals(EMPTY)) {
			nameToShow = currentScore.getName();
			scoreToShow = "" + currentScore.getScore();
		} else {
			player.setTextColor(this.mContext.getResources().getColor(R.color.empty_score));
		}
		
		player.setText((position + 1) + ". " + nameToShow);
		if (position == 0)
			player.setTextAppearance(this.mContext, R.style.boldText);
			
		TextView score =  (TextView) newView.findViewById(R.id.lb_player_score);
		score.setText(scoreToShow);
		if (position == 0)
			score.setTextAppearance(this.mContext, R.style.boldText);
		
		
	 return newView;
	}
	
	/*
	 * ========================Custom methods================================
	 */

	private ArrayList<Score> readScores(String spKey) {
		SharedPreferences sp = this.mContext.getSharedPreferences(FaceMatchActivity.GAME_PREFS,
				                                                  Context.MODE_PRIVATE);
		ArrayList<Score> scores = new ArrayList<Score>();
		Set<String> tokenizedScores = sp.getStringSet(spKey,
				new HashSet<String>());
		for (String s : tokenizedScores) {
			scores.add(new Score(s, TopScoreAdapter.delimiter));
		}
		Collections.sort(scores, SCORECOMPARATOR);
		return scores;
	}
	
}
