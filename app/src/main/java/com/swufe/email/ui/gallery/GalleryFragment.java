package com.swufe.email.ui.gallery;

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
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.swufe.email.DraftDetailActivity;
import com.swufe.email.MessageDetailActivity;
import com.swufe.email.R;
import com.swufe.email.WriteEmailActivity;
import com.swufe.email.data.MyMessage;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GalleryFragment extends Fragment  implements  Runnable, AdapterView.OnItemClickListener {

    private static final String TAG = "GalleryFragment";

    private GalleryViewModel galleryViewModel;

    String emailAddress;
    List<HashMap<String, String>> listData = new ArrayList<>();

    ListView listViewDRAFT;
    GalleryAdapter galleryAdapter;

    static Handler handler;
    SharedPreferences sharedPreferences;

    View root;


    @SuppressLint("HandlerLeak")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        root = inflater.inflate(R.layout.fragment_gallery, container, false);

        Log.i(TAG, "onCreateView: 草稿界面");

        sharedPreferences = getActivity().getSharedPreferences("myemail", Activity.MODE_PRIVATE);
        emailAddress = sharedPreferences.getString("email_address", "");

        listViewDRAFT = root.findViewById(R.id.listView_draft);

        Thread t = new Thread(GalleryFragment.this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 6) {
                    listData = (List<HashMap<String, String>>) msg.obj;
                    galleryAdapter = new GalleryAdapter(getContext(),
                            R.layout.list_item,
                            listData);
                    listViewDRAFT.setAdapter(galleryAdapter);
                    listViewDRAFT.setEmptyView(root.findViewById(R.id.nodraft));
                    listViewDRAFT.setOnItemClickListener(GalleryFragment.this);
                }
            }
        };

        return root;
    }

    @Override
    public void run() {
        List<HashMap<String, String>> listData = new ArrayList<>();
        List<MyMessage> myMessageList = LitePal.where("status = ? and from = ?", "1", emailAddress)
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

        Intent intent = new Intent(getContext(), DraftDetailActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("ItemSubject", subjectString);
        bundle.putString("ItemFrom", fromString);
        bundle.putString("ItemDate", dateString);

        intent.putExtras(bundle);
        startActivity(intent);
    }
}