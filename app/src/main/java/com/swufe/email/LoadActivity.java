package com.swufe.email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.swufe.email.data.Account;

import org.litepal.LitePal;

import java.util.List;

public class LoadActivity extends AppCompatActivity implements Runnable {

    private static final String TAG = "LoadActivity";

    static Handler handler;
    static Bundle bundle;
    private Account account;
    private String emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_layout);

        Thread thread = new Thread(LoadActivity.this);
        thread.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 5) {
                    bundle = (Bundle) msg.obj;
                    emailAddress = bundle.getString("email_address", "");

                    Bundle bundle = new Bundle();
                    bundle.putString("email_address", emailAddress);
                    Log.i(TAG, "handleMessage: 获得当前用户的邮件地址并发送到MainActivity" + emailAddress);
                    Intent intent = new Intent(LoadActivity.this, MainActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (msg.what == 6) {
                    Intent intent = new Intent(LoadActivity.this, AddEmailActivity.class);
                    startActivity(intent);
                }
                super.handleMessage(msg);
            }
        };

    }

    @Override
    public void run() {
        // TODO: 20-10-28 没有进行相关的错误处理
        List<Account> accounts = LitePal.select("emailAddress")
                .where("status = ?", "2")
                .limit(1)
                .find(Account.class);

//        若查询到状态为2的记录则直接进入展示界面
        if (accounts.size() != 0) {
            String emailAddress = accounts.get(0).getEmailAddress();
            Log.i(TAG, "run: emailAddress=" + emailAddress );

            Bundle bundle = new Bundle();
            bundle.putString("email_address", emailAddress);

            Message msg = handler.obtainMessage(5);
            msg.obj = bundle;
            handler.sendMessage(msg);
        } else  {
//            否则将直接跳转到注册新邮箱界面
            Message msg = handler.obtainMessage(6);
            handler.sendMessage(msg);
        }


    }
}