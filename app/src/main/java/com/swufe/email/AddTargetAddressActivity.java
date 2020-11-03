package com.swufe.email;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ListView;

public class AddTargetAddressActivity extends AppCompatActivity implements Runnable {

    private static final String TAG = "AddTargetAddressActivit";

    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_target_address_layout);

        listView = findViewById(R.id.list_target_address);
//        自定义adapter或其他的方式

    }

    @Override
    public void run() {
//  从用户保存的通信记录当中选出数据
    }
}