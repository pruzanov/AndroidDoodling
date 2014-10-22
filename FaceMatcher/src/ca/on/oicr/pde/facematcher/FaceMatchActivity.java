package ca.on.oicr.pde.facematcher;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import ca.on.oicr.pde.facematcher.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FaceMatchActivity extends Activity 
    implements TopMenuFragment.OnOptionSelectedListener {
	
	protected static final String TAG = "FaceMatcher";
	private FragmentManager  mFragmentManager;
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	//private static final boolean AUTO_HIDE = true;
	
	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	//private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	//private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	//private static final int HIDER_FLAGS = SystemUiHider.FLAG_FULLSCREEN;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	//private SystemUiHider mSystemUiHider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, getClass().getSimpleName() + ":entered onCreate()");

		super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        String[] names = {"Che Gevara","Robert De Niro","Pope Francis","Bill Gates"};
        NameMatchFragment mockFragment = NameMatchFragment.instanceOf(
        		                         getResources().getDrawable(R.drawable.deniro), 
        		                         names);
        
        mFragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();
		fragmentTransaction.add(R.id.ui_fragment_container, mockFragment);
		fragmentTransaction.commit();

		//final View controlsView = findViewById(R.id.fullscreen_content_controls);
		//final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		//mSystemUiHider = SystemUiHider.getInstance(this, controlsView,
		//		HIDER_FLAGS);
		
		//mSystemUiHider.setup();
		 
		
		/*mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});
		*/
				

		// Set up the user interaction to manually show or hide the system UI.
		/*controlsView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		}); */

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		//findViewById(R.id.dummy_button1).setOnTouchListener(
		//		mDelayHideTouchListener);
				
	}

	@Override
	public void onOptionSelected(int position) {
		switch (position) {
		case TopMenuFragment.SHOW_ABOUT:
			//TODO show about info in a dialog
			Log.d(TAG, "Would Have Shown About Info");
			break;
		case TopMenuFragment.SET_OPTIONS:
			//set options (in a dialog?) TODO: consider setting user credentials via options menu
			Log.d(TAG, "Would Have Open Settings Dialog");
			break;
		case TopMenuFragment.SHOW_LEADERS:
			//TODO launch game of third type:
			Log.d(TAG, "Would Have Switched to LeaderBoard Fragment, if possible");
			break;
		case TopMenuFragment.NAME_MATCH:
			//TODO launch game of first type
			Log.d(TAG, "Would have switched to NameMatching Game");
			//mFragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = mFragmentManager
					.beginTransaction();
			fragmentTransaction.add(R.id.ui_fragment_container, new NameMatchFragment());
			fragmentTransaction.commit();
			break;
		case TopMenuFragment.FACE_MATCH:
			//TODO launch game of second type
			Log.d(TAG, "Would have switched to FaceMatching Game");
			break;
		case TopMenuFragment.TIMED_MATCH:
			//TODO launch game of third type:
			Log.d(TAG, "Would have switched to TimedMatching Game");
		default:
			break;
		};
	}	
	    

	//@Override
	/*protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	} */

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behaviour of controls going away
	 * while interacting with activity UI.
	 */
	/*View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return view.performClick();
			//return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
			getActionBar().hide();
		}
	};*/

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	/*private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}*/
	
	/*
	private void startGame (int gameType) {
		// TODO read, randomize and present data using ArrayList
	}
	
	private void updateAndShowScore () {
		// TODO get current score and append to the total score, check if 
		// we earn any bonus/badges
	}
	
	private void showLeaderboard () {
		// TODO 
	}
	*/
	
}
