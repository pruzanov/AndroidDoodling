package ca.on.oicr.pde.facematcher;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ca.on.oicr.pde.facematcher.MatchGame.GameSet;

public class MatchGameContainerFragment extends Fragment {
	
	private int currentType = 1;
	private int currentScore;
	private int timeBonus;
	private int gameFragmentCounter;
	private TimerTask mTimer;
	
	private MatchGame.GameSet fistSet;
	protected MatchGame.GameSet getFistSet() {
		return fistSet;
	}

	protected void setFistSet(MatchGame.GameSet fistSet) {
		this.fistSet = fistSet;
	}

	private static final int MAX_TIME = 600;
	// Game Parameters:
	protected static final int OPTIONS_COUNT = 4;
	protected static final int QUIZES_COUNT = 5;
	// Special bonus for speed
	protected static final int MAX_TIME_BONUS = 100;
	protected static final int GUESSED_RIGHT_SCORE = 15;
	protected static final int GAME_SPAN = 120;
    OnTimeElapsedListener mCallback;
	
	// Container Activity must implement this interface
    public interface OnTimeElapsedListener {
			public void onTimeElapsed();
	}
	
	// Setters for game parameters
	public void setGameType(int currentType) {
		this.currentType = currentType;
	}

	protected int getCurrentScore() {
		return currentScore;
	}

	public void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
	}

	public void setTimeBonus(int timeBonus) {
		this.timeBonus = timeBonus;
	}

	public int getTimeBonus() {
		return timeBonus;
	}

	public void setGameFragmentCounter(int gameFragmentCounter) {
		this.gameFragmentCounter = gameFragmentCounter;
	}

	public int getGameFragmentCounter() {
		return gameFragmentCounter;
	}
	
	//public void setTimerCancelled(boolean timerCancelled) {
	//	this.timerCancelled = timerCancelled;
	//}
	
	public void cancelTimer() {
		if (null != this.mTimer && !this.mTimer.isCancelled())
			this.mTimer.cancel(true);
	}
	
	
	
	// FaceMatchFragment version
	public static MatchGameContainerFragment instanceOf(int gameType) {
		MatchGameContainerFragment fragment = new MatchGameContainerFragment();
        fragment.setGameType(gameType);
		fragment.setCurrentScore(0);
		fragment.setTimeBonus(MAX_TIME_BONUS);
		fragment.setGameFragmentCounter(0);
	
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnTimeElapsedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnTimeElapsedListener");
		}	
		this.startGame();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.game_container_fragment,
				container, false);
        if (this.currentType == FaceMatchActivity.FACE_MATCH_TIMED_GAME) {
        	rootView.findViewById(R.id.player_time).setVisibility(View.VISIBLE);
        	rootView.findViewById(R.id.quizz_counter).setVisibility(View.INVISIBLE);
        } else {
        	rootView.findViewById(R.id.player_time).setVisibility(View.INVISIBLE);
        	TextView countText = (TextView) rootView.findViewById(R.id.quizz_counter);
        	rootView.setVisibility(View.VISIBLE);
		    countText.setText(this.getGameFragmentCounter() + " of " + QUIZES_COUNT);
        }
		
		return rootView;
	}
	
	private void updateQuizCounter() {
		if (this.currentType == FaceMatchActivity.NAME_MATCH_GAME ||
				this.currentType == FaceMatchActivity.FACE_MATCH_GAME) {
			    TextView countText = (TextView) this.getView().findViewById(R.id.quizz_counter);
			    String updatedCount = (this.getGameFragmentCounter() + 1) + " of " + QUIZES_COUNT;
			    countText.setText(updatedCount);
			}
	}

	private void startGame() {
		if (null != this.mTimer && !this.mTimer.isCancelled())
			this.mTimer.cancel(true);
		this.mTimer = new TimerTask();
		this.mTimer.execute(FaceMatchActivity.GAME_SPAN);
		this.updateGame(-1, this.getFistSet());
		
		
	}

	protected void updateGame(int answer, GameSet nextSet) {
		// Reveal answers if user made a choice
		if (answer >= 0) {
			try {
				MatchGameFragment current = (MatchGameFragment) getFragmentManager()
						.findFragmentByTag("CURRENT");
				current.showAnswers(answer);
				if (answer == current.getRightAnswer()) {
					setCurrentScore(this.currentScore += GUESSED_RIGHT_SCORE);
				}
			} catch (Exception e) {
				Log.e(FaceMatchActivity.TAG,
						"Could not get current MatchGameFragment fragment");
			}
			
			//Update Score feedback
			TextView scoreView = (TextView) getView().findViewById(R.id.player_score);
			scoreView.setText(getResources().getString(R.string.score_string) + this.currentScore);
			updateQuizCounter();
		}
		
        	this.constructAndShowNext(nextSet);
	}

	private void constructAndShowNext(MatchGame.GameSet set) {
		this.gameFragmentCounter++;
		final MatchGameFragment nextFragment;

		if (this.currentType == FaceMatchActivity.NAME_MATCH_GAME) {
			Drawable thumb = getResources().getDrawable(
					getResources().getIdentifier(set.URLs[set.indexMe],
							"drawable", this.getClass().getPackage().getName()));
			nextFragment = MatchGameFragment.instanceOf(thumb, set.peopleNames,
					set.indexMe, this.currentScore);
			this.updateCurrentFragment(nextFragment);
		} else if (this.currentType == FaceMatchActivity.FACE_MATCH_GAME
				|| this.currentType == FaceMatchActivity.FACE_MATCH_TIMED_GAME) {
			Drawable[] thumbs = new Drawable[OPTIONS_COUNT];
			for (int d = 0; d < OPTIONS_COUNT; d++) {
				thumbs[d] = getResources().getDrawable(
						getResources().getIdentifier(set.URLs[d], "drawable",
								this.getClass().getPackage().getName()));
			}
			boolean timer = this.currentType == FaceMatchActivity.FACE_MATCH_TIMED_GAME ? true
					: false;
			nextFragment = MatchGameFragment.instanceOf(thumbs,
					set.peopleNames[set.indexMe], timer, set.indexMe,
					this.currentScore);
			this.updateCurrentFragment(nextFragment);
		}
	}

	/*
	 * Function for replacing fragments during a game
	 */
	private void updateCurrentFragment(final MatchGameFragment nextFragment) {
		Handler fragSwapper = new Handler();
		fragSwapper.postDelayed(new Runnable() {
			@Override
			public void run() {
				final FragmentTransaction fragmentTransaction = getFragmentManager()
						.beginTransaction();
				fragmentTransaction.setCustomAnimations(R.animator.fade_in,
						R.animator.fade_out);
				fragmentTransaction.replace(R.id.game_fragment_frame,
						nextFragment, "CURRENT");
				fragmentTransaction.commit();
			}
		}, 1000L);
	}

	private void updateTimer(int seconds) {
		if (this.isVisible()) {
			// Forbid time of more than MAX_TIME:
			if (seconds > MAX_TIME) {
				seconds = MAX_TIME;
			} else if (seconds < 0) {
				seconds = 0;
			}

			StringBuilder tSB = new StringBuilder();
			if (seconds >= 600)
				tSB.append(seconds / 60).append(":");
			else
				tSB.append("0").append(seconds / 60).append(":");

			int reminder = seconds % 60;
			if (reminder >= 10)
				tSB.append(reminder);
			else
				tSB.append("0").append(reminder);

			TextView timerView = (TextView) getView().findViewById(
					R.id.player_time);
			timerView.setText(tSB.toString());
			timerView.invalidate();
		}
	}

	/*
	 * TimerTask takes care of timed game and timer update in timed Face
	 * Matching Game
	 */
	class TimerTask extends AsyncTask<Integer, Integer, Void> {

		@Override
		protected void onPostExecute(Void result) {
			if (currentType == FaceMatchActivity.FACE_MATCH_TIMED_GAME) {
				Log.d(FaceMatchActivity.TAG, "Sending Time Elapsed Intent");
				mCallback.onTimeElapsed();
			}
		}
	

		@Override
		protected void onProgressUpdate(Integer... values) {
			// decrement time bonus
			MatchGameContainerFragment.this.timeBonus = values[0];
			if (MatchGameContainerFragment.this.timeBonus < 0)
				MatchGameContainerFragment.this.timeBonus = 0;
			updateTimer(values[1]);
		}

		@Override
		protected Void doInBackground(Integer... params) {
			int timerSeconds = params[0];
			for (int t = 0; t <= params[0]; t++) {
				//if (MatchGameContainerFragment.this.isTimerCancelled())
				//	break;
				try {
					Thread.sleep(1000L); // one second tick
				} catch (InterruptedException ie) {
					Log.d(FaceMatchActivity.TAG, "Timer Thread was interupted");
					return null;
				}
				timerSeconds--;
				int progress = (int) ((1.0f - (t / (float) params[0])) * MAX_TIME_BONUS);
				Log.d(FaceMatchActivity.TAG, "Progress is " + progress);

				this.publishProgress(progress, timerSeconds);
			}
			return null;

		}

	}

}
