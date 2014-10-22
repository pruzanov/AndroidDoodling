package ca.on.oicr.pde.facematcher;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TopMenuFragment extends Fragment {
	public static final int SHOW_ABOUT  = 1;
	public static final int SET_OPTIONS = 2;
	public static final int SHOW_LEADERS= 3;
	public static final int NAME_MATCH  = 4;
	public static final int FACE_MATCH  = 5;
	public static final int TIMED_MATCH = 6;
	
	OnOptionSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnOptionSelectedListener {
        public void onOptionSelected(int position);
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.top_menu_fragment, container, false);
		
		Button selectAboutButton = (Button) rootView.findViewById(R.id.button_show_about);
		selectAboutButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (mCallback != null) {
					mCallback.onOptionSelected(SHOW_ABOUT);
				}
				
			}});
		
		Button selectSettingsButton = (Button) rootView.findViewById(R.id.button_settings);
		selectSettingsButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (mCallback != null) {
					mCallback.onOptionSelected(SET_OPTIONS);
				}
				
			}});
		
		Button selectLeaderBoardButton = (Button) rootView.findViewById(R.id.button_show_leaderboard);
		selectLeaderBoardButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (mCallback != null) {
					mCallback.onOptionSelected(SHOW_LEADERS);
				}
				
			}});
		
		Button nameMatchButton = (Button) rootView.findViewById(R.id.button_matchname);
		nameMatchButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (mCallback != null) {
					mCallback.onOptionSelected(NAME_MATCH);
				}
				
			}});
		
		Button faceMatchButton = (Button) rootView.findViewById(R.id.button_matchface);
		faceMatchButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (mCallback != null) {
					mCallback.onOptionSelected(FACE_MATCH);
				}
				
			}});
		
		Button timedMatchButton = (Button) rootView.findViewById(R.id.button_timedgame);
		timedMatchButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (mCallback != null) {
					mCallback.onOptionSelected(TIMED_MATCH);
				}
				
			}});
		
		return rootView;

	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnOptionSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnOptionSelectedListener");
        }
    }

}
