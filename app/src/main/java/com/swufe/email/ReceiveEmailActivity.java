package com.swufe.email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import com.sun.mail.pop3.POP3Folder;
import com.swufe.email.data.Account;
import com.swufe.email.data.MyMessage;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;

public class ReceiveEmailActivity extends AppCompatActivity implements Runnable{

    private static final String TAG = "ReceiveEmailActivity";

    static Handler handler;
    static Intent intent;
    static Bundle bundle;

    String emailAddress;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_email_layout);

        Thread t = new Thread(ReceiveEmailActivity.this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                if (msg.what == 7) {
                    //        本邮箱的邮件全部存储之后, 转向展示页面即MainActivity
                    intent = new Intent(ReceiveEmailActivity.this, MainActivity.class);
                    bundle = new Bundle();
                    bundle.putString("email_address", emailAddress);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        };


    }

    @Override
    public void run() {
//        执行接受邮件的任务, 要求是传入参数
//        访问数据库account
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        emailAddress = bundle.getString("email_address", "");

//        根据条件访问数据库获得当前用户对应的account对象
        List<Account> accounts = LitePal.where("emailAddress = ?", emailAddress)
                .find(Account.class);
        Log.i(TAG, "run: accounts size = " + accounts.size());
        Account account = accounts.get(0);

        Log.i(TAG, "Receive: account emailAddress=" + account.getEmailAddress());
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


            POP3Folder folder = (POP3Folder) store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY); // 获取信息
            FetchProfile profile = new FetchProfile();
            profile.add(UIDFolder.FetchProfileItem.UID);
            profile.add(FetchProfile.Item.ENVELOPE);
            Message[] messages = folder.getMessages();
            Log.i(TAG, "run: 查看messages大小" + messages.length);
            for (Message message : messages) {
//                对于官方邮件本身的信息进行进一步的挖掘
//                查找需要的部分存储在数据库中, 每次链减进行检查, 当messages的大小发生变化时
//                便主动队数据库进行更新操作(默认用户为了获得最新的邮箱会进行重新登录刷新界面操作)
                MyMessage myMessage = new MyMessage();
                myMessage.setUid(folder.getUID(message));
                Log.i(TAG, "run: 此邮件的UID=" + folder.getUID(message));

                //                在本地数据库中查询此UID,若存在则不保存,只保存本地没有的邮件
                //            从本地获得已读取的邮件信息

                List<MyMessage> storeMessages = LitePal.where("uid = ?", folder.getUID(message))
                        .limit(1)
                        .find(MyMessage.class);
                if (storeMessages.size() == 1) {
                    Log.i(TAG, "run: 此邮件已存在");
                    continue;
                }

                myMessage.setStatus("0");
                myMessage.setSubject(message.getSubject());

                Log.i(TAG, "run: getSubject=" + message.getSubject());
                for (Address item : message.getFrom()) {
                    Log.i(TAG, "run: getFrom=" + item);
                    Log.i(TAG, "run: getFrom Address=" + matchMail(item + ""));
                    if (validMail(item.toString())) myMessage.setFrom(item.toString());
                    else if (validMail(matchMail(item.toString())))
                        myMessage.setFrom(matchMail(item.toString()));
                }
                myMessage.setSentDate(message.getSentDate() + "");
                myMessage.setContentType(message.getContentType());
                String contentType = message.getContentType();
                Log.i(TAG, "run: contentType=" + contentType);

                // 得到邮件的Multipart（内容总部件--【包涵附件】）
//                Multipart multipart = (Multipart) message.getContent();
                String text = "";
                switch (contentType.split(";")[0]) {
                    case "multipart/alternative":
                        String html = "";
                        Multipart m = (Multipart) message.getContent();
                        for (int k=0; k<m.getCount(); k++) {
                            if(m.getBodyPart(k).getContentType().startsWith("text/plain")) {
                                // 处理文本正文
                                text += m.getBodyPart(k).getContent().toString().trim()+"\n";
//                                Log.i(TAG, "multipart/alternative TEXT文本内容："+"\n" + m.getBodyPart(k).getContent().toString().trim()+"\n");
                            } else {
                                // 处理 HTML 正文
                                html +=  m.getBodyPart(k).getContent()+"\n";
//                                Log.i(TAG, "multipart/alternative HTML文本内容："+"\n" + m.getBodyPart(k).getContent()+"\n");
                            }
                        }
                        text += html2text(html);
                        Log.i(TAG, "run: text = " + text);
                        break;
                    case "text/plain":
                        Log.i(TAG, "TEXT文本内容："+"\n" + message.getContent().toString().trim()+"\n");
                        text = message.getContent().toString().trim()+"\n";
                        break;
                    case "text/html":
                        Log.i(TAG, "HTML文本内容："+"\n" + message.getContent()+"\n");
                        text = html2text(message.getContent().toString());
                        break;
                    case "multipart/related":
//                        Log.i(TAG, "run: " + "内嵌资源");
                        break;
                    case "application/":
//                        应用附件
//                        Log.i(TAG, "run: " + "应用文件");
                        break;
                    case "image/":
//                        Log.i(TAG, "run: " + "图片文件");
                        break;
                }

                //                获得确切的内容
                myMessage.setContent(text);

                myMessage.setMessageNumber(message.getMessageNumber());
//                Log.i(TAG, "run: getSentDate=" + message.getSentDate());
                Log.i(TAG, "run: contentType=" + message.getContentType());
//                Log.i(TAG, "run: getContent=" + message.getContent());
//                Log.i(TAG, "run: getMesseageNumber=" + message.getMessageNumber());
                for (Address item : message.getReplyTo()) {
//                    Log.i(TAG, "run: getReplyTo=" + item);
//                    Log.i(TAG, "run: getReplyTo Address=" + matchMail(item + ""));
                    if (validMail(item.toString())) myMessage.setReplyTo(item.toString());
                    else if (validMail(matchMail(item.toString())))
                        myMessage.setReplyTo(matchMail(item.toString()));
                }
                Log.i(TAG, "run: ---------------------------------------------------------------");

                myMessage.save();
//                接受数据时同时对数据进行存储
//                如何避免重复插入相同的数据
            }


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

        android.os.Message msg = handler.obtainMessage(7);
        msg.obj = emailAddress;
        handler.sendMessage(msg);

    }


    public String html2text(String html)
    {
        if (StringUtils.isEmpty(html))
        {
            return "";
        }

        Document document = Jsoup.parse(html);
        Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);
        document.outputSettings(outputSettings);
        document.select("br").append("\\n");
        document.select("p").prepend("\\n");
        document.select("p").append("\\n");
        String newHtml = document.html().replaceAll("\\\\n", "\n");
        String plainText = Jsoup.clean(newHtml, "", Whitelist.none(), outputSettings);
        String result = StringEscapeUtils.unescapeHtml4(plainText.trim());
        return result;
    }

    private String matchMail(String source) {
//        如何在一串字符串中辨识出邮箱帐号
//        https://blog.csdn.net/xujinsmile/article/details/8703622
//        显示时依照一定的顺序对邮箱是否符合进行判断, 如果其中一个判断通过则采用最先通过者作为显示的发信人
//        进行存储
        String regex = "[a-zA-z\\.[0-9]]*@[a-zA-z[0-9]]*\\.(com|cn|net)";
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
        String regex = "[a-zA-z.[0-9]]*@[a-zA-z[0-9]]*\\.(com|cn|net)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        return matcher.find() && source.equals(matcher.group());
    }
}