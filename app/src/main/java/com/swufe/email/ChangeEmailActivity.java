package com.swufe.email;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.swufe.email.data.Account;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChangeEmailActivity extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener {
//    用于切换用户的当前帐号
//    使用最简单的listview实现即可
private static final String TAG = "ChangeEmailActivity";

    List<HashMap<String, String>> list_data;
    ListAdapter listAdapter;

    static Handler handler;

    SharedPreferences sharedPreferences;
    ListView listView;
    SingleAdapter singleAdapter;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_email_layout);

        sharedPreferences = getSharedPreferences("myemail", Activity.MODE_PRIVATE);

        listView = findViewById(R.id.listView_email_address);

        Thread t = new Thread(ChangeEmailActivity.this);
        t.start();

        handler = new Handler() {
            @SuppressLint("ResourceType")
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 6) {
                    list_data = (List<HashMap<String, String>>) msg.obj;
                    singleAdapter = new SingleAdapter(
                            ChangeEmailActivity.this,
                            R.layout.single_list_item, list_data);
                    listView.setAdapter(singleAdapter);
                    listView.setEmptyView(findViewById(R.id.noaccount));
                    listView.setOnItemClickListener(ChangeEmailActivity.this);
                }
            }
        };


    }

    @Override
    public void run() {
//        从数据库中查找可用的帐号 2
        List<Account> accounts = LitePal.where("status = ?", "2")
                .find(Account.class);

        List<HashMap<String, String>> emailAddressList = new ArrayList<>();

        for (Account account : accounts) {
            HashMap<String, String> map = new HashMap<>();
            map.put("ItemString", account.getEmailAddress());
            Log.i(TAG, "run: account=" + account.getEmailAddress());
            emailAddressList.add(map);
        }

        Message msg = handler.obtainMessage(6);
        msg.obj = emailAddressList;
        handler.sendMessage(msg);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        TextView textView = view.findViewById(R.id.text_string);

        String currentEmailAddress = textView.getText().toString();
        Log.i(TAG, "onItemClick: currentEmailAddress" + currentEmailAddress);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email_address", currentEmailAddress);
        editor.apply();

        Intent intent = new Intent(ChangeEmailActivity.this, MainActivity.class);
        startActivity(intent);
    }
}