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
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.swufe.email.data.Account;

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

            @Override
            public void afterTextChanged(Editable editable) {
                String emailAddress = email_address.getText().toString();
                if (!validMail(emailAddress)) {
                    Toast.makeText(AddEmailActivity.this, "邮箱不符合标准", Toast.LENGTH_SHORT).show();
                }
                else {

                }
            }
        });

        Button submit = findViewById(R.id.btn_submit);
        submit.setOnClickListener(AddEmailActivity.this);

        ImageButton showCode = findViewById(R.id.show_code);
        showCode.setOnClickListener(AddEmailActivity.this);

    }

    @Override
    public void run() {
//        当且仅当用户填写完毕时,进行相关的判断
        if (isValid(email_address, email_password,
                pop3_host, pop3_port, smtp_host, smtp_port)) {
            account.setEmailAddress(email_address.getText().toString());
            account.setEmailPassword(email_password.getText().toString());
            account.setPOP3HOST(pop3_host.getText().toString());
//            pop3_port是数字
            account.setPOP3PORT(Integer.parseInt(pop3_port.getText().toString()));
            account.setSMTPHOST(smtp_host.getText().toString());
//            smtp_port是数字
            account.setSMTPPORT(Integer.parseInt(smtp_port.getText().toString()));

            if (isValid(account)) {
//                验证邮箱的有效性后返回主线程并写入数据库中
//                邮箱配置有效
                account.setStatus("2");
                // TODO: 20-10-28 对于线程返回的状态值进行统一的管理
                Message msg = handler.obtainMessage(7);
                msg.obj = account;
                handler.sendMessage(msg);
            } else {
//                邮箱配置无效舍弃,直接返回
                Message msg = handler.obtainMessage(8);
                handler.sendMessage(msg);
            }

        }
    }

    private boolean isValid(EditText... editTexts) {
//        当且仅当关键信息非空时进行相关的操作
        for (EditText editText : editTexts)
            if (editText.getText().toString().equals("")) return false;
        return true;
    }

    private boolean isValid(Account account) {
//        使用用户填写的信息进行连接操作若操作成功则允许向数据库中填写
//        否则使用Toast提示用户信息出错

        return true;
    }

    private boolean validMail (String source) {
//        利用正则表达式对字符串进行判断,若符合一般标准且没有多于的字符,则返回true
        String regex = "[a-zA-z.[0-9]]*@[a-zA-z[0-9]]*\\.com";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        return matcher.find() && source.equals(matcher.group());
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                Thread thread = new Thread(AddEmailActivity.this);
                thread.start();

                handler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        if (msg.what == 7) {
                            account = (Account) msg.obj;
                            account.save();

                            String emailAddress = account.getEmailAddress();
                            Bundle bundle = new Bundle();
                            bundle.putString("email_address", emailAddress);
                            Log.i(TAG, "handleMessage: email_address=" + emailAddress);

                            Intent intent = new Intent(AddEmailActivity.this, MainActivity.class);
                            startActivity(intent, bundle);
                        } else if (msg.what == 8) {

                        }
                        super.handleMessage(msg);
                    }
                };
                break;
            case R.id.show_code:
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