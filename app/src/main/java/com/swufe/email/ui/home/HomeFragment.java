package com.swufe.email.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.swufe.email.MainActivity;
import com.swufe.email.MessageDetailActivity;
import com.swufe.email.R;
import com.swufe.email.data.MyMessage;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment implements Runnable, AdapterView.OnItemClickListener {

    private static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;

    static Handler handler;
    HomeAdapter homeAdapter;
    ListView listViewINBOX;

    SharedPreferences sharedPreferences;


    String emailAddress;
    List<HashMap<String, String>> listData;

    @SuppressLint("HandlerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

//        Bundle bundle = getActivity().getIntent().getExtras();
//        emailAddress = bundle.getString("email_address", "");
//        Log.i(TAG, "onCreateView: 成功接受LoadActivity传递的参数" + emailAddress);
        sharedPreferences = getActivity().getSharedPreferences("myemail", Activity.MODE_PRIVATE);
        emailAddress = sharedPreferences.getString("email_address", "");

        listViewINBOX = root.findViewById(R.id.listView_inbox);

//        获得用户当前的邮箱地址后对其所属的本地邮件存储数据库进行检索,筛查出属于对应邮箱的邮件进行展示
        Thread t = new Thread(HomeFragment.this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 6) {
                    listData = (List<HashMap<String, String>>) msg.obj;
                    homeAdapter = new HomeAdapter(getContext(),
                            R.layout.list_item,
                            listData);
                    listViewINBOX.setAdapter(homeAdapter);
                    listViewINBOX.setOnItemClickListener(HomeFragment.this);
                }
                super.handleMessage(msg);
            }
        };

        return root;
    }

    @Override
    public void run() {
        List<HashMap<String, String>> listData = new ArrayList<>();
        List<MyMessage> myMessageList = LitePal.where("status = ?", "0")
                .find(MyMessage.class);

        for (MyMessage myMessage : myMessageList) {
            HashMap<String, String> map = new HashMap<>();
            map.put("ItemSubject", myMessage.getSubject());
            map.put("ItemDate", myMessage.getSentDate());
            map.put("ItemFrom", myMessage.getFrom());
//            Log.i(TAG, "run: ItemSubject=" + myMessage.getSubject());
//            Log.i(TAG, "run: ItemDate=" + myMessage.getSentDate());
//            Log.i(TAG, "run: ItemFrom=" + myMessage.getFrom());
            listData.add(map);
        }

        Message msg = handler.obtainMessage(6);
        msg.obj = listData;
        handler.sendMessage(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        TextView textSubject = view.findViewById(R.id.text_subject);
        TextView textFrom = view.findViewById(R.id.text_from);
        TextView textDate = view.findViewById(R.id.text_date);

        String subjectString = textSubject.getText().toString();
        String fromString = textFrom.getText().toString();
        String dateString = textDate.getText().toString();

        Log.i(TAG, "onItemClick: " + subjectString);
        Log.i(TAG, "onItemClick: " + fromString);
        Log.i(TAG, "onItemClick: " + dateString);

        Intent intent = new Intent(getContext(), MessageDetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("ItemSubject", subjectString);
        bundle.putString("ItemFrom", fromString);
        bundle.putString("ItemDate", dateString);

        intent.putExtras(bundle);
        startActivity(intent);
    }
}