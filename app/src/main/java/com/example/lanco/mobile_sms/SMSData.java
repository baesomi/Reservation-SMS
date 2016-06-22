package com.example.lanco.mobile_sms;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lanco on 2016-05-29.
 */
public class SMSData implements Parcelable{

    public static final String ERROR_UNKNOWN = "UNKNOWN";
    public static final String ERROR_GENERIC = "GENERIC";
    public static final String ERROR_NO_SERVICE = "NO_SERVICE";
    public static final String ERROR_NULL_PDU = "NULL_PDU";
    public static final String ERROR_RADIO_OFF = "RADIO_OFF";

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SENT = "SENT";
    public static final String STATUS_FAILED = "FAILED";

    private long report;
    private long reserved;
    private int id;
    private String recipientNumber;
    private String recipientName;
    private String group;
    private String sort;
    private String message;
    private String status = STATUS_PENDING;
    private String result = "";

    public SMSData(){}

    public SMSData(Parcel in){
        readFromParcel(in);
    }

    public int getId() {return (int) (getReportDate() / 1000);}

    public Long getReportDate() {
        return report;
    }

    public void setReportDate(long reportedDate) {
        this.report = reportedDate;
    }


    public Long getReservedDate() {
        return reserved;
    }

    public void setReservedDate(long reservedDate) {
        this.reserved = reservedDate;
    }

    public String getRecipientNumber() {
        return recipientNumber;
    }

    public void setRecipientNumber(String recipientNumber) {
        this.recipientNumber = recipientNumber;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getGroupName(){return group;}

    public void setGroupName(String group){this.group = group;}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    ///////////////////////////////////////////////////////////////////
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recipientName);
        dest.writeString(recipientNumber);
        dest.writeLong(report);
        dest.writeLong(reserved);
        dest.writeString(message);
        dest.writeString(sort);
        dest.writeString(status);
    }
    public void readFromParcel(Parcel in){
        recipientName = in.readString();
        recipientNumber = in.readString();
        report = in.readLong();
        reserved = in.readLong();
        message = in.readString();
        sort = in.readString();
        status = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){

        @Override
        public Object createFromParcel(Parcel source) {
            return new SMSData(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new SMSData[size];
        }
    };
    ///////////////////////////////////////////////////////////////////
}
