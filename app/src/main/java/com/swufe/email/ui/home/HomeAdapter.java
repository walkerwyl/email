package com.swufe.email.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.swufe.email.R;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeAdapter extends ArrayAdapter {
    private static final String TAG = "HomeAdapter";

    public HomeAdapter(Context context,
                       int resource,
                       ArrayList<HashMap<String, String>> list) {
        super(context, resource, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,
                    parent,
                    false);
        }
        HashMap<String, String> map = (HashMap<String, String>) getItem(position);
        TextView textSubject = itemView.findViewById(R.id.text_subject);
        TextView texyDate = itemView.findViewById(R.id.text_date);

        textSubject.setText(map.get("ItemSubject"));
        texyDate.setText(map.get("ItemDate"));

        return itemView;
    }
}
