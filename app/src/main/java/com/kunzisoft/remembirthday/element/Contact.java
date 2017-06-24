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
    private String lookup;
    private String name;
    private Uri imageThumbnailUri;
    private Uri imageUri;
    private DateUnknownYear birthday;

    public Contact(long id, String lookup, String name) {
        this(id, lookup, name, null);
    }

    public Contact(long id, String lookup, String name, DateUnknownYear birthday) {
        this.id = id;
        this.lookup = lookup;
        this.name = name;
        this.imageThumbnailUri = null;
        this.imageUri = null;
        this.birthday = birthday;
    }

    public Contact(String name) {
        this(ID_UNDEFINED, "", name, null);
    }

    public Contact(String name, DateUnknownYear birthday) {
        this(ID_UNDEFINED, "", name, birthday);
    }

    private Contact(Parcel in) {
        id = in.readLong();
        lookup = in.readString();
        name = in.readString();
        imageThumbnailUri = in.readParcelable(Uri.class.getClassLoader());
        imageUri = in.readParcelable(Uri.class.getClassLoader());
        birthday = in.readParcelable(DateUnknownYear.class.getClassLoader());
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLookUp() {
        return lookup;
    }

    public void setLookUp(String lookup) {
        this.lookup = lookup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getImageThumbnailUri() {
        return imageThumbnailUri;
    }

    public void setImageThumbnailUri(Uri imageUri) {
        this.imageThumbnailUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
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

    /**
     * Get number of years always > 0 between the birthday and today <br />
     * WARNING : if the year is unknown, return -1
     * @return
     */
    public int getAge() {
        if(birthday.containsYear())
            return Math.abs(birthday.getDeltaYears());
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
        parcel.writeString(lookup);
        parcel.writeString(name);
        parcel.writeParcelable(imageThumbnailUri, i);
        parcel.writeParcelable(imageUri, i);
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

        return id == contact.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
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
