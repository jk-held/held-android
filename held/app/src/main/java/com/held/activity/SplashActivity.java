package com.held.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.held.gcm.GCMControlManager;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.LoginUserResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class SplashActivity extends ParentActivity implements View.OnClickListener {

    private Button mGetStartedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mGetStartedBtn = (Button) findViewById(R.id.SPLASH_get_started_btn);
        mGetStartedBtn.setOnClickListener(this);

        setupGCM();

        if (!PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_phone_no)).isEmpty() &&
                PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_pin), 0) != 0) {
            if (getNetworkStatus()) {
                DialogUtils.showProgressBar();
                callLoginApi();
            } else {
                UiUtils.showSnackbarToast(findViewById(R.id.root_view), "You are not connected to internet");
            }
        } else if (!PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_phone_no)).isEmpty() &&
                PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_pin), 0) == 0)
            launchVerificationActivity();
    }

    private void callLoginApi() {
        HeldService.getService().loginUser(PreferenceHelper.getInstance(this).
                readPreference(getString(R.string.API_phone_no)), PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_pin), 0) + "", new Callback<LoginUserResponse>() {
            @Override
            public void success(LoginUserResponse loginUserResponse, Response response) {
                DialogUtils.stopProgressDialog();
                if (loginUserResponse.isLogin()) {
                    PreferenceHelper.getInstance(getApplicationContext()).writePreference(getString(R.string.API_session_token), loginUserResponse.getSession_token());
                    callUpdateRegIdApi();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                DialogUtils.stopProgressDialog();
                if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), json.substring(json.indexOf(":") + 2, json.length() - 2));
                } else
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Some Problem Occurred");
            }
        });
    }

    private void launchVerificationActivity() {
        Intent intent = new Intent(SplashActivity.this, VerificationActivity.class);
        intent.putExtra("username", PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_user_name)));
        intent.putExtra("phoneno", PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_phone_no)));
        startActivity(intent);
        finish();
    }

    private void launchPostActivity() {
        Intent intent = new Intent(SplashActivity.this, PostActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchFeedActivity() {
        Intent intent = new Intent(SplashActivity.this, FeedActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.SPLASH_get_started_btn:
                Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void setupGCM() {
        GCMControlManager gcmControlManager = new GCMControlManager(this);
        gcmControlManager.setupGCM();
    }

    private void callUpdateRegIdApi() {
        HeldService.getService().updateRegID(PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_session_token)),
                "notification_token", PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_registration_key)), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                        if (PreferenceHelper.getInstance(getApplicationContext()).readPreference(getString(R.string.API_is_first_post), false)) {
                            launchFeedActivity();
                        } else {
                            launchPostActivity();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(findViewById(R.id.root_view), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Some Problem Occurred");
                    }
                });
    }
}
