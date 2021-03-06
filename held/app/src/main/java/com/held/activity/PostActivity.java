package com.held.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.fragment.ChatFragment;
import com.held.fragment.FeedFragment;
import com.held.fragment.FriendsListFragment;
import com.held.fragment.NotificationFragment;
import com.held.fragment.PostFragment;
import com.held.fragment.SendFriendRequestFragment;
import com.held.utils.AppConstants;

/**
 * Created by jay on 5/8/15.
 */
public class PostActivity extends ParentActivity implements View.OnClickListener {

    private Fragment mDisplayFragment;
    private ImageView mChat, mCamera, mNotification;
    private EditText mSearchEdt;
    private Button mRetakeBtn, mPostBtn;
    private TextView mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mChat = (ImageView) findViewById(R.id.TOOLBAR_chat_img);
        mCamera = (ImageView) findViewById(R.id.TOOLBAR_camera_img);
        mNotification = (ImageView) findViewById(R.id.TOOLBAR_notification_img);
        mSearchEdt = (EditText) findViewById(R.id.TOOLBAR_search_edt);
        mRetakeBtn = (Button) findViewById(R.id.TOOLBAR_retake_btn);
        mPostBtn = (Button) findViewById(R.id.TOOLBAR_post_btn);
        mUsername = (TextView) findViewById(R.id.TOOLBAR_user_name_txt);

        mChat.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mRetakeBtn.setOnClickListener(this);
        mPostBtn.setOnClickListener(this);

//        if (PreferenceHelper.getInstance(getApplicationContext()).readPreference("isFirstPostCreated", false)) {
//            launchFeedScreen();
//        } else {
        launchCreatePostScreen();
//        }
    }

    private void launchFeedScreen() {
//        Intent intent = new Intent(PostActivity.this, FeedActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("isProfile", true);
//        startActivity(intent);
        finish();
//        updateToolbar(true, false, true, false, true, true, false, "");
//        addFragment(FeedFragment.newInstance(), FeedFragment.TAG, true);
//        mDisplayFragment = FeedFragment.newInstance();
    }

    private void launchCreatePostScreen() {
        updateToolbar(false, true, false, false, false, false, true, "");
        replaceFragment(PostFragment.newInstance(), PostFragment.TAG, false);
        mDisplayFragment = PostFragment.newInstance();
    }

    private void launchNotificationScreen() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(NotificationFragment.newInstance(), NotificationFragment.TAG, true);
        mDisplayFragment = NotificationFragment.newInstance();
    }

    private void launchCreatePostFragmentFromFeed() {
        updateToolbar(false, true, false, false, false, false, true, "");
        addFragment(PostFragment.newInstance(), PostFragment.TAG, true);
        mDisplayFragment = PostFragment.newInstance();
    }

    private void launchChatScreen(String id, boolean isOneToOne) {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(ChatFragment.newInstance(id, isOneToOne), ChatFragment.TAG, true);
        mDisplayFragment = ChatFragment.newInstance(id, isOneToOne);
    }

    @Override
    public void onBackPressed() {
        launchFeedScreen();
    }

    @Override
    public void perform(int id, Bundle bundle) {
        super.perform(id, bundle);
        switch (id) {
            case 0:
                launchCreatePostScreen();
                break;
            case 1:
                launchFeedScreen();
                break;
            case 2:
                if (bundle != null)
                    launchChatScreen(bundle.getString("postid"), false);
                break;
            case 3:
                launchCreatePostFragmentFromFeed();
                break;
            case 4:
                launchNotificationScreen();
                break;
            case 5:
                if (bundle != null)
                    launchRequestFriendScreen(bundle.getString("name"), bundle.getString("image"));
                break;
            case 6:
                if (bundle != null)
                    launchChatScreen(bundle.getString("owner_displayname"), true);
                break;
            case 7:
                launchInboxPage();
                break;

        }
    }

    private void launchInboxPage() {
        updateToolbar(true, false, true, false, true, true, false, "");
        addFragment(FriendsListFragment.newInstance(), FriendsListFragment.TAG, true);
        mDisplayFragment = FriendsListFragment.newInstance();
    }

    private void launchRequestFriendScreen(String name, String image) {
        updateToolbar(false, false, false, false, false, false, false, "");
        addFragment(SendFriendRequestFragment.newInstance(name, AppConstants.BASE_URL + image), SendFriendRequestFragment.TAG, true);
        mDisplayFragment = SendFriendRequestFragment.newInstance(name, AppConstants.BASE_URL + image);
    }

    @Override
    public void onClick(View view) {
        invalidateToolbar(view.getId());
    }

    private void invalidateToolbar(int id) {
        switch (id) {
            case R.id.TOOLBAR_chat_img:
                if (mDisplayFragment instanceof FeedFragment) {
                    perform(7, null);
                } else if (mDisplayFragment instanceof FriendsListFragment) {
                    onBackPressed();
                    mChat.setImageResource(R.drawable.icon_chat);
                } else if (mDisplayFragment instanceof NotificationFragment) {
                    onBackPressed();
                    mChat.setImageResource(R.drawable.icon_chat);
                } else if (mDisplayFragment instanceof ChatFragment) {
                    onBackPressed();
                    mChat.setImageResource(R.drawable.icon_chat);
                }
                break;
            case R.id.TOOLBAR_notification_img:
                if (mDisplayFragment instanceof FeedFragment) {
                    perform(4, null);
                } else if (mDisplayFragment instanceof FriendsListFragment) {
                    perform(4, null);
                } else if (mDisplayFragment instanceof NotificationFragment) {

                } else if (mDisplayFragment instanceof ChatFragment) {
                    onBackPressed();
                    mChat.setImageResource(R.drawable.icon_chat);
                }
                break;
            case R.id.TOOLBAR_camera_img:
                if (mDisplayFragment instanceof FeedFragment) {
                    perform(3, null);
                } else if (mDisplayFragment instanceof FriendsListFragment) {
                    perform(3, null);
                } else if (mDisplayFragment instanceof NotificationFragment) {
                    perform(3, null);
                } else if (mDisplayFragment instanceof ChatFragment) {
                    perform(3, null);
                }
                break;
            case R.id.TOOLBAR_retake_btn:
                break;
            case R.id.TOOLBAR_post_btn:
                break;
        }
    }

    public void onLeftSwipe() {
        // Do something
        launchFeedScreen();
    }

    public void onRightSwipe() {
        // Do something
        finish();
    }
}
