package com.delaroystudios.geofence.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by delaroy on 2/6/18.
 */

public class GeofenceDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "geofences.db";

    private static final int DATABASE_VERSION = 1;

    public GeofenceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a String that contains the SQL statement to create the reminder table
        String SQL_CREATE_GEOFENCE_TABLE =  "CREATE TABLE " + GeofenceContract.GeofenceEntry.TABLE_NAME + " ("
                + GeofenceContract.GeofenceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GeofenceContract.GeofenceEntry.KEY_TITLE + " TEXT, "
                + GeofenceContract.GeofenceEntry.KEY_DATE + " TEXT, "
                + GeofenceContract.GeofenceEntry.KEY_TIME + " TEXT " + " );";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_GEOFENCE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
