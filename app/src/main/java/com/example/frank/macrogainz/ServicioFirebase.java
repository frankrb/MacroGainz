package com.example.frank.macrogainz;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.support.constraint.Constraints.TAG;

public class ServicioFirebase extends FirebaseMessagingService {
    public ServicioFirebase() {
    }
    /**Este métdo gestiona los mensajes recibidos desde FCM
     * **/
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Si la aplicación está en background, se
        //muestra una notificación, pero no se
        //ejecuta este método

        if (remoteMessage.getData().size() > 0) {
            //Si el mensaje viene con datos
            Log.d(TAG, "Datos del mensaje FCM: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            //Si el mensaje es una notificación
            Log.d(TAG, "Notificación del mensaje FCM: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification());
        }
    }





    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        //Qué hacer cada vez que se genere un
        //token para el dispositivo
        Log.d(TAG, "Nuevo token: " + token);
    }

    /**Este metodo lanza la notificación a nuestro teléfono
     * **/
    private void sendNotification(RemoteMessage.Notification notification) {
        Intent intent = new Intent(this, PerfilUsuario.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // desde android Oreo el canal de notificación se requiere
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
