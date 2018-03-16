package com.delaroystudios.geofence;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.delaroystudios.geofence.data.GeofenceContract;

/**
 * Created by delaroy on 2/6/18.
 */

public class GeofenceCursorAdapter extends CursorAdapter {

    private TextView mTitleText, mDateAndTimeText, mRepeatInfoText;
    private ImageView mActiveImage , mThumbnailImage;

    public GeofenceCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.items, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        mTitleText = (TextView) view.findViewById(R.id.recycle_title);
        mDateAndTimeText = (TextView) view.findViewById(R.id.recycle_date_time);

        int titleColumnIndex = cursor.getColumnIndex(GeofenceContract.GeofenceEntry.KEY_TITLE);
        int dateColumnIndex = cursor.getColumnIndex(GeofenceContract.GeofenceEntry.KEY_DATE);

        String title = cursor.getString(titleColumnIndex);
        String date = cursor.getString(dateColumnIndex);

        mTitleText.setText("Description: " + title);
        mDateAndTimeText.setText("Date and Time: " + date);

    }
}
