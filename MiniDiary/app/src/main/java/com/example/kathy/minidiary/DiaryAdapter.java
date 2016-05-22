package com.example.kathy.minidiary;

/**
 * Created by Kathy on 22/5/2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DiaryAdapter extends CursorAdapter {

    private TextView mTitleTextView;
    private TextView mDateTextView;

    public DiaryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_listview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        //TextView callSignTextView = (TextView)view.findViewById(R.id.textview_callsign);
        viewHolder.mTitleTextView.setText(cursor.getString(MainFragment.COL_TITLE));
        viewHolder.mDateTextView.setText(cursor.getString(MainFragment.COL_DATE));
        viewHolder.mTitleTextView.setTextColor(cursor.getInt(MainFragment.COL_MOOD));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    class ViewHolder {
        TextView mTitleTextView;
        TextView mDateTextView;

        public ViewHolder(View rootView) {
            mTitleTextView = (TextView) rootView.findViewById(R.id.listview_title);
            mDateTextView = (TextView) rootView.findViewById(R.id.listview_item_data_time);
        }
    }
}
