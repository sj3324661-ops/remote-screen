package com.remotescreen.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;

public class ScreenCaptureService extends Service {

    private static final String CHANNEL_ID = "screen_capture";
    public static final String EXTRA_RESULT_CODE = "result_code";
    public static final String EXTRA_RESULT_DATA = "result_data";

    private MediaProjection mediaProjection;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Notification notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("Remote Screen")
                        .setContentText("Screen sharing is active")
                        .setSmallIcon(android.R.drawable.ic_menu_view)
                        .build();

        startForeground(1001, notification);
    }

    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId) {

        if (intent != null) {

            int resultCode =
                    intent.getIntExtra(
                            EXTRA_RESULT_CODE,
                            0
                    );

            Intent resultData;

            if (Build.VERSION.SDK_INT >= 33) {
                resultData =
                        intent.getParcelableExtra(
                                EXTRA_RESULT_DATA,
                                Intent.class
                        );
            } else {
                resultData =
                        intent.getParcelableExtra(
                                EXTRA_RESULT_DATA
                        );
            }

            if (resultData != null) {

                MediaProjectionManager manager =
                        (MediaProjectionManager)
                                getSystemService(
                                        MEDIA_PROJECTION_SERVICE
                                );

                if (manager != null) {

                    mediaProjection =
                            manager.getMediaProjection(
                                    resultCode,
                                    resultData
                            );
                }
            }
        }

        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Screen Sharing",
                            NotificationManager.IMPORTANCE_LOW
                    );

            NotificationManager manager =
                    getSystemService(
                            NotificationManager.class
                    );

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {

        if (mediaProjection != null) {
            mediaProjection.stop();
            mediaProjection = null;
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
        }
