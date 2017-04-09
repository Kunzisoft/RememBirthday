package com.kunzisoft.remembirthday.element;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model for contact with birthday manager <br />
 * Use Joda, must be initialize
 */
public class ContactBirthday extends Contact{

    public static final long ID_UNDEFINED = -1;

    private DateUnknownYear birthday;

    public ContactBirthday(long id, String name, DateUnknownYear birthday) {
        super(id, name);
        this.birthday = birthday;
    }

    public ContactBirthday(String name, DateUnknownYear birthday) {
        this(ID_UNDEFINED, name, birthday);
    }

    private ContactBirthday(Parcel in) {
        super(in);
        birthday = in.readParcelable(DateUnknownYear.class.getClassLoader());
    }

    public DateUnknownYear getBirthday() {
        return birthday;
    }

    public void setBirthday(DateUnknownYear date) {
        this.birthday = date;
    }

    /**
     * Get number of years between the birthday and today <br />
     * WARNING : if the year is unknown, return -1
     * @return
     */
    public int getAge() {
        if(!birthday.hasUnknownYear())
            return birthday.getDeltaYears();
        else
            return -1;
    }

    /**
     * Return number of days between today and the birthday
     * @return Number of days left
     */
    public int getBirthdayDaysRemaining() {
        return birthday.getDeltaDaysInAYear();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(birthday, i);
    }

    public static final Parcelable.Creator<ContactBirthday> CREATOR = new Parcelable.Creator<ContactBirthday>() {
        public ContactBirthday createFromParcel(Parcel in) {
            return new ContactBirthday(in);
        }

        public ContactBirthday[] newArray(int size) {
            return new ContactBirthday[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactBirthday contactBirthday = (ContactBirthday) o;

        if (id != contactBirthday.id) return false;
        if (name != null ? !name.equals(contactBirthday.name) : contactBirthday.name != null) return false;
        return birthday != null ? birthday.equals(contactBirthday.birthday) : contactBirthday.birthday == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ContactBirthday{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
