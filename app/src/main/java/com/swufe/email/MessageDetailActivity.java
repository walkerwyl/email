package com.swufe.email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.TextView;

import com.swufe.email.data.MyMessage;

import org.litepal.LitePal;

import java.util.List;

public class MessageDetailActivity extends AppCompatActivity implements Runnable{

    private static final String TAG = "MessageDetailActivity";

    static Handler handler;

    TextView detailSubject;
    TextView detailFrom;
    TextView detailDate;
    TextView detailBody;


    String subjectString;
    String fromString;
    String dateString;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail_layout);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        subjectString = bundle.getString("ItemSubject", "");
        fromString = bundle.getString("ItemFrom", "");
        dateString = bundle.getString("ItemDate", "");

        detailSubject = findViewById(R.id.text_detail_subject);
        detailFrom = findViewById(R.id.text_detail_from);
        detailDate = findViewById(R.id.text_detail_date);
        detailBody = findViewById(R.id.text_detail_body);

        detailSubject.setText(subjectString);
        detailFrom.setText(fromString);
        detailDate.setText(dateString);


        Thread thread = new Thread(MessageDetailActivity.this);
        thread.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 8) {
                    MyMessage myMessage = (MyMessage) msg.obj;
                    detailBody.setText(myMessage.getContent());
                }
                super.handleMessage(msg);
            }
        };

    }

    @Override
    public void run() {
//        根据提供的信息查询, 返回最后显示在界面上
        List<MyMessage> myMessageList = LitePal.where("sentDate = ?", dateString)
                .limit(1)
                .find(MyMessage.class);
        MyMessage myMessage = myMessageList.get(0);

        Message msg = handler.obtainMessage(8);
        msg.obj = myMessage;
        handler.sendMessage(msg);

    }
}