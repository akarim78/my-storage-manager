package com.smartapps08.storage;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.smartapps08.adapters.FileListAdapter;

public class GoogleDriveActivity extends ActionBarActivity implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {
	private static final String TAG = StorageDropboxActivity.class.getName();
	public GoogleApiClient mGoogleApiClient;
	protected static final int REQUEST_CODE_RESOLUTION = 1;

	private ListView lvFiles;
	private FileListAdapter adapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gdrive);
		lvFiles = (ListView) findViewById(R.id.lvFiles);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addApi(Drive.API).addScope(Drive.SCOPE_FILE)
					.addScope(Drive.SCOPE_FILE)
					// required for App Folder sample
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
		}
		mGoogleApiClient.connect();

	}

	ResultCallback<MetadataBufferResult> childrenRetrievedCallback = new ResultCallback<MetadataBufferResult>() {
		@Override
		public void onResult(MetadataBufferResult result) {
			Log.e(TAG, "Result received");
			if (!result.getStatus().isSuccess()) {
				Toast.makeText(getApplicationContext(),
						"Problem while retrieving files", Toast.LENGTH_LONG)
						.show();
				return;
			}
			MetadataBuffer buf = result.getMetadataBuffer();
			Log.e(TAG, "Size: " + buf.getCount());
			for (Metadata metadata : buf) {
				Toast.makeText(getApplicationContext(),
						metadata.getTitle() + "---" + metadata.isFolder(),
						Toast.LENGTH_LONG).show();
				Log.e(TAG, metadata.getTitle() + "---" + metadata.isFolder());
			}

			// mResultsAdapter.clear();
			// mResultsAdapter.append(result.getMetadataBuffer());
			// showMessage("Successfully listed files.");
		}
	};

	@Override
	public void onConnected(Bundle bundle) {
		Log.i(TAG, "GoogleApiClient connected");
		if (mGoogleApiClient.isConnected()) {
			Log.e(TAG, "Listing files");

			DriveFolder folder = Drive.DriveApi.getRootFolder(mGoogleApiClient);
			folder.listChildren(mGoogleApiClient).setResultCallback(
					childrenRetrievedCallback);
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Log.i(TAG, "GoogleApiClient connection suspended");
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
		if (!result.hasResolution()) {
			// show the localized error dialog.
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}
		try {
			result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
		} catch (SendIntentException e) {
			Log.e(TAG, "Exception while starting resolution activity", e);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
			mGoogleApiClient.connect();
		}
	}

	/**
	 * Called when activity gets invisible. Connection to Drive service needs to
	 * be disconnected as soon as an activity is invisible.
	 */
	@Override
	protected void onPause() {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
		super.onPause();
	}
}
