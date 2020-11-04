package com.swufe.email.data;

import org.litepal.crud.LitePalSupport;

import java.text.ParseException;
import java.util.Date;

public class MyMessage extends LitePalSupport {
//    一开始只完成文本文件的传输和展示
//    只考虑文本文件
    String status;
    //用于分类 0 邮箱中的邮件
    //1 草稿箱中的邮件
    //2 已发送的邮件存储
    String subject;
    String from;
    String replyTo;
    String sentDate;
    String contentType;
    String content;
    int messageNumber;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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


}
