package com.example.youtubenativeplayer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    Context context;
    ArrayList<VideoData> arrayList ;
    LayoutInflater layoutInflater;
    public ListViewAdapter(Context context, ArrayList<VideoData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            if (layoutInflater == null) layoutInflater = LayoutInflater.from(context);
            if (convertView== null){
                convertView = layoutInflater.inflate(R.layout.item , null);
            }
        ImageView imageView = convertView.findViewById(R.id.imageimage) ;
        TextView textView = convertView.findViewById(R.id.texttext);
        VideoData video = arrayList.get(position);
         textView.setText(video.getTitle());
        Picasso.get().load(video.getUrl()).into(imageView);
        return convertView;
    }

}
