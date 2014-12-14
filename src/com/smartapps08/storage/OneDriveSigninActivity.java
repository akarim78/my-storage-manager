package com.smartapps08.storage;

import java.util.Arrays;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveStatus;
import com.smartapps08.util.Config;

public class OneDriveSigninActivity extends ActionBarActivity {
	private StorageManagerApplication app;
	private LiveAuthClient authClient;
	private ProgressDialog initializeDialog;
	private Button signInButton;
	private TextView beginTextView;
	private Button needIdButton;

	private TextView beginTextViewNeedId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onedrive_signin);
		app = (StorageManagerApplication) getApplication();
		authClient = new LiveAuthClient(app, Config.CLIENT_ID);
		app.setAuthClient(authClient);
		initializeDialog = ProgressDialog.show(this, "",
				"Initializing. Please wait...", true);

		beginTextView = (TextView) findViewById(R.id.beginTextView);
		signInButton = (Button) findViewById(R.id.signInButton);

		beginTextViewNeedId = (TextView) findViewById(R.id.beginTextViewNeedId);
		needIdButton = (Button) findViewById(R.id.needIdButton);

		// Check to see if the CLIENT_ID has been changed.
		if (Config.CLIENT_ID.equals("0000000044134739")) {
			needIdButton.setVisibility(View.VISIBLE);
			beginTextViewNeedId.setVisibility(View.VISIBLE);
			needIdButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					final Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(getBaseContext().getString(
							R.string.AndroidSignInHelpLink)));
					startActivity(intent);
				}
			});
		}

		signInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				authClient.login(OneDriveSigninActivity.this,
						Arrays.asList(Config.SCOPES), new LiveAuthListener() {
							@Override
							public void onAuthComplete(LiveStatus status,
									LiveConnectSession session, Object userState) {
								if (status == LiveStatus.CONNECTED) {
									launchMainActivity(session);
								} else {
									Toast.makeText(
											getApplicationContext(),
											"Login did not connect. Status is "
													+ status + ".",
											Toast.LENGTH_LONG).show();
								}
							}

							@Override
							public void onAuthError(
									LiveAuthException exception,
									Object userState) {
								Log.e("OneDrive", exception.getMessage());
							}
						});
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		authClient.initialize(Arrays.asList(Config.SCOPES),
				new LiveAuthListener() {
					@Override
					public void onAuthError(LiveAuthException exception,
							Object userState) {
						initializeDialog.dismiss();
						showSignIn();
						Log.e("OneDrive", exception.getMessage());
					}

					@Override
					public void onAuthComplete(LiveStatus status,
							LiveConnectSession session, Object userState) {
						initializeDialog.dismiss();

						if (status == LiveStatus.CONNECTED) {
							launchMainActivity(session);
						} else {
							showSignIn();
						}
					}
				});
	}

	private void launchMainActivity(LiveConnectSession session) {
		assert session != null;
		app.setSession(session);
		app.setConnectClient(new LiveConnectClient(session));
		startActivity(new Intent(getApplicationContext(),
				OneDriveActivity.class));
	}

	private void showSignIn() {
		signInButton.setVisibility(View.VISIBLE);
		beginTextView.setVisibility(View.VISIBLE);
	}
}
