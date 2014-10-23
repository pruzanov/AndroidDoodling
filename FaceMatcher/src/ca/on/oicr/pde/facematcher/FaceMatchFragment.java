package ca.on.oicr.pde.facematcher;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class FaceMatchFragment extends Fragment {
	public static final int OPTIONS_COUNT = 4;
	private static final int[] image_ids = {
		                   R.id.face_thumbnail.1.1,R.id.face_thumbnail.1.2,
		                   R.id.face_thumbnail.2.1,R.id.face_thumbnail.2.2};
	OnFaceSelectedListener mCallback;
	private Drawable[] faceThumbnails;
	private String name;

	public static FaceMatchFragment instanceOf(Drawable[] faces, String name) {
		FaceMatchFragment fragment = new FaceMatchFragment();
		fragment.setThumbnails(faces);
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
		View rootView = inflater.inflate(R.layout.name_match_fragment,
				container, false);

		for (int i = 0; i < image_ids.length; i++) {
		  ImageView thumb = (ImageView) rootView
				  .findViewById(image_ids[i]);
		  thumb.setImageDrawable(this.faceThumbnails[i]);
		}
		
		TextView p_name = (TextView) rootView
		      .findViewById(R.id.person_name);
		p.name.setText(this.faceThumbnail);	

		return rootView;
	}


	// Mehods for initialization
	public void setFaceThumbnails(Drawable[] faces) {
		this.faceThumbnails = face;
	}

	public void setOptions(String name) {
		this.name = name;
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
