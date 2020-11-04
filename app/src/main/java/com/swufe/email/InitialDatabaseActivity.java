package com.swufe.email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.swufe.email.data.Account;
import com.swufe.email.data.Config;

import org.litepal.tablemanager.Connector;

public class InitialDatabaseActivity extends AppCompatActivity implements Runnable{
//    用于在软件首次安装时创建数据库以及相关的数据表

    private static final String TAG = "InitialDatabaseActivity";

    static Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread thread = new Thread(InitialDatabaseActivity.this);
        thread.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 7) {
                    Log.i(TAG, "handleMessage: 数据库和表格已经创建");
                    Intent intent = new Intent(InitialDatabaseActivity.this, AddEmailActivity.class);
                    startActivity(intent);
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void run() {
//        基本数据库和表格的初始化
        Connector.getDatabase();
        Log.i(TAG, "run: 基本数据库和数据表的初始化开始");
//        Config 表格的配置
        Config config = new Config();
        config.setNAME("yeah.net");
        config.setPOP3HOST("pop.yeah.net");
        config.setPOP3PORT(110);
        config.setSMTPHOST("smtp.yeah.net");
        config.setSMTPPORT(25);
        config.save();
        Log.i(TAG, "run: yeah.net 配置完成");

        config = new Config();
        config.setNAME("163.com");
        config.setPOP3HOST("pop.163.com");
        config.setPOP3PORT(110);
        config.setSMTPHOST("smtp.163.com");
        config.setSMTPPORT(25);
        config.save();
        Log.i(TAG, "run: 163.com 配置完成");

//        config = new Config();
//        config.setNAME("qq.com");
//        config.setPOP3HOST("smtp.qq.com");
//        config.setPOP3PORT(465);
//        config.setSMTPHOST("pop.qq.com");
//        config.setSMTPPORT(110);
//        config.save();
//        Log.i(TAG, "run: qq.com 配置完成");

//        测试使用的帐号
//        Accont表的数据插入
//        Account account = new Account();
//        account.setStatus("2");
//        account.setEmailAddress("wang_yu_song@yeah.net");
//        account.setEmailPassword("JGAFQZMBIWANVRCD");
//        account.setPOP3HOST("pop.yeah.net");
//        account.setPOP3PORT(110);
//        account.setSMTPHOST("smtp.yeah.net");
//        account.setSMTPPORT(25);
//        account.save();
//        Log.i(TAG, "run: 个人账户建立");

        Message msg = handler.obtainMessage(7);
        handler.sendMessage(msg);

    }
}