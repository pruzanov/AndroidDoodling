package ca.on.oicr.pde.facematcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

public class ConfigureDialogFragment extends DialogFragment {
	
	private boolean soundsEnabled;
	private String userName;
	
	public boolean isSoundsEnabled() {
		return soundsEnabled;
	}

	public String getUserName() {
		return userName;
	}
	
	// Use this instance of the interface to deliver action events
    ConfigureDialogListener mListener;
    
	public interface ConfigureDialogListener {
		public void onDialogPositiveClick(ConfigureDialogFragment dialog);
	}
	
	
	@SuppressLint("InflateParams")
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View rootView = inflater.inflate(R.layout.configure_dialog, null);  	
    	SharedPreferences sp = getActivity().getSharedPreferences(FaceMatchActivity.GAME_PREFS, Activity.MODE_PRIVATE);
    	
	    builder.setView(rootView)
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       mListener.onDialogPositiveClick(ConfigureDialogFragment.this);
                   }
               });
	    CheckBox cb = (CheckBox) rootView.findViewById(R.id.volume_toggle);
	    cb.setChecked(sp.getBoolean(FaceMatchActivity.GAME_SOUND, true));
	    cb.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Log.d(FaceMatchActivity.TAG, "Checkbox clicked!");
				soundsEnabled = soundsEnabled ? false : true;
				
			}});
    	EditText un = (EditText) rootView.findViewById(R.id.user_name);
    	un.setText(sp.getString(FaceMatchActivity.GAME_USER, FaceMatchActivity.DEFAULT_USER));
    	userName = un.getText().toString();
    	un.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				userName = s.toString();
				
			}});
        return builder.create();
    }

	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (ConfigureDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ConfigureDialogListener");
        }
    }

}
