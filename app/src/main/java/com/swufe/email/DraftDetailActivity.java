package com.swufe.email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.swufe.email.data.MyMessage;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DraftDetailActivity extends AppCompatActivity implements  View.OnClickListener, Runnable{

    private static final String TAG = "DraftDetailActivity";

    static Handler handler;

    TextView textViewTargetAddress;
    TextView textViewEmailSubject;
    TextInputEditText textInputEditTextEmailBody;

    Intent intent;
    Bundle bundle;

    String subjectString;
    String fromString;
    String dateString;

    String emailAddress;
    String targetAddress;
    String emailSubject;
    String emailBody;
    ArrayList<String> filePathArrayList;//传递给发送邮件活动
    ArrayList<String> fileNameArrayList;//展示附件列表数据源

    Date date;
    SimpleDateFormat ft;

    Spinner spinner;
    SpinnerAdapter spinnerAdapter;

    ImageButton addTargetAddress;
    ImageButton sendEmailButton;
    ImageButton attachFileButton;
    ImageView backView;
    Button saveDraftButton;
    EditText editTargetAddress;
    EditText editEmailSubject;
    TextInputEditText textInputEmailBody;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_email_layout);

        Log.i(TAG, "onCreate: enter draftdetail");

        intent = getIntent();
        bundle = intent.getExtras();

        subjectString = bundle.getString("ItemSubject", "");
        fromString = bundle.getString("ItemFrom", "");
        dateString = bundle.getString("ItemDate", "");
        Log.i(TAG, "onCreate: subjectString=" + subjectString);
        Log.i(TAG, "onCreate: dateString=" + dateString);

        textViewTargetAddress = findViewById(R.id.edit_target_address);

        textViewEmailSubject = findViewById(R.id.edit_email_subject);
        textViewEmailSubject.setText(subjectString);

        textInputEditTextEmailBody = findViewById(R.id.text_input_emdil_body);


        backView = findViewById(R.id.btn_back);
        backView.setOnClickListener(DraftDetailActivity.this);

        saveDraftButton = findViewById(R.id.btn_save_draft);
        saveDraftButton.setOnClickListener(DraftDetailActivity.this);

        editTargetAddress = findViewById(R.id.edit_target_address);
        editEmailSubject = findViewById(R.id.edit_email_subject);
        textInputEmailBody = findViewById(R.id.text_input_emdil_body);

        addTargetAddress = findViewById(R.id.btn_add_target_address);
        addTargetAddress.setOnClickListener(DraftDetailActivity.this);

        sendEmailButton = findViewById(R.id.btn_send);
        sendEmailButton.setOnClickListener(DraftDetailActivity.this);

        attachFileButton = findViewById(R.id.btn_attach_file);
        attachFileButton.setOnClickListener(DraftDetailActivity.this);

        Thread thread = new Thread(DraftDetailActivity.this);
        thread.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 8) {
                    MyMessage myMessage = (MyMessage) msg.obj;
                    textViewTargetAddress.setText(myMessage.getReplyTo());
                    textInputEditTextEmailBody.setText(myMessage.getContent());
                }
                super.handleMessage(msg);
            }
        };



    }

    @Override
    public void run() {
//        根据提供的信息查询, 返回最后显示在界面上
        List<MyMessage> myMessageList = LitePal.where("status = ? and subject = ? and sendDate = ?", "1", subjectString, dateString)
                .limit(1)
                .find(MyMessage.class);
        MyMessage myMessage = myMessageList.get(0);

        Message msg = handler.obtainMessage(8);
        msg.obj = myMessage;
        handler.sendMessage(msg);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
//                执行发送邮件的任务
//                收集数据
                targetAddress = editTargetAddress.getText().toString();
                emailSubject = editEmailSubject.getText().toString();
                emailBody = textInputEmailBody.getText().toString();

//                判断必要信息, 否则不执行
                if (emailSubject.equals("")) {
                    Toast.makeText(DraftDetailActivity.this,
                            "请填写主题", Toast.LENGTH_SHORT).show();
                    break;
                } else if (targetAddress.equals("") || !isValidTargetAddress(targetAddress)) {
                    Toast.makeText(DraftDetailActivity.this,
                            "请填写收信人", Toast.LENGTH_SHORT).show();
                    break;
                }

                Bundle sendEmailBundle = new Bundle();
                sendEmailBundle.putString("email_address", emailAddress);
                sendEmailBundle.putString("target_address", targetAddress);
                sendEmailBundle.putString("email_subject", emailSubject);
                sendEmailBundle.putString("email_body", emailBody);
                sendEmailBundle.putStringArrayList("filePathArrayList", filePathArrayList);

                Intent sendEmailIntent = new Intent(DraftDetailActivity.this, SendEmailActivity.class);
                sendEmailIntent.putExtras(sendEmailBundle);
                startActivity(sendEmailIntent);
                break;
            case R.id.btn_attach_file:
                Intent attachIntent = new Intent(Intent.ACTION_GET_CONTENT);
                attachIntent.setType("*/*");//无类型限制
                attachIntent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(attachIntent, 2);
                break;
            case R.id.btn_save_draft:
                targetAddress = editTargetAddress.getText().toString();
                emailSubject = editEmailSubject.getText().toString();
                emailBody = textInputEmailBody.getText().toString();

                //                判断必要信息, 否则不执行
                if (emailSubject.equals("")) {
                    Toast.makeText(DraftDetailActivity.this,
                            "请填写主题", Toast.LENGTH_SHORT).show();
                    break;
                } else if (targetAddress.equals("") || !isValidTargetAddress(targetAddress)) {
                    Toast.makeText(DraftDetailActivity.this,
                            "请填写收信人", Toast.LENGTH_SHORT).show();
                    break;
                }

                Intent saveDraftIntent = new Intent(DraftDetailActivity.this, ManagerActivity.class);
                Bundle saveDraftBundle = new Bundle();

                saveDraftBundle.putString("option", "save_draft");
                saveDraftBundle.putString("email_address", emailAddress);
                saveDraftBundle.putString("email_subject", emailSubject);
                saveDraftBundle.putString("target_address", targetAddress);
                saveDraftBundle.putString("email_body", emailBody);
                saveDraftBundle.putString("send_date", ft.format(date));
                Log.i(TAG, "onClick: send_date=" + ft.format(date));

                saveDraftIntent.putExtras(saveDraftBundle);
                startActivity(saveDraftIntent);

                break;
            case R.id.btn_back:
                intent = new Intent(DraftDetailActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private boolean isValidTargetAddress (String source) {
        String[] array = source.split(";");
        for (String item : array) {
            if (!validMail(item)) return false;
        }
        return true;
    }

    private boolean validMail (String source) {
//        利用正则表达式对字符串进行判断,若符合一般标准且没有多于的字符,则返回true
        String regex = "[a-zA-z.[0-9]]*@[a-zA-z[0-9]]*\\.(com|cn|net)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        return matcher.find() && source.equals(matcher.group());
    }
}