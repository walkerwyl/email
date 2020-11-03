package com.swufe.email;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;

import com.swufe.email.data.Account;
import com.swufe.email.data.Config;
import com.swufe.email.data.MyMessage;

import org.litepal.LitePal;

import java.util.List;

public class QueryLitePalActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "QueryLitePalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_layout);

        Button btn_config = findViewById(R.id.btn_config);
        Button btn_account = findViewById(R.id.btn_account);
        Button btn_message = findViewById(R.id.btn_message);

        btn_config.setOnClickListener(QueryLitePalActivity.this);
        btn_account.setOnClickListener(QueryLitePalActivity.this);
        btn_message.setOnClickListener(QueryLitePalActivity.this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_config:
                List<Config> configList = LitePal.findAll(Config.class);
                for (Config config : configList) {
                    Log.i(TAG, "onClick: Name=" + config.getNAME());
                    Log.i(TAG, "onClick: POP3HOST=" + config.getPOP3HOST());
                    Log.i(TAG, "onClick: POP3PORT=" + config.getPOP3PORT());
                    Log.i(TAG, "onClick: SMTPHOST=" + config.getSMTPHOST());
                    Log.i(TAG, "onClick: SMTPPORT=" + config.getSMTPPORT());
                    Log.i(TAG, "onClick: --------------------------------");
                }
                break;
            case R.id.btn_account:
                List<Account> accountList = LitePal.findAll(Account.class);
                for (Account account : accountList) {
                    Log.i(TAG, "onClick: emailAddress=" + account.getEmailAddress());
                    Log.i(TAG, "onClick: emailPassword=" + account.getEmailPassword());
                    Log.i(TAG, "onClick: POP3HOST=" + account.getPOP3HOST());
                    Log.i(TAG, "onClick: POP3PORT=" + account.getPOP3PORT());
                    Log.i(TAG, "onClick: SMTPHOST=" + account.getSMTPHOST());
                    Log.i(TAG, "onClick: SMTPPORT=" + account.getSMTPPORT());
                    Log.i(TAG, "onClick: ----------------------------------------");
                }
                break;
            case R.id.btn_message:
                List<MyMessage> myMessageList = LitePal.findAll(MyMessage.class);
                for (MyMessage myMessage : myMessageList) {
                    Log.i(TAG, "onClick: Subject=" + myMessage.getSubject());
                    Log.i(TAG, "onClick: From=" + myMessage.getFrom());
                    Log.i(TAG, "onClick: ReplyTo=" + myMessage.getReplyTo());
                    Log.i(TAG, "onClick: SentDate=" + myMessage.getSentDate());
                    Log.i(TAG, "onClick: ContentType=" + myMessage.getContentType());
                    Log.i(TAG, "onClick: Content=" + myMessage.getContent());
                    Log.i(TAG, "onClick: MessageNumber" + myMessage.getMessageNumber());
                }
                break;
            default:
                break;
        }
    }
}