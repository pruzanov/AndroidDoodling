package ca.on.oicr.pde.facematcher;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScoresContainerFragment extends Fragment {
	private int currentType = 1;
	private static int[] supportedGameTypes = {
			FaceMatchActivity.NAME_MATCH_GAME,
			FaceMatchActivity.FACE_MATCH_GAME,
			FaceMatchActivity.FACE_MATCH_TIMED_GAME };

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.score_container_fragment,
				container, false);
		

		if (this.isLayoutLarge(rootView)) {
			FragmentTransaction fragmentTransaction = getFragmentManager()
					.beginTransaction();
			int[] fragmentContainers = { R.id.score_fragment1,
					                     R.id.score_fragment2,
					                     R.id.score_fragment3 };

			for (int f = 0; f < fragmentContainers.length; f++) {
				fragmentTransaction.add(fragmentContainers[f],
						ScoreFragment.instanceOf(supportedGameTypes[f], ScoreFragment.MIN_ROWS));
			}
			fragmentTransaction.commit();
		} else {
			showCurrentTypeScores();
		}

		return rootView;
	}
	
	public void showCurrentTypeScores() {
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.replace(R.id.pager,
				ScoreFragment.instanceOf(this.currentType, ScoreFragment.MAX_ROWS), "CLICKABLE");
		
		if (++this.currentType > supportedGameTypes[supportedGameTypes.length - 1])
			this.currentType = supportedGameTypes[0];
		fragmentTransaction.commit();
	}

	private boolean isLayoutLarge(View root) {
		return root.findViewById(R.id.pager) == null;
	}
}
