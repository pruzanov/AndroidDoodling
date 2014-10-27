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
public class FaceMatchActivity extends Activity implements
		TopMenuFragment.OnOptionSelectedListener,
		NameMatchFragment.OnNameSelectedListener,
		FaceMatchFragment.OnFaceSelectedListener {
	
    // Game Parameters:
	protected static final int OPTIONS_COUNT = 4;
	protected static final int QUIZES_COUNT  = 10;
	protected static final String TAG = "FaceMatcher";
	// Supported Game types:
	private static final int NAME_MATCH_GAME = 1;
	private static final int FACE_MATCH_GAME = 2;
	private static final int FACE_MATCH_TIMED_GAME = 3;
	private FragmentManager mFragmentManager;
	private MatchGame mGame;
	private boolean gameInProgress;

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
		gameInProgress = false;
		Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		mFragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();
		fragmentTransaction.
		        //addToBackStack("TopMenu").
		        add(R.id.ui_fragment_container,
				new TopMenuFragment());
		fragmentTransaction.commit();
		
		getActionBar().hide();
        int deniroID = getResources().getIdentifier("deniro", "drawable", this.getPackageName());
		
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
		if (this.gameInProgress) {
			
			FragmentTransaction fragmentTransaction = getFragmentManager()
					                                  .beginTransaction();
			fragmentTransaction.replace(R.id.ui_fragment_container,
					                    new TopMenuFragment());
			fragmentTransaction.commit();
			this.gameInProgress = false;
		} else {
			super.onBackPressed();
		}
	}
	
	protected void addGameData(OicrPerson[] data) {
	   this.mGame = new MatchGame(data);
	   try {
	     Thread.sleep(300);
	   } catch (InterruptedException IE) {
		 Log.e(TAG, "Data producing thread was interrupted");  
	   }
	}

	/*
	 * (non-Javadoc)
	 * @see ca.on.oicr.pde.facematcher.TopMenuFragment.OnOptionSelectedListener#onOptionSelected(int)
	 * Method for communicating with Top Menu Fragment
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
			// TODO launch game of first type
			Log.d(TAG, "Would have switched to NameMatching Game");
			// mFragmentManager = getFragmentManager();
			
			break;
		case TopMenuFragment.FACE_MATCH:
			// TODO launch game of second type
			Log.d(TAG, "Would have switched to FaceMatching Game");
			/*
			 * DEBUGGING: building a dummy fragment with test data
			 */
			Drawable[] faces = {getResources().getDrawable(R.drawable.deniro), getResources().getDrawable(R.drawable.billgatesad),
					            getResources().getDrawable(R.drawable.muhammadali), getResources().getDrawable(R.drawable.johncarmack)};
			String name = "Robert De Niro";
			FaceMatchFragment mockFragment2 = FaceMatchFragment.instanceOf(faces, name);
			FragmentTransaction fragmentTransaction2 = getFragmentManager().beginTransaction();
			fragmentTransaction2.replace(R.id.ui_fragment_container, mockFragment2);
			fragmentTransaction2.addToBackStack("TopMenu");
			fragmentTransaction2.commit();
			break;
		case TopMenuFragment.TIMED_MATCH:
			// TODO launch game of third type:
			Log.d(TAG, "Would have switched to TimedMatching Game");
			/*
			 * DEBUGGING: building a dummy fragment with test data
			 */
			String[] names = {"Che Gevara","Robert De Niro","Pope Francis","Bill Gates"};
			NameMatchFragment mockFragment = NameMatchFragment.instanceOf(getResources().getDrawable(R.drawable.deniro), names);
			mFragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = mFragmentManager .beginTransaction();	
			fragmentTransaction.replace(R.id.ui_fragment_container, mockFragment,"current");
			fragmentTransaction.addToBackStack("TopMenu");
			fragmentTransaction.commit();
			this.gameInProgress = true;
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
	 * @see ca.on.oicr.pde.facematcher.NameMatchFragment.OnNameSelectedListener#onNameSelected(int)
	 * Method for interaction with Name Matching Fragment
	 */

	@Override
	public void onNameSelected(int option) {
		// TODO implement name-processing code
		Log.d(TAG, "Would handle name selection in Name Matching Game");
		// this.mMatchGame.setup(NAME_MATCH, TRIES);
		// 
	}

	/*
	 * (non-Javadoc)
	 * @see ca.on.oicr.pde.facematcher.FaceMatchFragment.OnFaceSelectedListener#onFaceSelected(int)
	 * Method for interaction with Face Matching Fragment
	 */
	@Override
	public void onFaceSelected(int option) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Would handle face selection in Face Matching Game");
	}
	
	public void onRadioButtonClicked(View v) {
		// Is the button now checked?
		Log.d(TAG, "Radio button clicked in FaceMatchActivity!");
		boolean checked = ((RadioButton) v).isChecked();
		//int option = 0;
		// Check which radio button was clicked
		switch (v.getId()) {
		case R.id.name_option_1:
			if (checked)
			//	option = 1;
			Log.d(FaceMatchActivity.TAG, "Selected Option 1");
			// Option 1 is clicked
			// check game counter, if it's over, finish
			// produce and show the nest fragment
			break;
		case R.id.name_option_2:
			if (checked)
			//	option = 2;
			Log.d(FaceMatchActivity.TAG, "Selected Option 2");
			//
			String[] names = {"Phil Collins","Bruce Lee","Muhammad Ali","Bill Clinton"};
			final NameMatchFragment mockFragment = NameMatchFragment.instanceOf(getResources().getDrawable(R.drawable.muhammadali), names);
			mFragmentManager = getFragmentManager();
			NameMatchFragment nf = (NameMatchFragment) mFragmentManager.findFragmentByTag("current");
			nf.showAnswers();
			final FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
			Handler fragSwapper = new Handler();
			fragSwapper.postDelayed(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						fragmentTransaction.replace(R.id.ui_fragment_container, mockFragment);
				        fragmentTransaction.commit();
					}}, 1000L);
			// Option 2 is clicked
			break;
		case R.id.name_option_3:
			if (checked)
			//	option = 3;
			Log.d(FaceMatchActivity.TAG, "Selected Option 3");
			// Option 3 is clicked
			break;
		case R.id.name_option_4:
			if (checked)
			//	option = 4;
			Log.d(FaceMatchActivity.TAG, "Selected Option 4");
			// Option 4 is clicked
			break;
		}
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

	private void startGame (int type) {
		if (null == this.mGame) {
		    Log.e(TAG,"Game was not setup properly, won't start");
			return;
		}
		
		this.gameInProgress = true;
		MatchGame.GameSet firstSet = this.mGame.getNextGameSet();
		// TODO create and show proper fragment, start timer task and set score to initial value
	}
	
	private void updateGame() {
		// TODO get next GameSet, create proper fragment, reveal answers for current quiz
		// if game is up, finish game
	}

	private void finishGame() {
		// TODO
		this.gameInProgress = false;
		// update score, show dialog and help reset to top menu
	}
	
	private void constructAndShowNext (MatchGame.GameSet set) {
		// TODO construct proper fragment and replace current one with the new one
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
	 * private void startGame (int gameType) { // TODO read, randomize and
	 * present data using ArrayList }
	 * 
	 * private void updateAndShowScore () { // TODO get current score and append
	 * to the total score, check if // we earn any bonus/badges }
	 * 
	 * private void showLeaderboard () { // TODO }
	 */

}
