package com.swufe.email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import com.swufe.email.data.Account;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class WriteEmailActivity extends AppCompatActivity implements Runnable{

    private static final String TAG = "WriteEmailActivity";

    Intent intent;
    Bundle bundle;
    static Handler handler;

    Spinner spinner;
    SpinnerAdapter spinnerAdapter;

    String emailAddress;

    // TODO: 20-10-28 使用下拉列表让用户选择自己的发送邮件的邮箱帐号
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_email_layout);

        intent = getIntent();
        bundle = intent.getExtras();
        emailAddress = bundle.getString("email_address", "");
        Log.i(TAG, "onCreate: 写邮件页面获得当前用户身份emailAddress=" + emailAddress);


        Thread thread = new Thread(WriteEmailActivity.this);
        thread.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 9) {
//                    根据从数据库中找出的数据设置下拉列表
                    List<String> emailAddressList = (ArrayList<String>) msg.obj;
                    spinner = findViewById(R.id.spinner_email_address);
                    spinnerAdapter = new ArrayAdapter<String>(WriteEmailActivity.this,
                            android.R.layout.simple_spinner_item, emailAddressList);
                    spinner.setAdapter(spinnerAdapter);
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void run() {
//        从数据库中选择所有的数据并保存在数组中返回
        List<Account> accounts = LitePal.findAll(Account.class);
        List<String> emailAddressList = new ArrayList<>();
        for (Account account : accounts) {
            emailAddressList.add(account.getEmailAddress());
        }

        Message msg = handler.obtainMessage(9);
        msg.obj = emailAddressList;
        handler.sendMessage(msg);
    }
}