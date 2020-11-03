package com.swufe.email;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.swufe.email.data.Account;
import com.swufe.email.data.Config;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.List;

public class CreateDatabaseActivity extends AppCompatActivity {
    private static final String TAG = "CreateDatabaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_database_layout);

        Button createDatabase = findViewById(R.id.create_database);
        createDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connector.getDatabase();
                Toast.makeText(CreateDatabaseActivity.this,"数据库可能创建成功", Toast.LENGTH_LONG).show();
            }
        });

        Button addData = findViewById(R.id.btn_add);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Account account = new Account();
//                account.setEmailAddress("wang_yu_song@yeah.net");
//                account.setEmailPassword("JGAFQZMBIWANVRCD");
//                account.setStatus("2");
//                account.setPOP3HOST("pop3.yeah.net");
//                account.setSMTPHOST("smtp.yeah.net");
//                account.setSMTPPORT(25);
//                account.save();
                Config config = new Config();
                config.setNAME("126.com");
                config.setPOP3HOST("smtp.126.com");
                config.setPOP3PORT(25);
                config.setSMTPHOST("pop.126.com");
                config.setSMTPPORT(110);
                config.save();
                Toast.makeText(CreateDatabaseActivity.this,"数据库可能插入一条数据", Toast.LENGTH_LONG).show();
            }
        });

        Button deleteData = findViewById(R.id.btn_delete);
        deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LitePal.deleteAll(Account.class, "emailAddress = ?", "wang_yu_song@yeah.net");
            }
        });


        Button queryData = findViewById(R.id.btn_query);
        queryData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                List<Account> accounts = LitePal.findAll(Account.class);
//                for (Account account : accounts) {
//                    Log.i(TAG, "onClick: account emailAddress=" + account.getEmailAddress() );
//                    Log.i(TAG, "onClick: account emailPassword=" + account.getEmailPassword());
//                    Log.i(TAG, "onClick: account status=" + account.getStatus());
//                    Log.i(TAG, "onClick: account POP3HOST=" + account.getPOP3HOST());
//                    Log.i(TAG, "onClick: account SMTPHOST=" + account.getSMTPHOST());
//                    Log.i(TAG, "onClick: account SMTPPORT=" + account.getSMTPPORT());
//                }
                List<Config> configList = LitePal.findAll(Config.class);
                for (Config config : configList) {
                    Log.i(TAG, "onClick: NAME=" + config.getNAME());
                    Log.i(TAG, "onClick: SMTPHOST=" + config.getSMTPHOST());
                    Log.i(TAG, "onClick: SMTPPORT=" + config.getSMTPPORT());
                    Log.i(TAG, "onClick: POP3HOST=" + config.getPOP3HOST());
                    Log.i(TAG, "onClick: POP3PORT=" + config.getPOP3PORT());
                }
            }
        });

        Button updateData = findViewById(R.id.btn_update);
        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Account account = new Account();
                account.setStatus("2");
                account.updateAll("emailAddress = ?", "wang_yu_song@yeah.net");
            }
        });

    }
}