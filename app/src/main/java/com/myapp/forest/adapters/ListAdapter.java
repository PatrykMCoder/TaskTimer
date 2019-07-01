package com.myapp.forest.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.myapp.forest.R;


public class ListAdapter extends BaseAdapter {

    private Context context;
    private String title[];
    private static LayoutInflater layoutInflater;

    private TextView titleTextView;
    private TextView finishTextView;

    public ListAdapter(Context context, String[] title) {
        this.context = context;
        this.title = title;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
       return title.length;
    }

    @Override
    public Object getItem(int position) {
        return title[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null)
            view = layoutInflater.inflate(R.layout.list_item, null);

        titleTextView = view.findViewById(R.id.listItemTitle);
        finishTextView = view.findViewById(R.id.listItemFinish);

        titleTextView.setText(title[position]);
        return view;
    }
}
