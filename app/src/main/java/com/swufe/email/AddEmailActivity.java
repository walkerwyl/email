package com.swufe.email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.ThemedSpinnerAdapter;

import com.swufe.email.data.Account;

public class AddEmailActivity extends AppCompatActivity implements Runnable{

    private static final String TAG = "AddEmailActivity";

//     TODO: 20-10-28 准确保存用户输入的数据到数据库中没有问题
//      问题在于:
//      1. 验证用户输入的有效性
//      2. 若用户输入错误时,提示用户重新输入(不指明可能的问题?)
//      3. 前提条件,用户输入的数据不存在
//        对于LitePal数据库的一些操作的判断需要更复杂的一些查询语句

    EditText email_address;
    EditText email_password;
    EditText pop3_host;
    EditText pop3_port;
    EditText smtp_host;
    EditText smtp_port;

    Account account;
    static Handler handler;
    static Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_email_layout);

        account = new Account();

        email_address = findViewById(R.id.edit_email_address);
        email_password = findViewById(R.id.edit_email_password);
        pop3_host = findViewById(R.id.edit_pop3_host);
        pop3_port = findViewById(R.id.edit_pop3_port);
        smtp_host = findViewById(R.id.edit_smtp_host);
        smtp_port = findViewById(R.id.edit_smtp_port);

        Thread thread = new Thread(AddEmailActivity.this);
        thread.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 7) {

                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void run() {
//        当且仅当用户填写完毕时,进行相关的判断
        if (isValid(email_address, email_password,
                pop3_host, pop3_port, smtp_host, smtp_port)) {


            // TODO: 20-10-28 对于线程返回的状态值进行统一的管理
            Message msg = handler.obtainMessage(7);
            msg.obj = account;
            handler.sendMessage(msg);
        }
    }

    private boolean isValid(EditText... editTexts) {
//        当且仅当关键信息非空时进行相关的操作
        for (EditText editText : editTexts)
            if (editText.getText().toString().equals("")) return false;
        return true;
    }
}