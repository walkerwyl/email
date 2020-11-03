package com.swufe.email.data;

import org.litepal.crud.LitePalSupport;

import java.text.ParseException;
import java.util.Date;

public class MyMessage extends LitePalSupport {
//    一开始只完成文本文件的传输和展示
//    只考虑文本文件
    String subject;
    String from;
    String replyTo;
    String sentDate;
    String contentType;
    String content;
    int messageNumber;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public int dateJudgment (String sentDate) throws ParseException {
//        判断该邮件是否今天发送
//        是, 则显示具体的时间
//        否, 则显示大致范围, 若在两日之内则, 今天(具体时间), 昨天, 星期日
//        其余则直接显示日期
        GMTDate gmtDate = new GMTDate(sentDate);
        Date date = gmtDate.toCSTDate();
        return 0;
    }
}
