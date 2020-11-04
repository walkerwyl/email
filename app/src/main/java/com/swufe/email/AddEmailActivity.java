package com.swufe.email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.swufe.email.data.Account;
import com.swufe.email.data.Config;

import org.litepal.LitePal;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddEmailActivity extends AppCompatActivity implements Runnable, View.OnClickListener {

    private static final String TAG = "AddEmailActivity";

//     TODO: 20-10-28 准确保存用户输入的数据到数据库中没有问题
//      问题在于:
//      2. 若用户输入错误时,提示用户重新输入(不指明可能的问题?)
//      3. 前提条件,用户输入的数据不存在
//        对于LitePal数据库的一些操作的判断需要更复杂的一些查询语句

    EditText email_address;
    EditText email_password;
    EditText pop3_host;
    EditText pop3_port;
    EditText smtp_host;
    EditText smtp_port;


    static Handler handler;
    static Bundle bundle;

    String[] array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_email_layout);


        email_address = findViewById(R.id.edit_email_address);
        email_password = findViewById(R.id.edit_email_password);
        pop3_host = findViewById(R.id.edit_pop3_host);
        pop3_port = findViewById(R.id.edit_pop3_port);
        smtp_host = findViewById(R.id.edit_smtp_host);
        smtp_port = findViewById(R.id.edit_smtp_port);

        Button submit = findViewById(R.id.btn_submit);
        submit.setOnClickListener(AddEmailActivity.this);

        ImageView showCode = findViewById(R.id.show_code);
        showCode.setOnClickListener(AddEmailActivity.this);

        email_address.addTextChangedListener(new TextWatcher() {
            //        https://blog.csdn.net/erweidetaiyangxi/article/details/78988388
            //        对用户输入的邮箱地址进行监听,当用户结束输入时自动判断
            //        1. 地址是否合法 2. 该地址对应的邮箱配置是否存储在数据库中
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("HandlerLeak")
            @Override
            public void afterTextChanged(Editable editable) {
                String emailAddress = email_address.getText().toString();
                if (!validMail(emailAddress)) {
                    Toast.makeText(AddEmailActivity.this, "邮箱不符合标准", Toast.LENGTH_SHORT).show();
                }
                else {
//                    邮箱有效时截取后半段自动填写服务器地址及端口
                    array = emailAddress.split("@");

                    Thread thread = new Thread(AddEmailActivity.this);
                    thread.start();

                    handler = new Handler() {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            if (msg.what == 7) {
                                List<Config> configList = (List<Config>) msg.obj;
                                for (Config config : configList) {
                                    if (array[1].equals(config.getNAME())) {
                                        // TODO: 20-11-3 端口号是否完全变成字符串类型, 存在数值与字符串间的转换 
                                        pop3_host.setText(config.getPOP3HOST());
                                        pop3_port.setText(""+config.getPOP3PORT());
                                        smtp_host.setText(config.getSMTPHOST());
                                        smtp_port.setText(""+config.getSMTPPORT());
                                    }
                                }
                            }
                            super.handleMessage(msg);
                        }
                    };
                }
            }
        });

    }

    @Override
    public void run() {
//        从数据库中取出数据并返回处理
        List<Config> configList = LitePal.findAll(Config.class);

        Message msg = handler.obtainMessage(7);
        msg.obj = configList;
        handler.sendMessage(msg);
    }

    private boolean isValid(EditText... editTexts) {
//        当且仅当关键信息非空时进行相关的操作
        for (EditText editText : editTexts)
            if (editText.getText().toString().equals("")) return false;
        return true;
    }

    private boolean validMail (String source) {
//        利用正则表达式对字符串进行判断,若符合一般标准且没有多于的字符,则返回true
        String regex = "[a-zA-z.[0-9]]*@[a-zA-z[0-9]]*\\.(com|cn|net)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        return matcher.find() && source.equals(matcher.group());
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
//                点击按钮直接跳转到ManagerActivity活动中,处理相关的数据
                if (isValid(email_address, email_password,
                        pop3_host, pop3_port, smtp_host, smtp_port)
                    && validMail(email_address.getText().toString())) {
                    String emailAddress = email_address.getText().toString();
                    String emailPassword = email_password.getText().toString();
                    String pop3Host = pop3_host.getText().toString();
                    int pop3Port = Integer.parseInt(pop3_port.getText().toString());
                    String smtpHost = smtp_host.getText().toString();
                    int smtpPort = Integer.parseInt(smtp_port.getText().toString());

                    Intent intent = new Intent(AddEmailActivity.this, ManagerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("option", "add_email");
                    bundle.putString("email_address", emailAddress);
                    bundle.putString("email_password", emailPassword);
                    bundle.putString("pop3_host", pop3Host);
                    bundle.putInt("pop3_port", pop3Port);
                    bundle.putString("smtp_host", smtpHost);
                    bundle.putInt("smtp_port", smtpPort);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.show_code:
//                用户调节密码的可见性
                if (email_password.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                    email_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    email_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                break;
            default:
                break;
        }
    }
}