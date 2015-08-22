package com.held.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.held.activity.ChatActivity;
import com.held.activity.FeedActivity;
import com.held.activity.NotificationActivity;
import com.held.activity.R;
import com.held.utils.HeldApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class GcmIntentService extends IntentService {


    private final String TAG = GcmIntentService.class.getSimpleName();
    private AudioManager am;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.d(TAG, "Send error : " + extras.toString());

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.d(TAG, "Deleted messages on server: " + extras.toString());

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                checkNotificationType(extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        } else {
            Log.i(TAG, "Received Empty push notification message");
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void checkNotificationType(Bundle bundleResponse) {

//        Intent intent = new Intent(this, SplashActivity.class);
//        sendNotification(intent);

        //Coming Notification format
        // "GCM": "{\"data\":{\"title\":\"push notification\",\"message\":\"You have an Event coming up!
        // Don't forget to check your schedule!\",\"notificationId\":2,\"vibrate\":1,\"sound\":1}}"
//
        String type = bundleResponse.getString("type");

        Log.i(TAG, " type: " + type);
        String message = bundleResponse.getString("gcm.notification.body");
        String title = bundleResponse.getString("gcm.notification.title");
        Log.i(TAG, " title: " + title);

        int gameId, oppToken;

        switch (type) {
            case "friend:approve":
                Intent intent = new Intent(this, ChatActivity.class);
                sendNotification(intent, title, message);
                return;
            case "friend:message":
                if (HeldApplication.IS_APP_FOREGROUND) {
                    intent = new Intent(this, ChatActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(this, ChatActivity.class);
                    sendNotification(intent, title, message);
                }
                return;
            case "post:held":
                intent = new Intent(this, FeedActivity.class);
                sendNotification(intent, title, message);
            case "post:download_request":
                intent = new Intent(this, NotificationActivity.class);
                intent.putExtra("id", 1);
                sendNotification(intent, title, message);
            case "post:download_approve":
//                try {
//                    URL url = new URL("file://some/path/anImage.png");
//                    InputStream input = url.openStream();
//                } catch (Exception e) {
//
//                }
//                try {
//                    //The sdcard directory e.g. '/sdcard' can be used directly, or
//                    //more safely abstracted with getExternalStorageDirectory()
//                    File storagePath = Environment.getExternalStorageDirectory();
//                    OutputStream output = new FileOutputStream(new File(storagePath, "myImage.png"));
//                    try {
//                        byte[] buffer = new byte[aReasonableSize];
//                        int bytesRead = 0;
//                        while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
//                            output.write(buffer, 0, bytesRead);
//                        }
//                    } finally {
//                        output.close();
//                    }
//                } finally {
//                    input.close();
//                }
                break;
        }

    }

    protected void sendNotification(Intent intent, String title, String message) {

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(GCMConstants.NotificationID.getID(), mBuilder.build());
    }

    /*public void setVibrate(NotificationCompat.Builder mBuilder) {
        if (am != null) {
            switch (am.getRingerMode()) {
                case AudioManager.RINGER_MODE_SILENT:
                    mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                    break;
            }
        }
    }*/
}