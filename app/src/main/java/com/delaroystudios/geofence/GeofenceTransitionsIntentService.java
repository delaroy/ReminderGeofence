/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.delaroystudios.geofence;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.delaroystudios.geofence.data.GeofenceContract;
import com.delaroystudios.geofence.reminder.AlarmScheduler;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.delaroystudios.geofence.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Listener for geofence transition changes.
 *
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a notification
 * as the output.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    private static final String TAG = "GeofenceTransitionsIS";


    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;
    private long mRepeatTime;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                    triggeringGeofences);

            // Send notification and log the transition details.
            Calendar currentTime = Calendar.getInstance();
            //sendNotification(geofenceTransitionDetails + currentTime.toString());

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            String formattedDate = df.format(currentTime.getTime());

            ContentValues values = new ContentValues();
            values.put(GeofenceContract.GeofenceEntry.KEY_TITLE, geofenceTransitionDetails);
            values.put(GeofenceContract.GeofenceEntry.KEY_DATE, formattedDate);

            getContentResolver().insert(GeofenceContract.GeofenceEntry.CONTENT_URI, values);
            Log.d(TAG, " inserted successfully " + values);

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String repInterval = sharedPrefs.getString(
                    getString(R.string.settings_rep_interval_key),
                    getString(R.string.settings_rep_interval_default));

            String repType = sharedPrefs.getString(
                    getString(R.string.settings_rep_type_key),
                    getString(R.string.settings_rep_type_default)
            );

            boolean repeatState = sharedPrefs.getBoolean(
                    getString(R.string.settings_repeat_key),
                    Boolean.parseBoolean(getString(R.string.settings_repeat_default))
            );

            if (repType.equals("minutes")) {
                mRepeatTime = Integer.parseInt(repInterval) * milMinute;
            } else if (repType.equals("hours")) {
                mRepeatTime = Integer.parseInt(repInterval) * milHour;
            }

            Log.d(TAG, " repeat time " + repType);

            if (repeatState == true) {
                new AlarmScheduler().setRepeatAlarm(getApplicationContext(), currentTime.getTimeInMillis(), geofenceTransitionDetails, mRepeatTime);
            } else  {
                new AlarmScheduler().setAlarm(getApplicationContext(), currentTime.getTimeInMillis(), geofenceTransitionDetails);
            }
            //  Toast.makeText(this, "Alarm time is " + selectedTimestamp, Toast.LENGTH_LONG).show();


           // Toast.makeText(getApplicationContext(), "Entered " + geofenceTransitionDetails + currentTime.toString(), Toast.LENGTH_LONG).show();
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);



        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}
