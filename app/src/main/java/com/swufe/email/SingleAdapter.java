package com.swufe.email;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

public class SingleAdapter extends ArrayAdapter {

    private static final String TAG = "SingleAdapter";

    public SingleAdapter (Context context,
                          int resource,
                          List<HashMap<String, String>> list) {
        super(context, resource, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.single_list_item,
                    parent,
                    false);
        }
        HashMap<String, String> map = (HashMap<String, String>) getItem(position);

        TextView textString = itemView.findViewById(R.id.text_string);

        textString.setText(map.get("ItemString"));

        return itemView;
    }
}
