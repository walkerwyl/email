package com.swufe.email.ui.slideshow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.RelativeDateTimeFormatter;
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

import com.swufe.email.MessageDetailActivity;
import com.swufe.email.R;
import com.swufe.email.data.MyMessage;
import com.swufe.email.ui.gallery.GalleryAdapter;
import com.swufe.email.ui.gallery.GalleryFragment;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SlideshowFragment extends Fragment implements Runnable, AdapterView.OnItemClickListener
                                                            , AdapterView.OnItemLongClickListener{

    private static final String TAG = "SlideshowFragment";

    String emailAddress;
    List<HashMap<String, String>> listData = new ArrayList<>();

    ListView listViewSend;
    static Handler handler;
    SlideshowAdapter slideshowAdapter;
    SharedPreferences sharedPreferences;

    private SlideshowViewModel slideshowViewModel;

    View root;

    @SuppressLint("HandlerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        root = inflater.inflate(R.layout.fragment_slideshow, container, false);


        sharedPreferences = getActivity().getSharedPreferences("myemail", Activity.MODE_PRIVATE);
        emailAddress = sharedPreferences.getString("email_address", "");

        listViewSend = root.findViewById(R.id.listView_send);

        Thread t = new Thread(SlideshowFragment.this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 7) {
                    listData = (List<HashMap<String, String>>) msg.obj;
                    slideshowAdapter = new SlideshowAdapter(getContext(),
                            R.layout.list_item,
                            listData);
                    listViewSend.setAdapter(slideshowAdapter);
                    listViewSend.setEmptyView(root.findViewById(R.id.nosend));
                    listViewSend.setOnItemClickListener(SlideshowFragment.this);
                    listViewSend.setOnItemLongClickListener(SlideshowFragment.this);
                }
            }
        };

        return root;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        TextView textSubject = view.findViewById(R.id.text_subject);
        TextView textFrom = view.findViewById(R.id.text_from);
        TextView textDate = view.findViewById(R.id.text_date);

        String subjectString = textSubject.getText().toString();
        String fromString = textFrom.getText().toString();
        String dateString = textDate.getText().toString();
        Log.i(TAG, "onItemClick: send date 传递" + dateString);

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

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
//        对用户选中的数据进行删除
        //长按按钮出现提示框,点击确定,确认删除当前选中的数据
        TextView textViewSubject = view.findViewById(R.id.text_subject);
        TextView textViewSendDate = view.findViewById(R.id.text_date);

        String subject = textViewSubject.getText().toString();
        String sendDate = textViewSendDate.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示")
                .setMessage("请确认是否删除当前数据")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Log.i(TAG, "onClick: 点击了确认按钮");
                        slideshowAdapter.remove(listViewSend.getItemAtPosition(position));
                    }
                }).setNegativeButton("否",null);
        builder.create().show();
//        同时从数据库中删除数据
        LitePal.deleteAll(MyMessage.class, "status = ? and subject = ? and sendDate = ?", "2", subject, sendDate);
        Log.i(TAG, "onItemLongClick: send date=" +  sendDate);

        return true;
    }

    @Override
    public void run() {
        List<HashMap<String, String>> listData = new ArrayList<>();
        List<MyMessage> myMessageList = LitePal.where("status = ? and from = ?", "2", emailAddress)
                .find(MyMessage.class);

        for (MyMessage myMessage : myMessageList) {
            HashMap<String, String> map = new HashMap<>();
            map.put("ItemSubject", myMessage.getSubject());
            map.put("ItemDate", myMessage.getSentDate());
//            对于replyTo字段进一步处理, 只取第一个邮箱
            map.put("ItemFrom", myMessage.getReplyTo().split(";")[0]);
            Log.i(TAG, "run: ItemSubject=" + myMessage.getSubject());
            Log.i(TAG, "run: ItemDate=" + myMessage.getSentDate());
            Log.i(TAG, "run: ItemFrom=" + myMessage.getReplyTo().split(";")[0]);
            listData.add(map);
        }

        Message msg = handler.obtainMessage(7);
        msg.obj = listData;
        handler.sendMessage(msg);
    }


}