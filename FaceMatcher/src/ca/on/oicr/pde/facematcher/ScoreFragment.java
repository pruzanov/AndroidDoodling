package ca.on.oicr.pde.facematcher;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

public class ScoreFragment extends Fragment {
	OnBannerClickListener mCallback;
	private int gameType;
	private int rowsToShow;
	public static final int MIN_ROWS = 4;
	public static final int MAX_ROWS = 9;

	// Getters and Setters
	private int getRowsToShow() {
		return rowsToShow;
	}

	public int getGameType() {
		return gameType;
	}

	public void setGameType(int gameType) {
		this.gameType = gameType;
	}

	// Container Activity must implement this interface
	public interface OnBannerClickListener {
		public void onBannerClicked(int option);
	}

	private TopScoreAdapter mAdapter;

	// FaceMatchFragment version
	public static ScoreFragment instanceOf(int type, int rows) {
		ScoreFragment fragment = new ScoreFragment();
		fragment.setGameType(type);
		fragment.setRows(rows);
		return fragment;
	}

	// Set number of rows shown in list view of ScoreFragments
	private void setRows(int rows) {
		if (rows < MIN_ROWS)
			this.rowsToShow = MIN_ROWS;
		else if (rows > MAX_ROWS)
			this.rowsToShow = MAX_ROWS;
		else
			this.rowsToShow = rows;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.score_fragment, container,
				false);
		// Configure banner
		ImageView banner = (ImageView) rootView.findViewById(R.id.banner_score);

		switch (this.getGameType()) {
		case FaceMatchActivity.NAME_MATCH_GAME:
			banner.setImageResource(R.drawable.banner_game02);
			banner.setBackgroundResource(R.color.game2_color);
			break;
		case FaceMatchActivity.FACE_MATCH_GAME:
			banner.setImageResource(R.drawable.banner_game01);
			banner.setBackgroundResource(R.color.game1_color);
			break;
		case FaceMatchActivity.FACE_MATCH_TIMED_GAME:
			banner.setImageResource(R.drawable.banner_game03);
			banner.setBackgroundResource(R.color.game3_color);
			break;
		default:
			break;
		};
		
		if (this.getRowsToShow() == MAX_ROWS) {
			banner.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					Log.d(FaceMatchActivity.TAG, "banner touched, id is "
							+ gameType);
					mCallback.onBannerClicked(gameType);
					return v.performClick();
				}
			});
		}
		// Configure list view with scores
		ListView listView = (ListView) rootView.findViewById(R.id.score_list);
		this.mAdapter = new TopScoreAdapter(container.getContext(),
				R.layout.score, this.getGameType(), this.rowsToShow);
		this.mAdapter.setNotifyOnChange(true);
		listView.setAdapter(this.mAdapter);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnBannerClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnBannerClickListener");
		}
	}

	// TODO implement score-recording functionality

}
