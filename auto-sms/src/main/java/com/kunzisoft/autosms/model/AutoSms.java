package com.kunzisoft.autosms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.kunzisoft.autosms.CalendarResolver;

import java.util.Calendar;
import java.util.Date;

public class AutoSms implements Parcelable {

    public static final String ERROR_UNKNOWN = "UNKNOWN";
    public static final String ERROR_GENERIC = "GENERIC";
    public static final String ERROR_NO_SERVICE = "NO_SERVICE";
    public static final String ERROR_NULL_PDU = "NULL_PDU";
    public static final String ERROR_RADIO_OFF = "RADIO_OFF";

    private Date dateCreated;
    private Date dateScheduled;
    private String recipientPhoneNumber;
    private String recipientLookup;
    private String message;
    private Status status = Status.PENDING;
    private int subscriptionId;
    private String recurringMode = CalendarResolver.RECURRING_NO;

    private String result = "";

    public AutoSms() {

    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateScheduled() {
        return dateScheduled;
    }

    public void setDateScheduled(Date dateScheduled) {
        this.dateScheduled = dateScheduled;
    }

    public String getRecipientPhoneNumber() {
        return recipientPhoneNumber;
    }

    public void setRecipientPhoneNumber(String recipientPhoneNumber) {
        this.recipientPhoneNumber = recipientPhoneNumber;
    }

    public String getRecipientLookup() {
        return recipientLookup;
    }

    public void setRecipientLookup(String recipientLookup) {
        this.recipientLookup = recipientLookup;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getRecurringMode() {
        return recurringMode;
    }

    public void setRecurringMode(String recurringMode) {
        this.recurringMode = recurringMode;
    }

    public AutoSms(Parcel in) {
        dateCreated = (Date) in.readSerializable();
        dateScheduled = (Date) in.readSerializable();
        recipientPhoneNumber = in.readString();
        recipientLookup = in.readString();
        message = in.readString();
        status = Status.valueOf(in.readString());
        result = in.readString();
        subscriptionId = in.readInt();
        recurringMode = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(dateCreated);
        dest.writeSerializable(dateScheduled);
        dest.writeString(recipientPhoneNumber);
        dest.writeString(recipientLookup);
        dest.writeString(message);
        dest.writeString(status.name());
        dest.writeString(result);
        dest.writeInt(subscriptionId);
        dest.writeString(recurringMode);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public AutoSms createFromParcel(Parcel in) {
            return new AutoSms(in);
        }

        public AutoSms[] newArray(int size) {
            return new AutoSms[size];
        }
    };

    @Override
    public String toString() {
        return "AutoSms{" +
                "dateCreated=" + dateCreated +
                ", dateScheduled=" + dateScheduled +
                ", recipientPhoneNumber='" + recipientPhoneNumber + '\'' +
                ", recipientLookup='" + recipientLookup + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", subscriptionId=" + subscriptionId +
                ", recurringMode='" + recurringMode + '\'' +
                ", result='" + result + '\'' +
                '}';
    }

    public enum Status {
        PENDING,
        SEND,
        DELIVERED,
        FAILED
    }
}
