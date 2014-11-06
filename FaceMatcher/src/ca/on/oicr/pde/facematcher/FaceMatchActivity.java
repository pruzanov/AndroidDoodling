package ca.on.oicr.pde.facematcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import ca.on.oicr.pde.facematcher.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
@SuppressLint("InflateParams")
public class FaceMatchActivity extends Activity implements
		TopMenuFragment.OnOptionSelectedListener,
		MatchGameFragment.OnAnswerSelectedListener,
		ConfigureDialogFragment.ConfigureDialogListener{

	// Game Parameters:
	protected static final int OPTIONS_COUNT = 4;
	protected static final int QUIZES_COUNT  = 5;
	private static final int SETTINGS_REQUEST_CODE = 33;
	protected static final String TAG = "FaceMatcher";
	// Supported Game types:
	public static final int NAME_MATCH_GAME       = 1;
	public static final int FACE_MATCH_GAME       = 2;
	public static final int FACE_MATCH_TIMED_GAME = 3;
	// Special bonus for speed
	private static final int MAX_TIME_BONUS = 100;
	private static final int GUESSED_RIGHT_SCORE = 15;
	private static final int GAME_SPAN = 120;

	private FragmentManager mFragmentManager;
	private MatchGame mGame;
	private int gameInProgress;
	private int currentScore;
	private int timeBonus;
	private int gameFragmentCounter;
	private boolean timerCancelled;

	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	// private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	// private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	// private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	// private static final int HIDER_FLAGS = SystemUiHider.FLAG_FULLSCREEN;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	// private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.gameInProgress = 0;
		this.timerCancelled = true;
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
		// final View controlsView =
		// findViewById(R.id.fullscreen_content_controls);
		// final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		// mSystemUiHider = SystemUiHider.getInstance(this, controlsView,
		// HIDER_FLAGS);

		// mSystemUiHider.setup();

		/*
		 * mSystemUiHider .setOnVisibilityChangeListener(new
		 * SystemUiHider.OnVisibilityChangeListener() {
		 * 
		 * @Override
		 * 
		 * @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) public void
		 * onVisibilityChange(boolean visible) { if (visible && AUTO_HIDE) { //
		 * Schedule a hide(). delayedHide(AUTO_HIDE_DELAY_MILLIS); } } });
		 */

		// Set up the user interaction to manually show or hide the system UI.
		/*
		 * controlsView.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View view) { if (TOGGLE_ON_CLICK) {
		 * mSystemUiHider.toggle(); } else { mSystemUiHider.show(); } } });
		 */

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		// findViewById(R.id.dummy_button1).setOnTouchListener(
		// mDelayHideTouchListener);

	}

	@Override
	public void onBackPressed() {

		if (this.gameInProgress > 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.ic_action_warning);
			builder.setTitle(R.string.game_close_warning);
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked OK button
							Log.d(TAG, "User chose to close, exiting...");
							FaceMatchActivity.this.goToTopMenu();
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked Cancel button
							Log.d(TAG, "User chose to stay, returning...");
							return;
						}
					});

			AlertDialog dialog = builder.create();
			dialog.show();

		} else {
			super.onBackPressed();
		}
	}

	private void goToTopMenu() {
		//final TopMenuFragment topFragment = new TopMenuFragment();
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
		this.timerCancelled = true;
		this.gameInProgress = 0;
	}

	/*
	 * Called from asynchronous task, adds validated data
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

	/*
	 * (non-Javadoc)
	 * 
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
			// set options (in a dialog?) TODO: consider setting user
			// credentials via options menu
			Log.d(TAG, "Would Have Open Settings Dialog");
			ConfigureDialogFragment confFragment = new ConfigureDialogFragment();
		    confFragment.show(getFragmentManager(), "config");
			break;
		case TopMenuFragment.SHOW_LEADERS:
			// TODO launch Leaderboard viewing activity:
			Log.d(TAG, "Would Show LeaderBoard, if possible");
			break;
		case TopMenuFragment.NAME_MATCH:
			Log.d(TAG, "Starting NameMatching Game");
			this.gameInProgress = NAME_MATCH_GAME;
			this.startGame(this.gameInProgress);
			break;
		case TopMenuFragment.FACE_MATCH:
			Log.d(TAG, "Starting FaceMatching Game");
			this.gameInProgress = FACE_MATCH_GAME;
			this.startGame(this.gameInProgress);
			break;
		case TopMenuFragment.TIMED_MATCH:
			Log.d(TAG, "Starting TimedMatching Game");
			this.gameInProgress = FACE_MATCH_TIMED_GAME;
			this.startGame(this.gameInProgress);
			break;
		default:
			break;
		}
		;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.on.oicr.pde.facematcher.MatchGameFragment.OnOptionSelectedListener#
	 * onOptionSelected(int) Method for interaction with MatchGameFragment
	 */
	@Override
	public void onAnswerSelected(int index) {
		Log.d(TAG, "Would handle face thumb with array index " + index
				+ " in Face Matching Game");
		this.updateGame(this.gameInProgress, index);
	}

	public void onRadioButtonClicked(View v) {
		boolean checked = ((RadioButton) v).isChecked();
		int index = 0;
		// Check which radio button was clicked
		switch (v.getId()) {
		case R.id.name_option_1:
			if (checked)
				index = 0;
			//Log.d(FaceMatchActivity.TAG, "Selected Option 1");
			break;
		case R.id.name_option_2:
			if (checked)
				index = 1;
			//Log.d(FaceMatchActivity.TAG, "Selected Option 2");
			break;
		case R.id.name_option_3:
			if (checked)
				index = 2;
			//Log.d(FaceMatchActivity.TAG, "Selected Option 3");
			break;
		case R.id.name_option_4:
			if (checked)
				index = 3;
			//Log.d(FaceMatchActivity.TAG, "Selected Option 4");
			break;
		}

		this.updateGame(this.gameInProgress, index);

	}

	// @Override
	/*
	 * protected void onPostCreate(Bundle savedInstanceState) {
	 * super.onPostCreate(savedInstanceState);
	 * 
	 * // Trigger the initial hide() shortly after the activity has been //
	 * created, to briefly hint to the user that UI controls // are available.
	 * delayedHide(100); }
	 */

	private void startGame(int type) {
		if (null == this.mGame) {
			Log.e(TAG, "Game was not setup properly, won't start");
			return;
		}
		this.currentScore = 0;
		this.timeBonus = MAX_TIME_BONUS;
		this.gameFragmentCounter = 0;
		this.timerCancelled = false;
		new TimerTask().execute(FaceMatchActivity.GAME_SPAN);
		this.updateGame(type, -1);
	}

	private void updateGame(int type, int answer) {

		// Reveal answers if user made a choice
		if (answer >= 0) {
			try {
				MatchGameFragment current = (MatchGameFragment) getFragmentManager()
						.findFragmentByTag("CURRENT");
				current.showAnswers(answer);
				Log.d(TAG, "Showing answers, would updates scores");
				if (answer == current.getRightAnswer()) {
					this.currentScore += GUESSED_RIGHT_SCORE;
				}
			} catch (Exception e) {
				Log.e(TAG, "Could not get current MatchGameFragment fragment");
			}
		}

		if (type != FACE_MATCH_TIMED_GAME
				&& this.gameFragmentCounter >= QUIZES_COUNT) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException ie) {
				Log.e(TAG, "Interrupted");
			}
			this.finishGame();
			return;
		}

		MatchGame.GameSet nextSet = this.mGame.getNextGameSet();
		this.constructAndShowNext(nextSet, type);
	}

	private void finishGame() {

		this.timerCancelled = true;
		this.gameInProgress = 0;
		String scoreMessage = "Your Score: "
				+ (this.currentScore + this.timeBonus);
		// SHOW SCORE
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View dialogView = this.getLayoutInflater().inflate(R.layout.complete_dialog, null);
		TextView scoreView = (TextView) dialogView.findViewById(R.id.gameover_text);
		scoreView.setText(scoreMessage);
		builder.setView(dialogView);	
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						FaceMatchActivity.this.goToTopMenu();
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void constructAndShowNext(MatchGame.GameSet set, int type) {
		this.gameFragmentCounter++;
		final MatchGameFragment nextFragment;

		if (type == NAME_MATCH_GAME) {
			Drawable thumb = getResources().getDrawable(
					getResources().getIdentifier(set.URLs[set.indexMe],
							"drawable", this.getPackageName()));
			nextFragment = MatchGameFragment.instanceOf(thumb, set.peopleNames,
					set.indexMe, this.currentScore);
			this.updateCurrentFragment(nextFragment);
		} else if (type == FACE_MATCH_GAME || type == FACE_MATCH_TIMED_GAME) {
			Drawable[] thumbs = new Drawable[OPTIONS_COUNT];
			for (int d = 0; d < OPTIONS_COUNT; d++) {
				thumbs[d] = getResources().getDrawable(
						getResources().getIdentifier(set.URLs[d], "drawable",
								this.getPackageName()));
			}
			boolean timer = type == FACE_MATCH_TIMED_GAME ? true : false;
			nextFragment = MatchGameFragment.instanceOf(thumbs,
					set.peopleNames[set.indexMe], timer, set.indexMe,
					this.currentScore);
			this.updateCurrentFragment(nextFragment);
		}
	}

	private void updateCurrentFragment(final MatchGameFragment nextFragment) {
		Handler fragSwapper = new Handler();
		fragSwapper.postDelayed(new Runnable() {
			@Override
			public void run() {
				final FragmentTransaction fragmentTransaction = mFragmentManager
						.beginTransaction();
				fragmentTransaction.setCustomAnimations(R.animator.fade_in,
						R.animator.fade_out);
				fragmentTransaction.replace(R.id.ui_fragment_container,
						nextFragment, "CURRENT");
				fragmentTransaction.commit();
			}
		}, 1000L);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behaviour of controls going
	 * away while interacting with activity UI.
	 */
	/*
	 * View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener()
	 * {
	 * 
	 * @Override public boolean onTouch(View view, MotionEvent motionEvent) { if
	 * (AUTO_HIDE) { delayedHide(AUTO_HIDE_DELAY_MILLIS); } return
	 * view.performClick(); //return false; } };
	 * 
	 * Handler mHideHandler = new Handler(); Runnable mHideRunnable = new
	 * Runnable() {
	 * 
	 * @Override public void run() { mSystemUiHider.hide();
	 * getActionBar().hide(); } };
	 */

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	/*
	 * private void delayedHide(int delayMillis) {
	 * mHideHandler.removeCallbacks(mHideRunnable);
	 * mHideHandler.postDelayed(mHideRunnable, delayMillis); }
	 */

	/*
	 * 
	 * private void updateAndShowScore () { // TODO get current score and append
	 * to the total score, check if // we earn any bonus/badges }
	 * 
	 * private void showLeaderboard () { // TODO }
	 */
	
	/*
	 * TimerTask takes care of timed game and timer update in timed Face Matching Game
	 */
	class TimerTask extends AsyncTask<Integer, Integer, Void> {

		@Override
		protected void onPostExecute(Void result) {
			if (FaceMatchActivity.this.gameInProgress == FACE_MATCH_TIMED_GAME) {
				FaceMatchActivity.this.finishGame();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// decrement time bonus
			FaceMatchActivity.this.timeBonus = values[0];
			if (FaceMatchActivity.this.timeBonus < 0)
				FaceMatchActivity.this.timeBonus = 0;
			//Log.d(TAG, "Current Time bonus: " + FaceMatchActivity.this.timeBonus);
			//Log.d(TAG, "Current Timer secs: " + values[1]);
			if (FaceMatchActivity.this.gameInProgress == FACE_MATCH_TIMED_GAME && values[1] > 0) {
				Intent intent = new Intent(MatchGameFragment.TIMERCHANGE_INTENT);
				intent.putExtra("timer", values[1]);
				LocalBroadcastManager.getInstance(FaceMatchActivity.this).sendBroadcast(intent);
			}
		}

		@Override
		protected Void doInBackground(Integer... params) {
			int timerSeconds = params[0];
			for (int t = 0; t <= params[0]; t++) {
				if (FaceMatchActivity.this.timerCancelled)
					break;
				try {
					Thread.sleep(1000L); // one second tick
				} catch (InterruptedException ie) {
					Log.d(TAG, "Timer Thread was interupted");
				}
				timerSeconds--;
				int progress = (int) ((1.0f - (t / (float) params[0])) * FaceMatchActivity.MAX_TIME_BONUS);
				Log.d(TAG, "Progress is " + progress);

				this.publishProgress(progress, timerSeconds);
			}
			return null;

		}

	}

	/*
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

	
	/*
	 * TODO Settings
	 */

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// TODO Handle Settings
		Log.d(TAG, "Received data from Configuration Dialog");
		boolean soundsOn = dialog.getArguments().getBoolean("sounds");
		String on = soundsOn ? "Sounds On" : "Sounds Off";
		Log.d(TAG, on);
				
	}
	
	

}
