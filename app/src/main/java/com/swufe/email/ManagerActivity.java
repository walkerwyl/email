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
import android.widget.TextView;
import android.widget.Toast;

import com.swufe.email.data.Account;
import com.swufe.email.data.MyMessage;

public class ManagerActivity extends AppCompatActivity implements Runnable{

    private static final String TAG = "ManagerActivity";

    SharedPreferences sharedPreferences;
    static Handler handler;
    Intent intent;
    Bundle bundle;

    TextView textView;

    String option;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_layout);

        Log.i(TAG, "onCreate: 管理界面");

        intent = getIntent();
        bundle = intent.getExtras();
        option = bundle.getString("option", "");
        Log.i(TAG, "onCreate: option=" + option);

        textView = findViewById(R.id.text_status);

        sharedPreferences = getSharedPreferences("myemail", Activity.MODE_PRIVATE);

        Thread thread = new Thread(ManagerActivity.this);
        thread.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 4 ) {
//                    intent = new Intent(ManagerActivity.this, MainActivity.class);
//                    intent.putExtras(bundle);
//                    startActivity(intent);

                } else if (msg.what == 5) {
                    textView.setText("草稿保存成功");
//                    延迟几秒返回主页面
                }

            }
        };

    }

    @Override
    public void run() {
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


//                同时将新注册的帐号作为当前使用帐号
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email_address", account.getEmailAddress());
                editor.apply();

                bundle = new Bundle();
                bundle.putString("email_address", account.getEmailAddress());

                Message msg = handler.obtainMessage(4);
                handler.sendMessage(msg);


                break;
            case "save_draft":
                Log.i(TAG, "run: 开始保存草稿");
                String emailAddress = bundle.getString("email_address", "");
                String emailSubject = bundle.getString("email_subject", "");
                String targetAddress = bundle.getString("target_address", "");
                String sendDate = bundle.getString("sent_date", "");
                String emailBody = bundle.getString("email_body", "");
                Log.i(TAG, "run: save_draft emailaddress=" + emailAddress);
                Log.i(TAG, "run: save_draft subject=" + emailSubject);

                // 草稿与其他不同, replyTo字段存储的是目标邮箱
                MyMessage draft = new MyMessage();
                draft.setStatus("1");
                draft.setFrom(emailAddress);
                draft.setSubject(emailSubject);
                draft.setReplyTo(targetAddress);
                draft.setSentDate(sendDate);
                draft.setContent(emailBody);
                draft.save();

                Log.i(TAG, "onClick: draft from=" + emailAddress);
                Log.i(TAG, "onClick: draft subject=" + emailSubject);
                Log.i(TAG, "onClick: draft body=" + emailBody);

                Log.i(TAG, "onClick: 草稿保存成功, 返回主页面");
                Message msg2 = handler.obtainMessage(4);
                handler.sendMessage(msg2);

                break;
            default:
                break;
        }
    }
}