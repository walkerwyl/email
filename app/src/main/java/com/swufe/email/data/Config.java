package com.swufe.email.data;

import org.litepal.crud.LitePalSupport;

public class Config extends LitePalSupport {
//    市面上常用的邮箱配置
    private String NAME;
    private String POP3HOST;
    private String SMTPHOST;
    private int POP3PORT;
    private int SMTPPORT;

    public String getNAME() {
        return this.NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getPOP3HOST() {
        return POP3HOST;
    }

    public void setPOP3HOST(String POP3HOST) {
        this.POP3HOST = POP3HOST;
    }

    public String getSMTPHOST() {
        return SMTPHOST;
    }

    public void setSMTPHOST(String SMTPHOST) {
        this.SMTPHOST = SMTPHOST;
    }

    public int getPOP3PORT() {
        return POP3PORT;
    }

    public void setPOP3PORT(int POP3PORT) {
        this.POP3PORT = POP3PORT;
    }

    public int getSMTPPORT() {
        return SMTPPORT;
    }

    public void setSMTPPORT(int SMTPPORT) {
        this.SMTPPORT = SMTPPORT;
    }
}
