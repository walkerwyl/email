package com.swufe.email.data;

import org.litepal.crud.LitePalSupport;

public class Account extends LitePalSupport {

    private String emailAddress;
    private String emailPassword;
//      用户别名全部去除
//    private String nickName;
    private String status;
    private String POP3HOST;
    private int POP3PORT;
    private String SMTPHOST;
    private int SMTPPORT;


    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

//    public String getNickName() {
//        return nickName;
//    }
//
//    public void setNickName(String nickName) {
//        this.nickName = nickName;
//    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPOP3HOST() {
        return POP3HOST;
    }

    public void setPOP3HOST(String POP3HOST) {
        this.POP3HOST = POP3HOST;
    }

    public int getPOP3PORT() {
        return POP3PORT;
    }

    public void setPOP3PORT(int POP3PORT) {
        this.POP3PORT = POP3PORT;
    }

    public String getSMTPHOST() {
        return SMTPHOST;
    }

    public void setSMTPHOST(String SMTPHOST) {
        this.SMTPHOST = SMTPHOST;
    }

    public int getSMTPPORT() {
        return SMTPPORT;
    }

    public void setSMTPPORT(int SMTPPORT) {
        this.SMTPPORT = SMTPPORT;
    }
}
