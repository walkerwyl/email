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


import java.util.HashMap;
import java.util.List;

public class HomeAdapter extends ArrayAdapter {
    private static final String TAG = "HomeAdapter";

    public HomeAdapter(Context context,
                       int resource,
                       List<HashMap<String, String>> list) {
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
        TextView textDate = itemView.findViewById(R.id.text_date);
        TextView textFrom = itemView.findViewById(R.id.text_from);

        textSubject.setText(map.get("ItemSubject"));
        textDate.setText(map.get("ItemDate").substring(0, 10));
        textFrom.setText(map.get("ItemFrom"));

        return itemView;
    }

//    使用方法转换, 实际的ItemDate
}
