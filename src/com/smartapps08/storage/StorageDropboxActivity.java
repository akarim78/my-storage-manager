package com.smartapps08.storage;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.smartapps08.adapters.FileListAdapter;
import com.smartapps08.model.FileListEntry;

public class StorageDropboxActivity extends ActionBarActivity {
	private static final String TAG = StorageDropboxActivity.class.getName();
	// app specific settings, need to obfuscate
	final static private String APP_KEY = "j53b8cg7dyq6zb0";
	final static private String APP_SECRET = "po3t94byh11hurs";
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;

	private FileListEntry currentDir;
	private List<FileListEntry> files;
	private FileListAdapter adapter;

	private ArrayAdapter<CharSequence> mSpinnerAdapter;
	private ListView fileListView;

	// No need to change these, leave them alone.
	final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

	private static final boolean USE_OAUTH1 = false;
	DropboxAPI<AndroidAuthSession> mApi;
	private boolean mLoggedIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dropbox);

		fileListView = (ListView) findViewById(R.id.lvFiles);
		// initialization
		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys,
				ACCESS_TYPE);
		mApi = new DropboxAPI<AndroidAuthSession>(session);
		mApi.getSession()
				.startOAuth2Authentication(StorageDropboxActivity.this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mApi.getSession().authenticationSuccessful()) {
			try {
				// Required to complete auth, sets the access token on the
				// session
				mApi.getSession().finishAuthentication();
				String accessToken = mApi.getSession().getOAuth2AccessToken();

				currentDir = new FileListEntry("/", null, "/", "", "", true);
				new FetchListing().execute("/");
			} catch (IllegalStateException e) {
				Log.i("DbAuthLog", "Error authenticating", e);
			}
		}
	}

	class FetchListing extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				Entry dirent = mApi.metadata(params[0], 1000, null, true, null);
				files = new ArrayList<FileListEntry>();

				for (Entry ent : dirent.contents) {
					FileListEntry fle = new FileListEntry(ent.path,
							ent.parentPath(), ent.fileName(), ent.size,
							ent.modified, ent.isDir);
					files.add(fle);
					Log.e(TAG, fle.toString());
				}
				currentDir = new FileListEntry(dirent.path,
						dirent.parentPath(), dirent.fileName(), dirent.size,
						dirent.modified, true);
				return "";
			} catch (DropboxException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			adapter = new FileListAdapter(StorageDropboxActivity.this, files);
			fileListView.setAdapter(adapter);
			fileListView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					FileListEntry selected = files.get(position);

					if (selected.isDir()) {
						new FetchListing().execute(selected.getPath());
//						currentDir = selected;
					} else {
						// show properties
					}
				}

			});
		}

	}

	@Override
	public void onBackPressed() {
		// if (getPreferenceHelper().useBackNavigation()) {
		if (currentDir.getPath().equals("/")) {
			finish();
		} else {
			new FetchListing().execute(currentDir.getParentPath());
		}
		// } else {
		// super.onBackPressed();
		// }

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dropbox, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
