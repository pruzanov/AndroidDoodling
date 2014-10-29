package ca.on.oicr.pde.facematcher;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FaceMatchFragment extends Fragment {
	private static final int[] image_ids = {
		                   R.id.face_thumbnail_1, R.id.face_thumbnail_2,
		                   R.id.face_thumbnail_3,R.id.face_thumbnail_4};
	OnFaceSelectedListener mCallback;
	private Drawable[] faceThumbnails;
	private String name;

	public static FaceMatchFragment instanceOf(Drawable[] faces, String name) {
		FaceMatchFragment fragment = new FaceMatchFragment();
		fragment.setFaceThumbnails(faces);
		fragment.setName(name);

		return fragment;
	}

	// Container Activity must implement this interface
	public interface OnFaceSelectedListener {
		public void onFaceSelected(int option);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.face_match_fragment,
				container, false);

		for (int i = 0; i < image_ids.length; i++) {
		  ImageView thumb = (ImageView) rootView
				  .findViewById(image_ids[i]);
		  thumb.setImageDrawable(this.faceThumbnails[i]);
		  thumb.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				mCallback.onFaceSelected(v.getId());
				return v.performClick();
			}
		});
		}
		
		TextView p_name = (TextView) rootView
		      .findViewById(R.id.person_name);
		p_name.setText(this.name);	

		return rootView;
	}


	// Mehods for initialization
	public void setFaceThumbnails(Drawable[] faces) {
		this.faceThumbnails = faces;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnFaceSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFaceSelectedListener");
		}
	}
	
	public void showAnswers() {
		//TODO show correct (and user's answer if it is incorrect)
		Log.d(FaceMatchActivity.TAG,"Would have shown the answers");
	}

}
