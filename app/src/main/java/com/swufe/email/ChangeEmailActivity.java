package com.swufe.email;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.swufe.email.data.Account;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ChangeEmailActivity extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener {
//    用于切换用户的当前帐号
//    使用最简单的listview实现即可
private static final String TAG = "ChangeEmailActivity";

    List<String> list_data;
    ListAdapter listAdapter;

    static Handler handler;

    ListView listView;
    ArrayAdapter arrayAdapter;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_email_layout);

        listView = findViewById(R.id.listView_email_address);

        Thread t = new Thread(ChangeEmailActivity.this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 6) {
                    list_data = (List<String>) msg.obj;
                    listAdapter = new ArrayAdapter<>(
                            ChangeEmailActivity.this,
                            R.layout.single_list_item, list_data);
                    listView.setAdapter(listAdapter);
                }
            }
        };


    }

    @Override
    public void run() {
//        从数据库中查找可用的帐号 2
        List<Account> accounts = LitePal.where("status = ?", "2")
                .find(Account.class);

        List<String> emailAddressList = new ArrayList<>();

        for (Account account : accounts) {
            emailAddressList.add(account.getEmailAddress());
        }

        Message msg = handler.obtainMessage(6);
        msg.obj = emailAddressList;
        handler.sendMessage(msg);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

    }
}