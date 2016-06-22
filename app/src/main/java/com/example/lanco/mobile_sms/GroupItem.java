package com.example.lanco.mobile_sms;

/**
 * Created by Lanco on 2016-06-07.
 */
public class GroupItem {
    private String t1;
    private String t2;

    public void setGName(String name){t1 = name;}
    public void setGPhone(String phone){t2 = phone;}

    public String getGName(){return this.t1;}
    public String getGPhone(){return this.t2;}
}
