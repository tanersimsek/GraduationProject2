package cmpe.sjsu.socialawesome.services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Arrays;
import java.util.List;

import cmpe.sjsu.socialawesome.InMailDetailFragment;
import cmpe.sjsu.socialawesome.R;
import cmpe.sjsu.socialawesome.StartActivity;
import cmpe.sjsu.socialawesome.Utils.HTTPUtil;
import cmpe.sjsu.socialawesome.models.PushMessageContent;

/**
 * Created by lam on 5/11/17.
 */
public class SocialMessageService extends FirebaseMessagingService {
    private static final String TAG = SocialMessageService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "The message receive: " + remoteMessage.getData());

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                        .setContentTitle(remoteMessage.getData().get(PushMessageContent.TITLE_PUSH_MESSAGE))
                        .setContentText(remoteMessage.getData().get(PushMessageContent.BODY_PUSH_MESSAGE));

        Intent intent = new Intent(this, StartActivity.class);
        if (remoteMessage.getData().get(PushMessageContent.ACTION_PUSH_MESSAGE) != null) {
            intent.putExtra(PushMessageContent.ACTION_PUSH_MESSAGE, remoteMessage.getData().get(PushMessageContent.ACTION_PUSH_MESSAGE));
        }

        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        builder.setContentIntent(pIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setAutoCancel(true);
        mNotificationManager.notify(0, builder.build());

        if (!isForeground("cmpe.sjsu.socialawesome")) {
            // Send email
            Log.d(TAG, "-- Sending email to the current user --");
            HTTPUtil.sendEmail(getBaseContext(), Arrays.asList(remoteMessage.getData().get(InMailDetailFragment.IN_MAIL_EMAIL_ADDRESS)),
                    getString(R.string.email_subject_inmail), getString(R.string.email_content_inmail, remoteMessage.getData().get(InMailDetailFragment.IN_MAIL_SUBJECT)
                            , remoteMessage.getData().get(InMailDetailFragment.IN_MAIL_SUBJECT)));
        }
    }

    public boolean isForeground(String PackageName) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(1);
        ComponentName componentInfo = task.get(0).topActivity;
        if (componentInfo.getPackageName().equals(PackageName)) return true;
        return false;
    }
}
