package com.swufe.email;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.swufe.email.data.MyMessage;

import org.litepal.LitePal;

import java.util.List;

public class QueryLitePalActivity extends AppCompatActivity {

    private static final String TAG = "QueryLitePalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        对数据库中的数据进行检查展示
        List<MyMessage> myMessageList = LitePal.findAll(MyMessage.class);
        Log.i(TAG, "onCreate: myMessageList size=" + myMessageList.size());
        for (MyMessage myMessage : myMessageList) {
            Log.i(TAG, "onCreate: sentDate=" + myMessage.getSentDate());
        }
    }
}