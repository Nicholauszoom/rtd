package com.example.transactionsms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationAdapter extends BaseAdapter {

    private Context mContext;
    private String[] navigationTitles;
    private int[] navigationIcons;

    public NavigationAdapter(Context context, String[] titles, int[] icons) {
        mContext = context;
        navigationTitles = titles;
        navigationIcons = icons;
    }

    @Override
    public int getCount() {
        return navigationTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return navigationTitles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.nav_item_layout, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        ImageView iconImageView = convertView.findViewById(R.id.iconImageView);

        titleTextView.setText(navigationTitles[position]);
        iconImageView.setImageResource(navigationIcons[position]);

        return convertView;
    }
}