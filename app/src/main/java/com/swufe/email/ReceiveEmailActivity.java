package com.swufe.email;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.swufe.email.data.Account;
import com.swufe.email.data.MyMessage;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class ReceiveEmailActivity extends AppCompatActivity implements Runnable{

    private static final String TAG = "ReceiveEmailActivity";

    static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_email_layout);

        Thread t = new Thread(ReceiveEmailActivity.this);
        t.start();

        handler = new Handler() {
        };
    }

    @Override
    public void run() {
//        执行接受邮件的任务, 要求是传入参数
//        访问数据库account
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String emailAddress = bundle.getString("email_address", "");

//        根据条件访问数据库获得当前用户对应的account对象
        List<Account> accounts = LitePal.where("emailAddress = ?", emailAddress)
                .find(Account.class);
        Log.i(TAG, "run: accounts size = " + accounts.size());
        Account account = accounts.get(0);

        Log.i(TAG, "Receive: account emailAddress=" + account.getEmailAddress() );
        Log.i(TAG, "Receive: account emailPassword=" + account.getEmailPassword());
        Log.i(TAG, "Receive: account status=" + account.getStatus());
        Log.i(TAG, "Receive: account POP3HOST=" + account.getPOP3HOST());
        Log.i(TAG, "Receive: account SMTPHOST=" + account.getSMTPHOST());
        Log.i(TAG, "Receive: account SMTPPORT=" + account.getSMTPPORT());

        Properties properties = new Properties();
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.store.protocol", "pop3");//个人设置
        properties.setProperty("mail.pop3.host", account.getPOP3HOST());//个人设置

        Session session = Session.getDefaultInstance(properties); // 取得pop3协议的邮件服务器

        Store store = null;
        try {
            store = session.getStore();
            // 连接pop.sina.com邮件服务器 //
            store.connect(account.getPOP3HOST(), emailAddress, account.getEmailPassword()); // 返回文件夹对象
            Log.i(TAG, "run: 链接pop服务器");

            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY); // 获取信息
            Message[] messages = folder.getMessages();
            Log.i(TAG, "run: 查看messages大小"+ messages.length);
            for (Message message : messages) {
//                对于官方邮件本身的信息进行进一步的挖掘
//                查找需要的部分存储在数据库中, 每次链减进行检查, 当messages的大小发生变化时
//                便主动队数据库进行更新操作(默认用户为了获得最新的邮箱会进行重新登录刷新界面操作)
                MyMessage myMessage = new MyMessage();
                myMessage.setSubject(message.getSubject());

                Log.i(TAG, "run: getSubject=" + message.getSubject());
                for (Address item : message.getFrom()) {
                    Log.i(TAG, "run: getFrom=" + item);
                    Log.i(TAG, "run: getFrom Address=" + matchMail(item + ""));
                    if (validMail(item.toString())) myMessage.setFrom(item.toString());
                    else if (validMail(matchMail(item.toString()))) myMessage.setFrom(matchMail(item.toString()));
                }
                myMessage.setSentDate(message.getSentDate() + "");
                myMessage.setContentType(message.getContentType());
                myMessage.setContent(message.getContent().toString());
                myMessage.setMessageNumber(message.getMessageNumber());
                Log.i(TAG, "run: getSentDate=" + message.getSentDate());
                Log.i(TAG, "run: getContentType=" + message.getContentType());
                Log.i(TAG, "run: getContent=" + message.getContent());
                Log.i(TAG, "run: getMesseageNumber=" + message.getMessageNumber());
                for (Address item : message.getReplyTo()) {
                    Log.i(TAG, "run: getReplyTo=" + item);
                    Log.i(TAG, "run: getReplyTo Address=" + matchMail(item + ""));
                    if (validMail(item.toString())) myMessage.setReplyTo(item.toString());
                    else if (validMail(matchMail(item.toString()))) myMessage.setReplyTo(matchMail(item.toString()));
                }
                Log.i(TAG, "run: ---------------------------------------------------------------");
                myMessage.save();
//                接受数据时同时对数据进行存储
//                如何避免重复插入相同的数据
            }
//            Message message = messages[5];
            

            folder.close(false);
            store.close();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
            Log.i(TAG, "run: MessagingException" + e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "run: IOException" + e);
        }


    }

    private String matchMail(String source) {
//        如何在一串字符串中辨识出邮箱帐号
//        https://blog.csdn.net/xujinsmile/article/details/8703622
//        显示时依照一定的顺序对邮箱是否符合进行判断, 如果其中一个判断通过则采用最先通过者作为显示的发信人
//        进行存储
        String regex = "[a-zA-z\\.[0-9]]*@[a-zA-z[0-9]]*\\.com";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        String mail;
        if (matcher.find()){
            mail = matcher.group();
            return mail;
        }
        return "";
    }

    private boolean validMail (String source) {
//        利用正则表达式对字符串进行判断,若符合一般标准且没有多于的字符,则返回true
        String regex = "[a-zA-z\\.[0-9]]*@[a-zA-z[0-9]]*\\.com";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        if (matcher.find() && source.equals(matcher.group())) return true;
        else return false;
    }
}