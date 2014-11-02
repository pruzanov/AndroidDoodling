package ca.on.oicr.pde.facematcher;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import ca.on.oicr.pde.facematcher.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FaceMatchActivity extends Activity
		implements
		TopMenuFragment.OnOptionSelectedListener,
		MatchGameFragment.OnChoiceSelectedListener {

	// Game Parameters:
	protected static final int OPTIONS_COUNT = 4;
	protected static final int QUIZES_COUNT = 5;
	protected static final String TAG = "FaceMatcher";
	// Supported Game types:
	public static final int NAME_MATCH_GAME = 1;
	public static final int FACE_MATCH_GAME = 2;
	public static final int FACE_MATCH_TIMED_GAME = 3;
	// Special bonus for speed
	private static final int MAX_TIME_BONUS = 200;
	private FragmentManager mFragmentManager;
	private MatchGame mGame;
	private int gameInProgress;
	private int currentScore;
	private int timeBonus;
	private int gameFragmentCounter;

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
		gameInProgress = 0;
		Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		mFragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();
		fragmentTransaction.
		// addToBackStack("TopMenu").
				add(R.id.ui_fragment_container, new TopMenuFragment());
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
		// TODO Add a dialog asking user to confirm exit to the top menu
		if (this.gameInProgress > 0) {

			FragmentTransaction fragmentTransaction = getFragmentManager()
					.beginTransaction();
			fragmentTransaction.replace(R.id.ui_fragment_container,
					new TopMenuFragment());
			fragmentTransaction.commit();
			this.gameInProgress = 0;
		} else {
			super.onBackPressed();
		}
	}

	/*
	 * Called from asynchronous task, adds validated data
	 */
	protected void addGameData(OicrPerson[] data) {
		this.mGame = new MatchGame(data);
		// TODO show dialog, dismiss on load
		try {
			Thread.sleep(300);
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
			// TODO show about info in a dialog
			Log.d(TAG, "Would Have Shown About Info");
			break;
		case TopMenuFragment.SET_OPTIONS:
			// set options (in a dialog?) TODO: consider setting user
			// credentials via options menu
			Log.d(TAG, "Would Have Open Settings Dialog");
			break;
		case TopMenuFragment.SHOW_LEADERS:
			// TODO launch game of third type:
			Log.d(TAG,
					"Would Have Switched to LeaderBoard Fragment, if possible");
			break;
		case TopMenuFragment.NAME_MATCH:
			Log.d(TAG, "Starting NameMatching Game");
			this.gameInProgress = NAME_MATCH_GAME;
			this.startGame(this.gameInProgress);
			break;
		case TopMenuFragment.FACE_MATCH:
			// TODO launch game of second type
			Log.d(TAG, "Would have switched to FaceMatching Game");
			/*
			 * DEBUGGING: building a dummy fragment with test data
			 */
			Drawable[] faces = { getResources().getDrawable(R.drawable.deniro),
					getResources().getDrawable(R.drawable.billgatesad),
					getResources().getDrawable(R.drawable.muhammadali),
					getResources().getDrawable(R.drawable.johncarmack) };
			String name = "Robert De Niro";
			MatchGameFragment mockFragment2 = MatchGameFragment.instanceOf(
					faces, name, false);
			FragmentTransaction fragmentTransaction2 = getFragmentManager()
					.beginTransaction();
			fragmentTransaction2.replace(R.id.ui_fragment_container,
					mockFragment2);
			fragmentTransaction2.commit();
			this.gameInProgress = FACE_MATCH_GAME;
			break;
		case TopMenuFragment.TIMED_MATCH:
			// TODO launch game of third type:
			Log.d(TAG, "Would have switched to TimedMatching Game");
			/*
			 * DEBUGGING: building a dummy fragment with test data
			 */
			Drawable[] faces2 = {
					getResources().getDrawable(R.drawable.deniro),
					getResources().getDrawable(R.drawable.billgatesad),
					getResources().getDrawable(R.drawable.muhammadali),
					getResources().getDrawable(R.drawable.johncarmack) };
			String name2 = "Robert De Niro";
			MatchGameFragment mockFragment = MatchGameFragment.instanceOf(
					faces2, name2, true);
			FragmentTransaction fragmentTransaction = getFragmentManager()
					.beginTransaction();
			fragmentTransaction.replace(R.id.ui_fragment_container,
					mockFragment);
			fragmentTransaction.commit();
			this.gameInProgress = FACE_MATCH_TIMED_GAME;
			/*
			 * DEBUGGING ENDS
			 */
			break;
		default:
			break;
		}
		;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.on.oicr.pde.facematcher.MatchGameFragment.OnOptionSelectedListener#
	 * onOptionSelected(int) Method for interaction with MatchGameFragment
	 */
	@Override
	public void onChoiceSelected(int index) {

		Log.d(TAG, "Would handle face thumb with array index " + index
				+ " in Face Matching Game");
		try {
			MatchGameFragment current = (MatchGameFragment) getFragmentManager()
					.findFragmentByTag("CURRENT");
			current.showAnswers(index);
			Log.d(TAG, "Showing answers, would update scores");
			this.updateGame(this.gameInProgress, index);
		} catch (Exception e) {
			Log.e(TAG, "Could not get current MatchGameFragment fragment");
		}

	}

	public void onRadioButtonClicked(View v) {
		// Is the button now checked?
		Log.d(TAG, "Radio button clicked in FaceMatchActivity!");
		boolean checked = ((RadioButton) v).isChecked();
		int option = 0;
		// Check which radio button was clicked
		switch (v.getId()) {
		case R.id.name_option_1:
			if (checked)
				option = 0;
			Log.d(FaceMatchActivity.TAG, "Selected Option 1");
			break;
		case R.id.name_option_2:
			if (checked)
				option = 1;
			Log.d(FaceMatchActivity.TAG, "Selected Option 2");
			break;
		case R.id.name_option_3:
			if (checked)
				option = 2;
			Log.d(FaceMatchActivity.TAG, "Selected Option 3");
			break;
		case R.id.name_option_4:
			if (checked)
				option = 3;
			Log.d(FaceMatchActivity.TAG, "Selected Option 4");
			break;
		}
		
		this.updateGame(this.gameInProgress, option);

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
		this.updateGame(type, -1);
	}

	private void updateGame(int type, int answer) {

		if (type != FACE_MATCH_TIMED_GAME
				&& this.gameFragmentCounter >= QUIZES_COUNT) {
			this.finishGame();
			return;
		}
		// Reveal answers if user made a choice
		if (answer >= 0) {
			try {
				MatchGameFragment current = (MatchGameFragment) getFragmentManager()
						.findFragmentByTag("CURRENT");
				current.showAnswers(answer);
				Log.d(TAG, "Showing answers, would updates scores");
			} catch (Exception e) {
				Log.e(TAG, "Could not get current MatchGameFragment fragment");
			}
		}
		MatchGame.GameSet nextSet = this.mGame.getNextGameSet();
		this.constructAndShowNext(nextSet, type);
	}

	private void finishGame() {
		Log.d(TAG, "Finishing game, would show score");
		this.onBackPressed();
		// TODO update score, show dialog and help reset to top menu
	}

	private void constructAndShowNext(MatchGame.GameSet set, int type) {
		this.gameFragmentCounter++;
		final MatchGameFragment nextFragment;
		
		if (type == NAME_MATCH_GAME) {
			Drawable thumb = getResources().getDrawable(
					getResources().getIdentifier(set.URLs[set.indexMe],
							"drawable", this.getPackageName()));
			nextFragment = MatchGameFragment.instanceOf(thumb, set.peopleNames);
			this.updateCurrentFragment(nextFragment);
		} else if (type == FACE_MATCH_GAME || type == FACE_MATCH_TIMED_GAME) {
			Drawable[] thumbs = new Drawable[OPTIONS_COUNT];
			for (int d = 0; d < OPTIONS_COUNT; d++) {
				thumbs[d] = getResources().getDrawable(
						getResources().getIdentifier(set.URLs[d], "drawable",
								this.getPackageName()));
			}
			boolean timer = type == FACE_MATCH_TIMED_GAME ? true : false;
			nextFragment = MatchGameFragment.instanceOf(
					thumbs, set.peopleNames[set.indexMe], timer);
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

}
