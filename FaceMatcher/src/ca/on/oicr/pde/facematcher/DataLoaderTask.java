package ca.on.oicr.pde.facematcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

public class DataLoaderTask extends AsyncTask<Void, Void, OicrPerson[]> {

	private WeakReference<FaceMatchActivity> mParent;

	public DataLoaderTask(FaceMatchActivity parent) {
		super();
		mParent = new WeakReference<FaceMatchActivity>(parent);
	}

	@Override
	protected OicrPerson[] doInBackground(Void... params) {
		OicrPerson[] people = null;
		try {
			people = getDataFromFile();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return people;
	}

	@Override
	protected void onPostExecute(OicrPerson[] result) {
		if (null != result && null != mParent.get()) {
			Log.d(FaceMatchActivity.TAG, "Loaded " + result.length
					+ " records for OICR people");
			mParent.get().addGameData(result);
		}
	}

	private OicrPerson[] getDataFromFile(Void... params) throws IOException {

		Resources res = mParent.get().getResources();

		String jsonLine = "";
		String jString = "";

		Log.d(FaceMatchActivity.TAG,
				"Will Load the data from static people.json");
		InputStream fis = res.openRawResource(R.raw.oicr_people);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		while (null != (jsonLine = br.readLine())) {
			jString = jString.concat(jsonLine);
		}
		br.close();
		OicrPerson[] results = getRecordsFromJSON(jString, "people");
		return results;
	}

	public OicrPerson[] getRecordsFromJSON(String JsonString, String type) {
		List<OicrPerson> dataAsList = new ArrayList<OicrPerson>();
		try {
			JSONObject object = (JSONObject) new JSONTokener(JsonString)
					.nextValue();
			JSONArray People = object.getJSONArray(type);
			for (int i = 0; i < People.length(); i++) {
				JSONObject tmp = (JSONObject) People.get(i);
				OicrPerson newPerson = new OicrPerson();
				newPerson.setName(tmp.getString("name"));
				newPerson.setImageURL(tmp.getString("png"));
				try {
					newPerson.imageURL = newPerson.imageURL.substring(0,
							newPerson.imageURL.lastIndexOf(".png"));
					newPerson.setImageID(this.mParent
							.get()
							.getResources()
							.getIdentifier(newPerson.imageURL, "raw", // drawable for debugging, raw for real data
									this.mParent.get().getPackageName()));
					if (newPerson.isValid())
						dataAsList.add(newPerson);
				} catch (NullPointerException npe) {
					Log.e(FaceMatchActivity.TAG,"Invalid Record in data table");
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		OicrPerson[] results = new OicrPerson[dataAsList.size()];
		for (int o = 0; o < dataAsList.size(); o++) {
			results[o] = dataAsList.get(o);
		}
		return results;
	}

}
