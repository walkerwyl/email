package com.swufe.email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.swufe.email.data.Account;

import org.litepal.LitePal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LoadActivity extends AppCompatActivity {

    private static final String TAG = "LoadActivity";

    SharedPreferences sharedPreferences;

    static Handler handler = null;
    static Bundle bundle;
    public String emailAddress;
    String updateTime;
    String today;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_layout);

        //用于存取数据
        sharedPreferences = getSharedPreferences("myemail", Activity.MODE_PRIVATE);

        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        today = date.format(formatter);

        emailAddress = sharedPreferences.getString("email_address", "");
        updateTime = sharedPreferences.getString("update_time", "");

        if (emailAddress != null && !emailAddress.equals("")) {
            Bundle bundle = new Bundle();
            bundle.putString("email_address", emailAddress);
//                    Log.i(TAG, "handleMessage: 获得当前用户的邮件地址并发送到MainActivity" + emailAddress);
//                    Intent intent = new Intent(LoadActivity.this, MainActivity.class);
            Log.i(TAG, "handleMessage: 获得当前用户的邮件地址并发送到ReceiveEmailActivity" + emailAddress);
            if (!today.equals(updateTime)) {
                Intent receiveEmailIntent = new Intent(LoadActivity.this, ReceiveEmailActivity.class);
                receiveEmailIntent.putExtras(bundle);
                startActivity(receiveEmailIntent);
            } else {
                Intent intent = new Intent(LoadActivity.this, MainActivity.class);
                bundle = new Bundle();
                bundle.putString("email_address", emailAddress);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } else {
//                    初始化相关数据库和数据表
            Intent intent = new Intent(LoadActivity.this, InitialDatabaseActivity.class);
            startActivity(intent);
        }


    }
}