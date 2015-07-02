package br.great.jogopervasivo.gcmUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import br.ufc.great.arviewer.android.R;
import br.great.jogopervasivo.actvititesDoJogo.ChatActivity;

/**
 * Created by messiaslima on 26/02/2015.
 */
public class NotificationCustomUtil {
    private static NotificationManager mNotificationManager;

    public static void sendNotification(final Context context, String author, String message) {

        //Notification notification = new Notification(R.drawable.ic_launcher,context.getString(R.string.app_name),0);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, ChatActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_UPDATE_CURRENT);
        // notification.setLatestEventInfo(context, context.getString(R.string.app_name), author+": "+message,contentIntent);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(author + ": " + message);

        mBuilder.setContentIntent(contentIntent);

        Notification notification = mBuilder.build();

        notification.flags |= notification.FLAG_AUTO_CANCEL;
        notification.defaults |= notification.DEFAULT_ALL;

        mNotificationManager.notify((int)Math.random()*10, notification);


    }
}
