package com.kunzisoft.remembirthday.element;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model for contact with birthday manager <br />
 * Use Joda, must be initialize
 */
public class Contact implements Parcelable{

    public static final long ID_UNDEFINED = -1;

    private long id;
    private String name;
    private Uri imageUri;
    private DateUnknownYear birthday;

    public Contact(long id, String name) {
        this(id, name, null);
    }

    public Contact(long id, String name, DateUnknownYear birthday) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
    }

    public Contact(String name) {
        this(ID_UNDEFINED, name, null);
    }

    public Contact(String name, DateUnknownYear birthday) {
        this(ID_UNDEFINED, name, birthday);
    }

    private Contact(Parcel in) {
        id = in.readLong();
        name = in.readString();
        birthday = in.readParcelable(DateUnknownYear.class.getClassLoader());
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasBirthday() {
        return birthday!=null;
    }

    public DateUnknownYear getBirthday() {
        return birthday;
    }

    public void setBirthday(DateUnknownYear date) {
        this.birthday = date;
    }

    public boolean containsImage() {
        return imageUri!=null;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
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
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeParcelable(birthday, i);
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (id != contact.id) return false;
        if (name != null ? !name.equals(contact.name) : contact.name != null) return false;
        return birthday != null ? birthday.equals(contact.birthday) : contact.birthday == null;

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
        return "Contact{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
