package com.kunzisoft.remembirthday.element;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by joker on 01/07/17.
 */

public class AutoMessage extends Reminder {

    private Type type;
    private String content;

    /**
     * Create default auto message
     */
    public AutoMessage(Date anniversary, int hourOfDay, int minuteOfHour, int deltaDay) {
        super(anniversary, hourOfDay, minuteOfHour, deltaDay);
        init();
    }

    public AutoMessage(Date anniversary, int hourOfDay, int minuteOfHour) {
        this(anniversary, hourOfDay, minuteOfHour, 0);
    }

    public AutoMessage(Date anniversary, int minutes) {
        super(anniversary, minutes);
        init();
    }

    private void init() {
        type = Type.SMS;
        content = "";
    }

    public AutoMessage(Parcel in) {
        super(in);
        this.type = Type.valueOf(in.readString());
        this.content = in.readString();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(type.name());
        parcel.writeString(content);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public AutoMessage createFromParcel(Parcel in) {
            return new AutoMessage(in);
        }

        public AutoMessage[] newArray(int size) {
            return new AutoMessage[size];
        }
    };

    @Override
    public String toString() {
        String toString = super.toString();
        return "AutoMessage{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", " + toString +
                '}';
    }

    public enum Type {
        SMS,
        EMAIL
    }
}
