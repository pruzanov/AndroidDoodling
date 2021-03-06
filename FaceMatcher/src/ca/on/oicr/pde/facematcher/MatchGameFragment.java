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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Arguably, the most important class as it creates the view
 * for a single game view for all types of games
 */
public class MatchGameFragment extends Fragment {
	
	private static final int[] image_ids = { R.id.face_thumbnail_1,	R.id.face_thumbnail_2,
		                                     R.id.face_thumbnail_3, R.id.face_thumbnail_4 };
	OnAnswerSelectedListener mCallback;
	private Drawable[] faceThumbnails;
	private String name;
	private Drawable faceThumbnail;
	private String[] names;
	private int score;
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	// Supported game type
	private int gameType;
	private int rightAnswer;

	public int getRightAnswer() {
		return rightAnswer;
	}

	public void setRightAnswer(int rightAnswer) {
		this.rightAnswer = rightAnswer;
	}

	public int getGameType() {
		return gameType;
	}

	public void setGameType(int gameType) {
		this.gameType = gameType;
	}

	// FaceMatchFragment version
	public static MatchGameFragment instanceOf(Drawable[] faces, String name,
			boolean timer, int rightIdx, int score) {
		MatchGameFragment fragment = new MatchGameFragment();
		fragment.setFaceThumbnails(faces);
		fragment.setName(name);
		fragment.setRightAnswer(rightIdx);
		fragment.setScore(score);
		Log.d(FaceMatchActivity.TAG, "Score set to " + score);
		if (timer) {
			fragment.setGameType(FaceMatchActivity.FACE_MATCH_TIMED_GAME);
		} else {
			fragment.setGameType(FaceMatchActivity.FACE_MATCH_GAME);
		}
		return fragment;
	}

	// NameMatchFragment version
	public static MatchGameFragment instanceOf(Drawable face, String[] names,
			int rightIdx, int score) {
		MatchGameFragment fragment = new MatchGameFragment();
		fragment.setFaceThumbnail(face);
		fragment.setOptions(names);
		fragment.setGameType(FaceMatchActivity.NAME_MATCH_GAME);
		fragment.setRightAnswer(rightIdx);
		fragment.setScore(score);
		Log.d(FaceMatchActivity.TAG, "Score set to " + score);
		return fragment;
	}

	// Container Activity must implement this interface
	public interface OnAnswerSelectedListener {
		public void onAnswerSelected(int option);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View fragmentView;
		if (this.getGameType() == FaceMatchActivity.NAME_MATCH_GAME) {
			fragmentView =  onCreateNameMatchView(inflater, container,
					        savedInstanceState);
		} else if (this.getGameType() == FaceMatchActivity.FACE_MATCH_GAME) {
			fragmentView =  onCreateFaceMatchView(inflater, container,
					        savedInstanceState, false);
		} else if (this.getGameType() == FaceMatchActivity.FACE_MATCH_TIMED_GAME) {
			fragmentView =  onCreateFaceMatchView(inflater, container,
					        savedInstanceState, true);
		} else {
			return null;
		}

		return fragmentView;
	}

	/*
	 * onCreateView() methods for FaceMatchFragment
	 */
	public View onCreateFaceMatchView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState, boolean timed) {
		View rootView = inflater.inflate(R.layout.face_match_fragment,
				                         container, false);
        if (timed) {
        	ImageView banner = (ImageView) rootView.findViewById(R.id.banner_face_match);
        	banner.setImageResource(R.drawable.banner_game03);
        	banner.setBackgroundResource(R.color.game3_color);
        }
        
		for (int i = 0; i < image_ids.length; i++) {
			ImageView thumb = (ImageView) rootView.findViewById(image_ids[i]);
			thumb.setImageDrawable(this.faceThumbnails[i]);
			thumb.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int thumbID = v.getId();
					int option = -1;
					switch (thumbID) {
					case R.id.face_thumbnail_1:
						option = 0;
						break;
					case R.id.face_thumbnail_2:
						option = 1;
						break;
					case R.id.face_thumbnail_3:
						option = 2;
						break;
					case R.id.face_thumbnail_4:
						option = 3;
						break;
					default:
						break;
					};
				
					mCallback.onAnswerSelected(option);
					return v.performClick();
				}
			});
		}

		TextView p_name = (TextView) rootView.findViewById(R.id.person_name);
		p_name.setText(this.name);

		return rootView;
	}

	/*
	 * onCreateView() method for NameMatchFragment
	 */
	public View onCreateNameMatchView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.name_match_fragment,
				container, false);
		ImageView thumb = (ImageView) rootView
				.findViewById(R.id.face_thumbnail);
		thumb.setImageDrawable(this.faceThumbnail);
		RadioGroup rg = (RadioGroup) rootView.findViewById(R.id.name_options);
		for (int i = 0; i < FaceMatchActivity.OPTIONS_COUNT; i++) {
			RadioButton rb = (RadioButton) rg.getChildAt(i);
			rb.setText(this.names[i]);
		}

		return rootView;
	}

	// Methods for initialisation
	public void setFaceThumbnails(Drawable[] faces) {
		this.faceThumbnails = faces;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFaceThumbnail(Drawable face) {
		this.faceThumbnail = face;
	}

	public void setOptions(String[] names) {
		this.names = names;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnAnswerSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnChoiceSelectedListener");
		}
	}

	/**
	 * This methods reveals the right (and wrong, if answered incorrectly) quiz answer(s)
	 * @param index of the answer
	 */
	public void showAnswers(int answer) {
		// Show Yes, I'm and mark right answer with green bg
		if (this.getGameType() == FaceMatchActivity.NAME_MATCH_GAME) {
			ImageView feedbackView = (ImageView) getView().findViewById(
					             R.id.answer_feedback);
			int [] optionsIDs = {R.id.name_option_1, R.id.name_option_2,
						         R.id.name_option_3, R.id.name_option_4};
			RadioButton rb_answer = (RadioButton) getView().findViewById(optionsIDs[answer]);
			if (answer == this.getRightAnswer()) {
				feedbackView.setImageDrawable(getResources().getDrawable(
						R.drawable.msg_check));	
				rb_answer.setBackgroundColor(getResources().getColor(R.color.right_answer));
			} else {
				feedbackView.setImageDrawable(getResources().getDrawable(
						R.drawable.msg_x));
				rb_answer.setBackgroundColor(getResources().getColor(R.color.wrong_answer));
				RadioButton rb_right_answer = (RadioButton) getView()
						.findViewById(optionsIDs[this.getRightAnswer()]);
				rb_right_answer.setBackgroundColor(getResources().getColor(R.color.right_answer));
			}
		} else {
			int [] thumbIDs =    {R.id.face_thumbnail_1, R.id.face_thumbnail_2,
		                          R.id.face_thumbnail_3, R.id.face_thumbnail_4};
			int [] feedbackIDs = {R.id.feedback_icon_1,  R.id.feedback_icon_2,
					              R.id.feedback_icon_3,  R.id.feedback_icon_4};
			for (int i = 0; i < FaceMatchActivity.OPTIONS_COUNT; i++) {
				ImageView thumbnailView = (ImageView) getView().findViewById(
						thumbIDs[i]);
				ImageView feedbackView = (ImageView) getView().findViewById(
						feedbackIDs[i]);
				if (i == answer) {
				   feedbackView.setVisibility(View.VISIBLE);
				   if (answer == this.getRightAnswer()) {  
					 feedbackView.setImageDrawable(getResources()
							                      .getDrawable(R.drawable.ic_check));	 
					 continue;  
				   } else {
					 feedbackView.setImageDrawable(getResources()
							                      .getDrawable(R.drawable.ic_x));
				   }
				} else if (i == this.getRightAnswer()) {
					feedbackView.setImageDrawable(getResources()
		                      .getDrawable(R.drawable.ic_check));
                    feedbackView.setVisibility(View.VISIBLE);
                    continue;
				}
				thumbnailView.setAlpha(0.3f);
			}
		  }

		}
	
}
