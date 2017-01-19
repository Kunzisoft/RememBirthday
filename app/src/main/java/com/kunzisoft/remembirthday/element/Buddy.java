package com.kunzisoft.remembirthday.element;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by joker on 15/12/16.
 */
public class Buddy implements Parcelable{

    private String name;
    private Date birthday;

    public Buddy(String name, Date birthday) {
        this.name = name;
        this.birthday = birthday;
    }

    private Buddy(Parcel in) {
        name = in.readString();
        birthday = (Date) in.readSerializable();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date date) {
        this.birthday = date;
    }

    /**
     * Return number of days between today and the birthday
     * @param birthday Birthday of buddy
     * @return Number of days
     */
    public static int getStayDays(Date birthday) {

        Calendar calendar = Calendar.getInstance();
        Date today = new Date();

        // Todo difference date
        return 12;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeSerializable(birthday);
    }

    public static final Parcelable.Creator<Buddy> CREATOR = new Parcelable.Creator<Buddy>() {
        public Buddy createFromParcel(Parcel in) {
            return new Buddy(in);
        }

        public Buddy[] newArray(int size) {
            return new Buddy[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Buddy buddy = (Buddy) o;

        if (name != null ? !name.equals(buddy.name) : buddy.name != null) return false;
        return birthday != null ? birthday.equals(buddy.birthday) : buddy.birthday == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Buddy{" +
                "name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
