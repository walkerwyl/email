package com.swufe.email.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GMTDate {
//    自定义基本的数据类型处理GMT格式的时间并提供一些简单的方法
    String gmtDate;

    GMTDate (String gmtDate) {
        this.gmtDate = gmtDate;
    }

    public String getGmtDate() {
        return gmtDate;
    }

    public void setGmtDate(String gmtDate) {
        this.gmtDate = gmtDate;
    }

    public Date toCSTDate () throws ParseException {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy",
                        new Locale("ENGLISH", "CHINA"));
        Date myDate = simpleDateFormat.parse(this.gmtDate);
        return myDate;
    }

}
