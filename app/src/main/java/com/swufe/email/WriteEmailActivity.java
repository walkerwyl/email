package com.swufe.email;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import com.google.android.material.textfield.TextInputEditText;
import com.swufe.email.data.Account;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class WriteEmailActivity extends AppCompatActivity implements View.OnClickListener, Runnable{

    private static final String TAG = "WriteEmailActivity";

    Intent intent;
    Bundle bundle;
    static Handler handler;


    Spinner spinner;
    SpinnerAdapter spinnerAdapter;

    ImageButton addTargetAddress;
    ImageButton sendEmailButton;
    EditText editTargetAddress;
    EditText editEmailSubject;
    TextInputEditText textInputEmailBody;

    String emailAddress;
    String targetAddress;
    String emailSubject;
    String emailBody;

    // TODO: 20-10-28 使用下拉列表让用户选择自己的发送邮件的邮箱帐号
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_email_layout);

        intent = getIntent();
        bundle = intent.getExtras();
        emailAddress = bundle.getString("email_address", "");
        Log.i(TAG, "onCreate: 写邮件页面获得当前用户身份emailAddress=" + emailAddress);

        editTargetAddress = findViewById(R.id.edit_target_address);
        editEmailSubject = findViewById(R.id.edit_email_subject);
        textInputEmailBody = findViewById(R.id.text_input_emdil_body);

        addTargetAddress = findViewById(R.id.btn_add_target_address);
        addTargetAddress.setOnClickListener(WriteEmailActivity.this);

        sendEmailButton = findViewById(R.id.btn_send);
        sendEmailButton.setOnClickListener(WriteEmailActivity.this);

//        收集用户的邮件数据
//        targetAddress = editTargetAddress.getText().toString();




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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_target_address:
//                跳转到用户最近的通信地址,列表展示,需要新的活动返回数据startActivityForResult
                Bundle bundle = new Bundle();
                bundle.putString("email_address", "");
                Intent intent = new Intent(WriteEmailActivity.this, AddTargetAddressActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_send:
//                执行发送邮件的任务
//                收集数据
                targetAddress = editTargetAddress.getText().toString();
                emailSubject = editEmailSubject.getText().toString();
                emailBody = textInputEmailBody.getText().toString();

//                验证数据是否完备
//                邮箱格式是否正确
//                targetAddress是否格式正确


                Bundle sendEmailBundle = new Bundle();
                sendEmailBundle.putString("email_address", emailAddress);
                sendEmailBundle.putString("target_address", targetAddress);
                sendEmailBundle.putString("email_subject", emailSubject);
                sendEmailBundle.putString("email_body", emailBody);
                Intent sendEmailIntent = new Intent(WriteEmailActivity.this, SendEmailActivity.class);
                sendEmailIntent.putExtras(sendEmailBundle);
                startActivity(sendEmailIntent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        接收用户选择地址界面返回的数据,字符串并放入edittext中
        if (1 == requestCode && 2 == resultCode) {
            Bundle bundle = data.getExtras();
            String targetAddressString = bundle.getString("targer_address_list", "");
            editTargetAddress.setText(targetAddressString);
        }
        super.onActivityResult(requestCode, resultCode, data);
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