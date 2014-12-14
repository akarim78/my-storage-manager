package com.smartapps08.storage;

import java.util.ArrayList;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;
import com.smartapps08.adapters.FileListAdapter;
import com.smartapps08.model.FileListEntry;
import com.smartapps08.util.JsonKeys;

public class OneDriveActivity extends ActionBarActivity {
	private LiveAuthClient authClient;

	private ListView lvFiles;
	private FileListAdapter adapter;
	private ArrayList<FileListEntry> files;

	public static final String EXTRA_PATH = "path";

	private static final int DIALOG_DOWNLOAD_ID = 0;
	private static final String HOME_FOLDER = "me/skydrive";

	private LiveConnectClient client;
	private String currentFolderId;
	private Stack<String> prevFolderIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onedrive);
		lvFiles = (ListView) findViewById(R.id.lvFiles);
		prevFolderIds = new Stack<String>();

		StorageManagerApplication app = (StorageManagerApplication) getApplication();
		authClient = app.getAuthClient();
		client = app.getConnectClient();

	}

	@Override
	protected void onStart() {
		super.onStart();
		loadFolder(HOME_FOLDER);
	}

	private void loadFolder(String folderId) {
		assert folderId != null;
		currentFolderId = folderId;

		final ProgressDialog progressDialog = ProgressDialog.show(this, "",
				"Loading. Please wait...", true);

		client.getAsync(folderId + "/files", new LiveOperationListener() {
			@Override
			public void onComplete(LiveOperation operation) {
				progressDialog.dismiss();

				JSONObject result = operation.getResult();
				if (result.has(JsonKeys.ERROR)) {
					JSONObject error = result.optJSONObject(JsonKeys.ERROR);
					String message = error.optString(JsonKeys.MESSAGE);
					String code = error.optString(JsonKeys.CODE);
					Log.e("OneDrive", code + ": " + message);
					return;
				}
				try {
					JSONArray data = result.optJSONArray(JsonKeys.DATA);
					Log.e("JSON", data.toString());
					files = new ArrayList<FileListEntry>();
					if (data != null && data.length() > 0) {
						for (int i = 0; i < data.length(); i++) {
							JSONObject file = data.getJSONObject(i);
							String path = file.getString("id");
							String parentPath = file.getString("parent_id");
							String name = file.getString("name");
							String size = file.getString("size");
							String modified = file.getString("updated_time");
							boolean isDir = false;
							if (file.getString("type").equalsIgnoreCase(
									"folder")) {
								isDir = true;
							}
							FileListEntry entry = new FileListEntry(path,
									parentPath, name, size, modified, isDir);
							files.add(entry);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				adapter = new FileListAdapter(OneDriveActivity.this, files);
				lvFiles.setAdapter(adapter);
			}

			@Override
			public void onError(LiveOperationException exception,
					LiveOperation operation) {
				progressDialog.dismiss();

			}
		});

		lvFiles.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FileListEntry entry = files.get(position);
				if (entry.isDir()) {
					prevFolderIds.push(currentFolderId);
					loadFolder(entry.getPath());
				}else{
					
				}
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// if prev folders is empty, send the back button to the TabView
			// activity.
			if (prevFolderIds.isEmpty()) {
				return false;
			}

			loadFolder(prevFolderIds.pop());
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
