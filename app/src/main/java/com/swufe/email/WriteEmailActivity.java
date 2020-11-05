package com.swufe.email;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;
import com.swufe.email.data.Account;
import com.swufe.email.data.MyMessage;

import org.litepal.LitePal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WriteEmailActivity extends AppCompatActivity implements View.OnClickListener, Runnable{

    private static final String TAG = "WriteEmailActivity";

    Intent intent;
    Bundle bundle;
    static Handler handler;


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

    String emailAddress;
    String targetAddress;
    String emailSubject;
    String emailBody;
    ArrayList<String> filePathArrayList;//传递给发送邮件活动
    ArrayList<String> fileNameArrayList;//展示附件列表数据源

    Date date;
    SimpleDateFormat ft;

    // TODO: 20-10-28 使用下拉列表让用户选择自己的发送邮件的邮箱帐号
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_email_layout);

        intent = getIntent();
        bundle = intent.getExtras();
        emailAddress = bundle.getString("email_address", "");
        Log.i(TAG, "onCreate: 写邮件页面获得当前用户身份emailAddress=" + emailAddress);

        date = new Date();
        ft = new SimpleDateFormat("yyyy-MM-dd HH-mm");


        fileNameArrayList = new ArrayList<>();
        fileNameArrayList.add("附件");
        filePathArrayList = new ArrayList<>();

        backView = findViewById(R.id.btn_back);
        backView.setOnClickListener(WriteEmailActivity.this);

        saveDraftButton = findViewById(R.id.btn_save_draft);
        saveDraftButton.setOnClickListener(WriteEmailActivity.this);

        editTargetAddress = findViewById(R.id.edit_target_address);
        editEmailSubject = findViewById(R.id.edit_email_subject);
        textInputEmailBody = findViewById(R.id.text_input_emdil_body);

        addTargetAddress = findViewById(R.id.btn_add_target_address);
        addTargetAddress.setOnClickListener(WriteEmailActivity.this);

        sendEmailButton = findViewById(R.id.btn_send);
        sendEmailButton.setOnClickListener(WriteEmailActivity.this);

        attachFileButton = findViewById(R.id.btn_attach_file);
        attachFileButton.setOnClickListener(WriteEmailActivity.this);

        Thread thread = new Thread(WriteEmailActivity.this);
        thread.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 9) {
//                    根据从数据库中找出的数据设置下拉列表
                    List<String> emailAddressList = (ArrayList<String>) msg.obj;
                    spinner = findViewById(R.id.spinner_email_address);
                    spinnerAdapter = new ArrayAdapter<String>(WriteEmailActivity.this,
                            android.R.layout.simple_spinner_item, emailAddressList);
                    spinner.setAdapter(spinnerAdapter);

                }
                super.handleMessage(msg);
            }
        };
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
                    Toast.makeText(WriteEmailActivity.this,
                            "请填写主题", Toast.LENGTH_SHORT).show();
                    break;
                } else if (targetAddress.equals("") || !isValidTargetAddress(targetAddress)) {
                    Toast.makeText(WriteEmailActivity.this,
                            "请填写收信人", Toast.LENGTH_SHORT).show();
                    break;
                }

                Bundle sendEmailBundle = new Bundle();
                sendEmailBundle.putString("email_address", emailAddress);
                sendEmailBundle.putString("target_address", targetAddress);
                sendEmailBundle.putString("email_subject", emailSubject);
                sendEmailBundle.putString("email_body", emailBody);
                sendEmailBundle.putStringArrayList("filePathArrayList", filePathArrayList);

                Intent sendEmailIntent = new Intent(WriteEmailActivity.this, SendEmailActivity.class);
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
                    Toast.makeText(WriteEmailActivity.this,
                            "请填写主题", Toast.LENGTH_SHORT).show();
                    break;
                } else if (targetAddress.equals("") || !isValidTargetAddress(targetAddress)) {
                    Toast.makeText(WriteEmailActivity.this,
                            "请填写收信人", Toast.LENGTH_SHORT).show();
                    break;
                }

                Intent saveDraftIntent = new Intent(WriteEmailActivity.this, ManagerActivity.class);
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
                intent = new Intent(WriteEmailActivity.this, MainActivity.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        接收用户选择地址界面返回的数据,字符串并放入edittext中
        if (1 == requestCode && 2 == resultCode) {
            Bundle bundle = data.getExtras();
            String targetAddressString = bundle.getString("targer_address_list", "");
            editTargetAddress.setText(targetAddressString);
        } else if (requestCode == 2 &&resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                Log.i(TAG, "onActivityResult: uri=" + uri);
                Log.i(TAG, "onActivityResult: uri.getPath=" + uri.getPath());
                Log.i(TAG, "onActivityResult: " + getFilePathFromURI(this, uri));
                filePathArrayList.add(getFilePathFromURI(this, uri));
            Log.i(TAG, "onActivityResult: filePath=" + getFilePathFromURI(this, uri));
            Log.i(TAG, "onActivityResult: filePathArrayList size=" + filePathArrayList.size());
                File file = new File(getFilePathFromURI(this,uri));
                Log.i(TAG, "onActivityResult: fileName=" + file.getName());
                fileNameArrayList.add(file.getName());
            Log.i(TAG, "onActivityResult: fileNameArrayList size=" + fileNameArrayList.size());
//            在更新的同时对附件列表进行更新,使用默认的listview和adapter进行处理
        } else {
            Log.i(TAG, "onActivityResult: 出错");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void run() {
//        从数据库中选择所有的数据并保存在数组中返回
        List<Account> accounts = LitePal.findAll(Account.class);
        List<String> emailAddressList = new ArrayList<>();
        for (Account account : accounts) {
            emailAddressList.add(account.getEmailAddress());
        }

        Message msg = handler.obtainMessage(9);
        msg.obj = emailAddressList;
        handler.sendMessage(msg);
    }


    //获取拍照和选取的图片的绝对路径
    public static String getFilePathFromURI(Context context, Uri contentUri) {
        File rootDataDir = context.getExternalFilesDir(null);
//        MyApplication.getMyContext().getExternalFilesDir(null).getPath();
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(rootDataDir + File.separator + fileName);
            copyFile(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return fileName;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copyFile(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int copyStream(InputStream input, OutputStream output) throws Exception, IOException {
        final int BUFFER_SIZE = 1024 * 2;
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        return count;
    }
}