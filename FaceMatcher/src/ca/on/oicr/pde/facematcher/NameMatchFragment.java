package ca.on.oicr.pde.facematcher;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class NameMatchFragment extends Fragment {
	public static final int OPTIONS_COUNT = 4;
	OnNameSelectedListener mCallback;
    private Drawable faceThumbnail;
    private String[] names;
    
    public static NameMatchFragment instanceOf(Drawable face, String[] names) {
    	NameMatchFragment fragment = new NameMatchFragment();
    	fragment.setFaceThumbnail(face);
    	fragment.setOptions(names);
    	
    	return fragment;
    }
    
    // Container Activity must implement this interface
    public interface OnNameSelectedListener {
        public void onNameSelected(int option);
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView =   inflater.inflate(R.layout.name_match_fragment, container, false);
		ImageView thumb = (ImageView) rootView.findViewById(R.id.face_thumbnail);
		thumb.setImageDrawable(this.faceThumbnail);
		RadioGroup rg = (RadioGroup) rootView.findViewById(R.id.name_options);
		for (int i = 0; i < OPTIONS_COUNT; i++) {
			RadioButton rb = (RadioButton) rg.getChildAt(i);
			rb.setText(this.names[i]);
		}
		
		return rootView;
	}
	
	public void onRadioButtonClicked(View v){
		// Is the button now checked?
		Log.d(FaceMatchActivity.TAG,"Radio button clicked in NameMatchFragment!");
	    boolean checked = ((RadioButton) v).isChecked();
	    int option = 0;
	    // Check which radio button was clicked
	    switch(v.getId()) {
	        case R.id.name_option_1:
	            if (checked)
	            	option = 1;
	                Log.d(FaceMatchActivity.TAG,"Selected Option 1");
	                // Option 1 is clicked
	            break;
	        case R.id.name_option_2:
	            if (checked)
	            	option = 2;
	                Log.d(FaceMatchActivity.TAG,"Selected Option 2");
	                // Option 2 is clicked
	            break;
	        case R.id.name_option_3:
	            if (checked)
	            	option = 3;
	                Log.d(FaceMatchActivity.TAG,"Selected Option 3");
	                // Option 3 is clicked
	            break;
	        case R.id.name_option_4:
	            if (checked)
	            	option = 4;
	                Log.d(FaceMatchActivity.TAG,"Selected Option 4");
	                // Option 4 is clicked
	            break;
	    }
        if (option > 0)
	      	mCallback.onNameSelected(option);
	}
	
	// Mehods for initialization
	public void setFaceThumbnail (Drawable face) {
		this.faceThumbnail = face;
	}
	
	public void setOptions (String[] names) {
		this.names = names;
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnNameSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNameSelectedListener");
        }
    }

}
