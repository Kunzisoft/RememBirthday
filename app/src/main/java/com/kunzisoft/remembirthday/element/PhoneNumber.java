package com.kunzisoft.remembirthday.element;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model for Phone Number
 */
public class PhoneNumber implements Parcelable {

    private String number;
    private int type;

    public PhoneNumber(String number, int type) {
        this.number = number;
        this.type = type;
    }

    private PhoneNumber(Parcel in) {
        number = in.readString();
        type = in.readInt();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PhoneNumber{" +
                "number='" + number + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(number);
        parcel.writeInt(type);
    }

    public static final Parcelable.Creator<PhoneNumber> CREATOR = new Parcelable.Creator<PhoneNumber>() {
        public PhoneNumber createFromParcel(Parcel in) {
            return new PhoneNumber(in);
        }

        public PhoneNumber[] newArray(int size) {
            return new PhoneNumber[size];
        }
    };
}
