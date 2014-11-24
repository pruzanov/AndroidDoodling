package ca.on.oicr.pde.facematcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main Activity for the Game. Navigation is possible with in-game buttons 
 * and 'Back' button. Activity uses Fragments to show different views and 
 * dynamically changing UI during the game
 * 
 */
@SuppressLint("InflateParams")
public class FaceMatchActivity extends Activity implements
		TopMenuFragment.OnOptionSelectedListener,
		MatchGameFragment.OnAnswerSelectedListener,
		ConfigureDialogFragment.ConfigureDialogListener,
		ScoreFragment.OnBannerClickListener,
		MatchGameContainerFragment.OnTimeElapsedListener {

	// Game Parameters:
	protected static final int OPTIONS_COUNT = 4;
	protected static final int QUIZES_COUNT  = 10;
	protected static final String TAG = "FaceMatcher";
	// Supported Game types:
	public static final int NAME_MATCH_GAME       = 1;
	public static final int FACE_MATCH_GAME       = 2;
	public static final int FACE_MATCH_TIMED_GAME = 3;
	// Special bonus for speed
	protected static final int MAX_TIME_BONUS = 100;
	protected static final int GUESSED_RIGHT_SCORE = 15;
	protected static final int GAME_SPAN = 60;
	//SharedPreferences
	public static final String GAME_PREFS = "game_config";
	public static final String GAME_USER  = "user_name";
	public static final String GAME_SOUND = "soundsOn";
	public static final String DEFAULT_USER = "anonymous";
	public static final int KEPT_SCORES = 10;
	
	
	private FragmentManager mFragmentManager;
	private MatchGame mGame;
	private int gameInProgress;
	private int currentScore;
	private boolean soundsOn;
	private String userName;
	private MediaPlayer mediaPlayer;
	private MatchGameContainerFragment mGameContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.gameInProgress = 0;
		Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");
		setContentView(R.layout.main);

		mFragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();
		fragmentTransaction.add(R.id.ui_fragment_container,
				new TopMenuFragment());
		fragmentTransaction.commit();

		getActionBar().hide();
		new DataLoaderTask(this).execute();
		SharedPreferences sp = getSharedPreferences(GAME_PREFS, MODE_PRIVATE);
		this.soundsOn = sp.getBoolean(GAME_SOUND, true);
		this.userName = sp.getString(GAME_USER, DEFAULT_USER);
	}

	/**
	 * We overriding this to show a confirmation dialog
	 * 
	 * (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {

		if (this.gameInProgress > 0) {
			//TODO pause timer! We don't need it to keep counting
			this.mGameContainer.setTimerPaused(true);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(this.getLayoutInflater().inflate(R.layout.quit_confirm_dialog, null));
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked OK button
							Log.d(TAG, "User chose to close, exiting...");
							FaceMatchActivity.this.mGameContainer.cancelTimer();
							FaceMatchActivity.this.goToTopMenu();
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked Cancel button
							Log.d(TAG, "User chose to stay, returning...");
							FaceMatchActivity.this.mGameContainer.setTimerPaused(false);
							return;
						}
					});

			AlertDialog dialog = builder.create();
			if (this.soundsOn)
				this.playFeedbackTone(R.raw.reminder);
			dialog.show();

		} else {
			super.onBackPressed();
		}
	}
	
	/**
	 * Function for returning from a Game to the Top Menu
	 * (may not be needed, actually since the UI got redesigned and
	 * this functionality is redundant with BackStack-provided
	 * ability to revert last fragment transaction
	 */
	private void goToTopMenu() {

		Handler fragSwapper = new Handler();
		fragSwapper.postDelayed(new Runnable() {
			@Override
			public void run() {
				final FragmentTransaction fragmentTransaction = mFragmentManager
						.beginTransaction();
				fragmentTransaction.setCustomAnimations(R.animator.fade_in,
						R.animator.fade_out);
				fragmentTransaction.replace(R.id.ui_fragment_container,
						new TopMenuFragment());
				fragmentTransaction.commit();
			}
		}, 1000L);
		
		this.gameInProgress = 0;
	}

	/**
	 * Called from asynchronous task, adds validated data
	 * @param data array with Game Data 
	 */
	protected void addGameData(OicrPerson[] data) {
		this.mGame = new MatchGame(data);
		// TODO show alert, dismiss on load
		try {
			Thread.sleep(100); // Remove this if decide to go without dialog implementation in the final version
		} catch (InterruptedException IE) {
			Log.e(TAG, "Data producing thread was interrupted");
		}
	}

	/**
	 * Function for selecting type of activity
	 * @param position selected in Top Menu
	 * @see ca.on.oicr.pde.facematcher.TopMenuFragment.OnOptionSelectedListener#
	 * onOptionSelected(int) Method for communicating with Top Menu Fragment
	 */
	@Override
	public void onOptionSelected(int position) {
		switch (position) {
		case TopMenuFragment.SHOW_ABOUT:
			this.showAboutDialog();
			break;
		case TopMenuFragment.SET_OPTIONS:
			ConfigureDialogFragment confFragment = new ConfigureDialogFragment();
		    confFragment.show(getFragmentManager(), "config");
			break;
		case TopMenuFragment.SHOW_LEADERS:
			Log.d(TAG, "Showing LeaderBoard");
			this.showLeaderboard();
			break;
		case TopMenuFragment.NAME_MATCH:
			Log.d(TAG, "Starting NameMatching Game");
			this.gameInProgress = NAME_MATCH_GAME;
			this.startGame();
			break;
		case TopMenuFragment.FACE_MATCH:
			Log.d(TAG, "Starting FaceMatching Game");
			this.gameInProgress = FACE_MATCH_GAME;
			this.startGame();
			break;
		case TopMenuFragment.TIMED_MATCH:
			Log.d(TAG, "Starting TimedMatching Game");
			this.gameInProgress = FACE_MATCH_TIMED_GAME;
			this.startGame();
			break;
		default:
			break;
		};
	}

	
	/**
	 * This function handles answers to quizzes in FaceMatching game(s)
	 * @param index is an index of thumbnail clicked in  FaceMatching game 
	 */
	@Override
	public void onAnswerSelected(int index) {
		//Sounds
		if (this.soundsOn) {
			if (this.mGame.getCurrentRightAnswer() == index) {
				playFeedbackTone(R.raw.confirm);
			} else {
				playFeedbackTone(R.raw.missedit);
			}
		}
				
		this.updateGame(index);
	}

	/**
	 * This function handles responses in NameMatching game
	 * @param View - needed to get id of the radio button that got clicked
	 */
	public void onRadioButtonClicked(View v) {
		boolean checked = ((RadioButton) v).isChecked();
		int index = 0;
		// Check which radio button was clicked
		switch (v.getId()) {
		case R.id.name_option_1:
			if (checked)
				index = 0;
			break;
		case R.id.name_option_2:
			if (checked)
				index = 1;
			break;
		case R.id.name_option_3:
			if (checked)
				index = 2;
			break;
		case R.id.name_option_4:
			if (checked)
				index = 3;
			break;
		}
		//Sounds
		if (this.soundsOn) {
			if (this.mGame.getCurrentRightAnswer() == index) {
				playFeedbackTone(R.raw.confirm);
			} else {
				playFeedbackTone(R.raw.missedit);
			}
		}

		this.updateGame(index);

	}

	/**
	 * Set up the Game Container and start a new game
	 */
	private void startGame() {
		
		if (null == this.mGame) {
			Log.e(TAG, "Game was not setup properly, won't start");
			return;
		}
		this.currentScore = 0;
		this.mGameContainer = MatchGameContainerFragment.instanceOf(this.gameInProgress);
		this.mGameContainer.setFistSet(mGame.getNextGameSet());
		FragmentTransaction gameStartTransaction = mFragmentManager
				.beginTransaction()
				.setCustomAnimations(R.animator.fade_in,
				                     R.animator.fade_out)
				.addToBackStack("GameStarted")
		        .replace(R.id.ui_fragment_container,
				this.mGameContainer, "GAME");
		gameStartTransaction.commit();
	}

	/**
	 * This function is responsible for updating ongoing game and
	 * stopping it if number of quizzes exceeds the limit
	 * @param answer index of the last answer selected
	 */
	private void updateGame(int answer) {
	
		if (this.gameInProgress != FaceMatchActivity.FACE_MATCH_TIMED_GAME
				&& this.mGameContainer.getGameFragmentCounter() >= QUIZES_COUNT) {	
			this.mGameContainer.updateGame(answer, mGame.getNextGameSet(), false);
			try {
				Thread.sleep(500);
			} catch (InterruptedException ie) {
				Log.e(FaceMatchActivity.TAG, "Interrupted");
			}
			this.finishGame();
		} else {
			this.mGameContainer.updateGame(answer, mGame.getNextGameSet(), true);
		}
	}

	/**
	 * This function checks the final score, sends request for registering this score and 
	 * displays an appropriate dialog
	 */
	private void finishGame() {
		
		this.mGameContainer.cancelTimer();
		int finalScore = (this.mGameContainer.getCurrentScore() 
				        + this.mGameContainer.getTimeBonus());
		String scoreMessage = "Your Score: " + finalScore;
		this.currentScore = finalScore;
		// SHOW SCORE - if score makes it to the top, notify player
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View dialogView = this.getLayoutInflater().inflate(R.layout.complete_dialog, null);
		// check if user name set, if yes, show Top score icon
		ImageView topScore = (ImageView) dialogView.findViewById(R.id.topscore_icon);
		TextView scoreView = (TextView) dialogView.findViewById(R.id.gameover_text);
		int soundResource;
		if (!this.userName.isEmpty() && !this.userName.equals(DEFAULT_USER) 
				&& this.addNewScores(TopScoreAdapter.SCORE_SET_PREFIX + this.gameInProgress)) {
			topScore.setVisibility(ImageView.VISIBLE);
			scoreView.setTextColor(getResources().getColor(R.color.right_answer));
			scoreMessage = "New Top Score: " + finalScore;
			soundResource = R.raw.allegro;
		} else { // if user name NOT set, show warning as toast message
			topScore.setVisibility(ImageView.INVISIBLE);
			soundResource = R.raw.climb;
			if (this.userName.equals(DEFAULT_USER))
			    Toast.makeText(FaceMatchActivity.this,
					"Set User in Settings to enable Top Scores", Toast.LENGTH_LONG).show();		
		}
		scoreView.setText(scoreMessage);
		this.gameInProgress = 0;
		
		if (this.soundsOn)
			this.playFeedbackTone(soundResource);
			
		builder.setView(dialogView);	
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						FaceMatchActivity.this.goToTopMenu();
						// If user name is not set record it here and check
						// for top score. If new top score, show toast
					}
				});

		AlertDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
	}

		
	/**
	 * Function that initialises and shows leader board (scores) fragment
	 */
	private void showLeaderboard() {
		Handler fragSwapper = new Handler();
		fragSwapper.postDelayed(new Runnable() {
			@Override
			public void run() {
				final FragmentTransaction fragmentTransaction = mFragmentManager
						.beginTransaction()
						.setCustomAnimations(R.animator.fade_in,
						                     R.animator.fade_out)
						.addToBackStack("ShowScores")
						.replace(R.id.ui_fragment_container,
						new ScoresContainerFragment(), "SCORES");
				fragmentTransaction.commit();
			}
		}, 1000L);
	}

	
	/**
	 * This is to show a simple 'About' dialog
	 */
	@SuppressLint("InflateParams")
	public void showAboutDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(this.getLayoutInflater().inflate(R.layout.about_dialog, null));
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						return;
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	
	/**
	 * Settings are recorded when an appropriate dialog closes
	 * @param ConfigureDialogFragment - handle setting new parameters
	 */

	@Override
	public void onDialogPositiveClick(ConfigureDialogFragment dialog) {
		Log.d(TAG, "Received data from Configuration Dialog");
		
		SharedPreferences sp = getSharedPreferences(GAME_PREFS, MODE_PRIVATE);
		this.soundsOn = dialog.isSoundsEnabled();
		this.userName = dialog.getUserName();
		String soundState = this.soundsOn ? "ON" : "OFF";
		Log.d(TAG, "Received data from Configuration Dialog, user is " + this.userName + " sound is " + soundState);
		sp.edit().putBoolean(GAME_SOUND, this.soundsOn)
		         .putString(GAME_USER, this.userName).commit();
				
	}

	/**
	 * Function for displaying different ScoreFragments in TopScore container
	 * @param option type of score banner clicked  
	 */
	@Override
	public void onBannerClicked(int option) {
		ScoresContainerFragment scoreBox = (ScoresContainerFragment) getFragmentManager().findFragmentByTag("SCORES");
		Log.d(TAG, "Invalidating score fragment's view");
		scoreBox.showCurrentTypeScores();
		
	}
	
	/**
	 * 
	 * @param gameTypeKey - which Leader Board gets updated
	 * @return true if score is a new Top Score, false if it is not
	 */
	private boolean addNewScores(String gameTypeKey) {
		
		boolean newTopScore = true;
		
		if (null == this.userName && this.userName.isEmpty())
			return false;
		
		SharedPreferences sp = getSharedPreferences(GAME_PREFS, 
				                                    Context.MODE_PRIVATE);
		Set<String> scoreSet = new HashSet<String>();
		Set<String> tokenizedScores = sp.getStringSet(gameTypeKey, new HashSet<String>());
		
		ArrayList<Score> storedScores = new ArrayList<Score>();
		for (String s : tokenizedScores) {
			Score nextScore =  new Score(s, TopScoreAdapter.delimiter);
			if (nextScore.getScore() > this.currentScore)
				newTopScore = false;
			storedScores.add(nextScore);
		}
		storedScores.add(new Score(this.userName, this.currentScore));
				
		for (Score score : storedScores) {
			scoreSet.add(score.toString());
			if (scoreSet.size() >= KEPT_SCORES)
				break;
		}
				
		sp.edit().putStringSet(gameTypeKey, scoreSet).commit();
        return newTopScore;
	}
	
	/**
	 * playFeedbackTone function plays a sound which id is paased as an argument 
	 * @param soundResource
	 */
	private void playFeedbackTone(int soundResource) {

		if (null != this.mediaPlayer && this.mediaPlayer.isPlaying()) {
			this.mediaPlayer.stop();
			this.mediaPlayer.release();
		}

		this.mediaPlayer = MediaPlayer.create(this, soundResource);
		mediaPlayer.start(); // no need to call prepare(); create() does that for you
	}
	

   /**
    * This is for shutting down the game #3 when timer is up
    */
	@Override
	public void onTimeElapsed() {
		this.finishGame();	
	}
	

}
