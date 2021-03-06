package com.held.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.held.receiver.NetworkStateReceiver;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.CreateUserResponse;
import com.held.retrofit.response.LoginUserResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.retrofit.response.VerificationResponse;
import com.held.retrofit.response.VoiceCallResponse;
import com.held.utils.DialogUtils;
import com.held.utils.NetworkUtil;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by jay on 1/8/15.
 */
public class VerificationActivity extends ParentActivity implements View.OnClickListener, NetworkStateReceiver.OnNetworkChangeListener {

    public static String TAG = VerificationActivity.class.getSimpleName();

    private EditText mFirstEdt, mSecondEdt, mThirdEdt, mForthEdt;
    private TextView mResendSmsTxt, mVoiceCallTxt, mUserNameTxt, mIndicationTxt;
    private boolean mNetWorkStatus, isBackPressed;
    private String mPhoneNo, mUserName, mPin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        if (getIntent().getExtras() != null) {
            mUserName = getIntent().getExtras().getString("username");
            mPhoneNo = getIntent().getExtras().getString("phoneno");
        }

        mFirstEdt = (EditText) findViewById(R.id.VERIFICATION_code_one);
        mSecondEdt = (EditText) findViewById(R.id.VERIFICATION_code_two);
        mThirdEdt = (EditText) findViewById(R.id.VERIFICATION_code_three);
        mForthEdt = (EditText) findViewById(R.id.VERIFICATION_code_four);
        mResendSmsTxt = (TextView) findViewById(R.id.VERIFICATION_sms_resend_txt);
        mVoiceCallTxt = (TextView) findViewById(R.id.VERIFICATION_voice_call_txt);
        mUserNameTxt = (TextView) findViewById(R.id.VERIFICATION_username_txt);
        mIndicationTxt = (TextView) findViewById(R.id.VERIFICATION_code_sent_txt);
        mUserNameTxt.setText("Hi, " + mUserName + "!");
        mIndicationTxt.setText("verification code sent to " + mPhoneNo);

        mResendSmsTxt.setOnClickListener(this);
        mVoiceCallTxt.setOnClickListener(this);

        mNetWorkStatus = NetworkUtil.isInternetConnected(getApplicationContext());
        NetworkStateReceiver.registerOnNetworkChangeListener(this);

        mFirstEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(mFirstEdt.getText().toString().trim()))
                    mSecondEdt.requestFocus();
            }
        });

        mSecondEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(mSecondEdt.getText().toString().trim()))
                    mThirdEdt.requestFocus();
            }
        });

        mThirdEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(mThirdEdt.getText().toString().trim()))
                    mForthEdt.requestFocus();
            }
        });

        mForthEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!TextUtils.isEmpty(mFirstEdt.getText().toString().trim()) && !TextUtils.isEmpty(mSecondEdt.getText().toString().trim())
                        && !TextUtils.isEmpty(mThirdEdt.getText().toString().trim()) && !TextUtils.isEmpty(mForthEdt.getText().toString().trim())) {
                    mPin = mFirstEdt.getText().toString().trim() + mSecondEdt.getText().toString().trim()
                            + mThirdEdt.getText().toString().trim() + mForthEdt.getText().toString().trim();
                    Log.d(TAG, "Pin No: " + mPin);

                    if (mNetWorkStatus) {
                        mFirstEdt.setText("");
                        mSecondEdt.setText("");
                        mThirdEdt.setText("");
                        mForthEdt.setText("");
                        DialogUtils.showProgressBar();
                        callVerificationApi();
                    } else
                        UiUtils.showSnackbarToast(findViewById(R.id.root_view), Utils.getString(R.string.error_offline_msg));
                }

            }
        });

    }

    private void callVerificationApi() {
        HeldService.getService().verifyUser(mPhoneNo, mPin, new Callback<VerificationResponse>() {
                    @Override
                    public void success(VerificationResponse verificationResponse, Response response) {
                        if (verificationResponse.isVerified()) {
                            PreferenceHelper.getInstance(getApplicationContext()).writePreference(getString(R.string.API_pin), Integer.parseInt(mPin));
                            DialogUtils.showProgressBar();
                            callLoginUserApi();
                        }
                        DialogUtils.stopProgressDialog();
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
                }

        );
    }

    private void callUpdateRegIdApi() {
        HeldService.getService().updateRegID(PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_session_token)),
                "notification_token", PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_registration_key)), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                        launchComposeScreen();
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

    private void callLoginUserApi() {
        HeldService.getService().loginUser(mPhoneNo, mPin, new Callback<LoginUserResponse>() {
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
                if (error != null && error.getResponse() != null &&!TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), json.substring(json.indexOf(":") + 2, json.length() - 2));
                } else
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Some Problem Occurred");
            }
        });
    }

    private void launchComposeScreen() {
        Intent intent = new Intent(VerificationActivity.this, PostActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.VERIFICATION_sms_resend_txt:
                if (mNetWorkStatus) {
                    if (validatePhoneNo()) {
                        DialogUtils.showProgressBar();
                        callResendSmsApi();
                    }
                } else
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), Utils.getString(R.string.error_offline_msg));
                break;
            case R.id.VERIFICATION_voice_call_txt:
                if (mNetWorkStatus) {
                    if (validatePhoneNo()) {
                        DialogUtils.showProgressBar();
                        callVoiceCallApi();
                    }
                } else {
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), Utils.getString(R.string.error_offline_msg));
                }
                break;
        }
    }

    private void callVoiceCallApi() {
        HeldService.getService().voiceCall(mPhoneNo, new Callback<VoiceCallResponse>() {
            @Override
            public void success(VoiceCallResponse voiceCallResponse, Response response) {
                DialogUtils.stopProgressDialog();
                Log.d(TAG, "Voice Call Success");
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

    private boolean validatePhoneNo() {
        if (!TextUtils.isEmpty(mPhoneNo)) {
            return true;
        } else return false;
    }

    private void callResendSmsApi() {
        HeldService.getService().resendSms(mPhoneNo, new Callback<CreateUserResponse>() {
            @Override
            public void success(CreateUserResponse createUserResponse, Response response) {
                DialogUtils.stopProgressDialog();
                PreferenceHelper.getInstance(getApplicationContext()).writePreference(getString(R.string.API_pin), createUserResponse.getPin());
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

    @Override
    public void onNetworkStatusChanged(boolean isEnabled) {
        mNetWorkStatus = isEnabled;
    }

}
