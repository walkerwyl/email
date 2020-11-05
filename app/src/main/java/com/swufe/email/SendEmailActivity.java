package com.swufe.email;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.swufe.email.data.Account;
import com.swufe.email.data.MyMessage;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailActivity extends AppCompatActivity implements Runnable{

    private static final String TAG = "SendEmailActivity";

    static Handler handler;
    Intent intent;

    String emailAddress;
    String targetAddress;
    String emailSubject;
    String emailBody;
    String sendDate;
    ArrayList<String> targetAddressList;
    ArrayList<String> filePathArrayList;
    ArrayList<File> fileArrayList;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_email_layout);

        intent = getIntent();
        Bundle bundle = intent.getExtras();

        emailAddress = bundle.getString("email_address", "");
        targetAddress = bundle.getString("target_address", "");
        emailSubject = bundle.getString("email_subject", "");
        emailBody = bundle.getString("email_body", "");
        sendDate = bundle.getString("send_date", "");
        filePathArrayList = bundle.getStringArrayList("filePathArrayList");
        Log.i(TAG, "onCreate: emailAddress" + emailAddress);
        Log.i(TAG, "onCreate: targetAddress" + targetAddress);
        Log.i(TAG, "onCreate: emailSubject" + emailSubject);
        Log.i(TAG, "onCreate: emailBody" + emailBody);

        targetAddressList = new ArrayList<>();
        targetAddressList = splitTargetAddress(targetAddress);

        fileArrayList = new ArrayList<>();
        for (String filePath : filePathArrayList) {
            File file = new File(filePath);
            fileArrayList.add(file);
        }

        for (String item : targetAddressList) {
            Log.i(TAG, "onCreate: targetAddress item=" + item);
        }

        Thread t = new Thread(SendEmailActivity.this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
//                if (msg.what == 1) {
////                    邮件发送成功
//                    intent = new Intent(SendEmailActivity.this, MainActivity.class);
//                    startActivity(intent);
//                }
            }
        };
    }

    @Override
    public void run() {
        try {
//            根据用户的邮箱地址获得用户完整的account对象
            List<Account> accounts = LitePal.where("emailAddress = ?", emailAddress)
                    .limit(1)
                    .find(Account.class);
            Log.i(TAG, "run: size" + accounts.size());
            final Account account = accounts.get(0);
            //            发送邮件的同时保存到数据库中
            MyMessage myMessage = new MyMessage();
//            0 收件 1 草稿 2 已发送
            myMessage.setStatus("2");
            myMessage.setSubject(emailSubject);
            myMessage.setSentDate(sendDate);
            Log.i(TAG, "run: send date=" + sendDate);
//            此时保存已发送邮件的日期
            myMessage.setContent(emailBody);
            myMessage.setFrom(emailAddress);
//            用第一个收件人代替
            myMessage.setReplyTo(targetAddressList.get(0));
            myMessage.save();
            Log.i(TAG, "run: 保存发送邮件");

//            Account account = LitePal.findFirst(Account.class);

            Log.i(TAG, "Query: account emailAddress=" + account.getEmailAddress());
            Log.i(TAG, "Query: account emailPassword=" + account.getEmailPassword());
            Log.i(TAG, "Query: account status=" + account.getStatus());
            Log.i(TAG, "Query: account POP3HOST=" + account.getPOP3HOST());
            Log.i(TAG, "Query: account SMTPHOST=" + account.getSMTPHOST());
            Log.i(TAG, "Query: account SMTPPORT=" + account.getSMTPPORT());

            targetAddressList.add(account.getEmailAddress());

            // 创建邮箱
//            Mail mail = new Mail();
//            mail.setMailServerHost(account.getSMTPHOST());
//            mail.setMailServerPort("" + account.getSMTPPORT());
//            mail.setFromAddress(account.getEmailAddress());
//            mail.setPassword(account.getEmailPassword());
//            mail.setToAddress(targetAddressList);
//            mail.setSubject(emailSubject);
//            mail.setContent(emailBody);
//            if (filePathArrayList.size() != 0) {
//                mail.setAttachFiles(fileArrayList);
//            }
//
//            MailSender.getInstance().sendMail(mail,null);

            Properties properties = new Properties();
            properties.setProperty("mail.smtp.host", account.getSMTPHOST());
            properties.setProperty("mail.smtp.auth", "true");
            Session session= Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                    return new PasswordAuthentication(emailAddress,account.getEmailPassword());
                }
            });
//创建和编写邮件
            Message message= new MimeMessage(session);
//设置发件人
            message.setFrom(new InternetAddress(emailAddress));
//设置收件人
            message.setRecipient(Message.RecipientType.TO,new InternetAddress(targetAddress));
//设置标题
            message.setSubject(emailSubject);
//设置邮件的内容
            message.setText(emailBody);
            message.setSentDate(new Date());
//3.发送邮件
            Transport.send(message);


            Log.i(TAG, "run: 邮件发送成功");

            // 发送邮箱


            android.os.Message msg = handler.obtainMessage(1);
            handler.sendMessage(msg);
        } catch (Exception e) {
            Log.i(TAG, "run: 邮件发送失败" + e);
        }
    }

    private ArrayList<String> splitTargetAddress (String targetAddress) {
//        对传输过来的邮箱目标地址字符串进行拆分, 默认以分号进行分割
        ArrayList<String> targetAddressList = new ArrayList<>();
        String[] arrays = targetAddress.split(";");
        for (String item : arrays) {
            targetAddressList.add(item);
            Log.i(TAG, "splitTargetAddress: item=" + item);
        }
        return targetAddressList;
    }

}