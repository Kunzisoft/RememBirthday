package com.kunzisoft.remembirthday.element;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

/**
 * Model for contact with birthday manager <br />
 * Use Joda, must be initialize
 */
public class Contact implements Parcelable{

    public static final long ID_UNDEFINED = -1;

    private long id;
    private String lookupKey;
    private long rawId;
    private long dataAnniversaryId;
    private String name;
    private Uri imageThumbnailUri;
    private Uri imageUri;
    private DateUnknownYear birthday;

    public Contact(long id, String lookupKey, String name) {
        this(id, lookupKey, ID_UNDEFINED, name, null);
    }

    public Contact(long id, String lookupKey, long rawId, String name) {
        this(id, lookupKey, rawId, name, null);
    }

    public Contact(long id, String lookupKey, long rawId, String name, DateUnknownYear birthday) {
        this.id = id;
        this.lookupKey = lookupKey;
        this.rawId = rawId;
        this.name = name;
        this.imageThumbnailUri = null;
        this.imageUri = null;
        this.birthday = birthday;
    }

    public Contact(String name) {
        this(ID_UNDEFINED, "", ID_UNDEFINED, name, null);
    }

    public Contact(String name, DateUnknownYear birthday) {
        this(ID_UNDEFINED, "", ID_UNDEFINED, name, birthday);
    }

    private Contact(Parcel in) {
        id = in.readLong();
        lookupKey = in.readString();
        rawId = in.readLong();
        dataAnniversaryId = in.readLong();
        name = in.readString();
        imageThumbnailUri = in.readParcelable(Uri.class.getClassLoader());
        imageUri = in.readParcelable(Uri.class.getClassLoader());
        birthday = in.readParcelable(DateUnknownYear.class.getClassLoader());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLookUpKey() {
        return lookupKey;
    }

    public void setLookUpKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }

    public long getRawId() {
        return rawId;
    }

    public void setRawId(long rawId) {
        this.rawId = rawId;
    }

    public long getDataAnniversaryId() {
        return dataAnniversaryId;
    }

    public void setDataAnniversaryId(long dataAnniversaryId) {
        this.dataAnniversaryId = dataAnniversaryId;
    }

    public Uri getUri() {
        return ContactsContract.Contacts.getLookupUri(id, lookupKey);
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
        parcel.writeString(lookupKey);
        parcel.writeLong(rawId);
        parcel.writeLong(dataAnniversaryId);
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

        if(rawId != -1 && rawId == contact.rawId) return true;

        if (id == -1 || id != contact.id) return false;
        return lookupKey != null ? lookupKey.equals(contact.lookupKey) : contact.lookupKey == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (lookupKey != null ? lookupKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                "lookupKey='" + lookupKey + '\'' +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
