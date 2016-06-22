package com.example.lanco.mobile_sms;

/**
 * Created by kjy79 on 2016-06-08.
 */
public class CalendarItem {
    private String text1;
    private String text2;
    private String text3;

    public void setName(String name) {text1 = name ;}
    public void setDate(String date) {text2 = date ;}
    public void setContent(String content){text3 = content;}

    public String getName() {return this.text1;}
    public String getDate() {return this.text2;}
    public String getContent(){ return  this.text3;}
}
