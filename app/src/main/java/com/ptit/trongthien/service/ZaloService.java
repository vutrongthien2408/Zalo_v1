package com.ptit.trongthien.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;
import com.ptit.trongthien.model.ItemChat;
import com.ptit.trongthien.resource.MyResource;
import com.ptit.trongthien.zalo_v1.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by TrongThien on 5/24/2017.
 */
public class ZaloService extends Service {
    private static final int REQUEST_CODE = 1;
    private static final int NOTIFY_ID = 2;
    private String postName = "";
    private String userNameSend = "";
    private String notify = "";
    public static Socket mSocket;

    {
        try {
            mSocket = IO.socket(Main.MY_IP);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSocket.connect();
        mSocket.on("have-new-msg", msgHaveNewMsg);
        mSocket.on("have-new-status", msgHaveNewStatus);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private Emitter.Listener msgHaveNewMsg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        userNameSend = jsonObject.getString("userNameSend");
                        MyResource myResource = (MyResource) getApplicationContext();
                        String userNameReceive = jsonObject.getString("userNameReceive");
                        if (userNameReceive.equals(myResource.userName)) {
                            notify = "Ban co tin nhan moi tu " + userNameSend;
                            createMsgNotify();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            runnable.run();
        }
    };

    //lang nghe thong bao co cap nhat moi
    private Emitter.Listener msgHaveNewStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        boolean result = jsonObject.getBoolean("resultStatus");
                        postName = jsonObject.getString("userName");
                        MyResource myResource = (MyResource) getApplicationContext();
                        if (result == true) {
                            if (!postName.equals(myResource.userName)) {
                                notify = postName + " vua cap nhat cam nghi moi";
                                createNotify();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            runnable.run();
        }
    };

    //tao thong bao co cam nghi moi
    private void createNotify() {
        final NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        login();
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, 0);

        final Notification.Builder notifyBuilder = new Notification.Builder(this);
        notifyBuilder.setContentTitle("Zalo");
        notifyBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notifyBuilder.setAutoCancel(true);

        notifyBuilder.setContentText(notify);

        notifyBuilder.setContentIntent(pIntent);
        notifyManager.notify(NOTIFY_ID, notifyBuilder.build());
    }
    private void createMsgNotify() {
        final NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        login();
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, 0);

        final Notification.Builder notifyBuilder = new Notification.Builder(this);
        notifyBuilder.setContentTitle("Zalo");
        notifyBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notifyBuilder.setAutoCancel(true);

        notifyBuilder.setContentText(notify);

        notifyBuilder.setContentIntent(pIntent);
        notifyManager.notify(NOTIFY_ID, notifyBuilder.build());
    }
}
