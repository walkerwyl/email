package com.swufe.email;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.swufe.email.data.Account;

public class ManagerActivity extends AppCompatActivity implements Runnable{

    private static final String TAG = "ManagerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread thread = new Thread(ManagerActivity.this);
        thread.start();


    }

    @Override
    public void run() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String option = bundle.getString("option", "");
        switch (option) {
            case "add_email":
                Account account = new Account();
                account.setEmailAddress(bundle.getString("email_address", ""));
                account.setEmailPassword(bundle.getString("email_password", ""));
                account.setStatus("2");
                account.setPOP3HOST(bundle.getString("pop3_host", ""));
                account.setPOP3PORT(bundle.getInt("pop3_port", 25));
                account.setSMTPHOST(bundle.getString("smtp_host", ""));
                account.setSMTPPORT(bundle.getInt("smtp_port", 110));
//                帐号一定符合标准, 验证密码能否连接POP3, SMTP服务器, 若成功则保存到数据库中
                account.save();
                intent = new Intent(ManagerActivity.this, MainActivity.class);
                bundle.putString("email_address", account.getEmailAddress());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case "t":
                break;
            default:
                break;
        }
    }
}