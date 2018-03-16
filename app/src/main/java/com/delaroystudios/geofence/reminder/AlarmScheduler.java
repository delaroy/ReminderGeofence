package com.delaroystudios.geofence.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

/**
 * Created by delaroy on 9/22/17.
 */

public class AlarmScheduler {

    public void setAlarm(Context context, long alarmTime, String geoFenceTitle) {
        AlarmManager manager = AlarmManagerProvider.getAlarmManager(context);

        PendingIntent operation =
                ReminderAlarmService.getReminderPendingIntent(context, Uri.parse(geoFenceTitle));


        if (Build.VERSION.SDK_INT >= 23) {

            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, operation);

        } else if (Build.VERSION.SDK_INT >= 19) {

            manager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, operation);

        } else {

            manager.set(AlarmManager.RTC_WAKEUP, alarmTime, operation);

        }
    }

    public void setRepeatAlarm(Context context, long alarmTime, String geoFenceTitle, long RepeatTime) {
        AlarmManager manager = AlarmManagerProvider.getAlarmManager(context);

          PendingIntent operation =
                ReminderAlarmService.getReminderPendingIntent(context, Uri.parse(geoFenceTitle));

        manager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, RepeatTime, operation);


    }

    public void cancelAlarm(Context context) {
        AlarmManager manager = AlarmManagerProvider.getAlarmManager(context);

        PendingIntent operation =
               ReminderAlarmService.getReminderPendingIntent(context);

        manager.cancel(operation);


    }

}