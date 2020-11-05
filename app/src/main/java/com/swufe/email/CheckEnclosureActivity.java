package com.swufe.email;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.swufe.email.data.MyMessage;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CheckEnclosureActivity extends AppCompatActivity implements Runnable {
    private static final String TAG = "CheckEnclosureActivity";

    static Handler handler;

    List<HashMap<String, String>> list_data;
    ArrayList<String> backFilePathList = new ArrayList<>();

    ListView listView;
    SingleAdapter singleAdapter;

    Intent intent;
    Bundle bundle;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_email_layout);

        intent = getIntent();
        bundle = intent.getExtras();

        listView = findViewById(R.id.listView_email_address);

        Thread t = new Thread(CheckEnclosureActivity.this);
        t.start();

        handler = new Handler() {
            @SuppressLint("ResourceType")
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 7) {
                    list_data = (List<HashMap<String, String>>) msg.obj;
                    singleAdapter = new SingleAdapter(
                            CheckEnclosureActivity.this,
                            R.layout.single_list_item, list_data);
                    listView.setAdapter(singleAdapter);
                    listView.setEmptyView(findViewById(R.id.noaccount));
                }
            }
        };

    }

    @Override
    public void run() {
        List<HashMap<String, String>> data = new ArrayList<>();
        ArrayList<String> filePathList = bundle.getStringArrayList("file_path");

        for (String item : filePathList) {
            HashMap<String, String> map = new HashMap<>();
            map.put("ItemString", item);
            data.add(map);
        }

        Message msg = handler.obtainMessage(7);
        msg.obj = data;
        handler.sendMessage(msg);
    }
}