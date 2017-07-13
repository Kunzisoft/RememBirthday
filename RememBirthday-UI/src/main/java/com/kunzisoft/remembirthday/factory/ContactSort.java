package com.kunzisoft.remembirthday.factory;

import android.content.res.Resources;
import android.provider.ContactsContract;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.element.Contact;

import java.util.Comparator;

/**
 * Class to manage the sort of contacts in the lists. <br />
 * Since some elements of the lists are not homogeneous (birthdays: -03-28, 2017-03-28, 03-28), it is possible to sort in the query with "sortOrder" or after having retrieved the list with "Comparator"
 * @author joker on 05/07/17.
 */
public enum ContactSort {

    CONTACT_SORT_BY_NAME(
            R.string.pref_contacts_sort_list_value_name,
            R.string.pref_contacts_order_list_value_asc,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY),
    CONTACT_SORT_BY_NAME_DESC(
            R.string.pref_contacts_sort_list_value_name,
            R.string.pref_contacts_order_list_value_desc,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " DESC"),
    CONTACT_SORT_BY_ANNIVERSARY(
            R.string.pref_contacts_sort_list_value_anniversary,
            R.string.pref_contacts_order_list_value_asc,
            ContactsContract.CommonDataKinds.Event.START_DATE),
    CONTACT_SORT_BY_ANNIVERSARY_DESC(
            R.string.pref_contacts_sort_list_value_anniversary,
            R.string.pref_contacts_order_list_value_desc,
            ContactsContract.CommonDataKinds.Event.START_DATE + " DESC"),
    CONTACT_SORT_BY_ANNIVERSARY_DAYS_LEFT(
            R.string.pref_contacts_sort_list_value_days_left,
            R.string.pref_contacts_order_list_value_asc,
            new Comparator<Contact>() {
        @Override
        public int compare(Contact contactA, Contact contactB) {
            if(contactA.getBirthdayDaysRemaining() < contactB.getBirthdayDaysRemaining())
                return -1;
            else if(contactA.getBirthdayDaysRemaining() == contactB.getBirthdayDaysRemaining())
                return 0;
            else
                return 1;
        }
    }),
    CONTACT_SORT_BY_ANNIVERSARY_DAYS_LEFT_DESC(
            R.string.pref_contacts_sort_list_value_days_left,
            R.string.pref_contacts_order_list_value_desc,
            new Comparator<Contact>() {
        @Override
        public int compare(Contact contactA, Contact contactB) {
            if(contactA.getBirthdayDaysRemaining() > contactB.getBirthdayDaysRemaining())
                return -1;
            else if(contactA.getBirthdayDaysRemaining() == contactB.getBirthdayDaysRemaining())
                return 0;
            else
                return 1;
        }
    });

    private int resourceValueSortString = -1;
    private int resourceValueOrderString = -1;
    private String sortOrder = null;
    private Comparator<Contact> contactComparator = null;

    /**
     * Define the last parameter 'sortOrder' of CursorLoader @see <a href="https://developer.android.com/reference/android/content/CursorLoader.html">CursorLoader Doc</a>
     * @param sortOrder part of "ORDER BY" SQL query
     */
    ContactSort(int resourceValueSortString, int resourceValueOrderString, String sortOrder) {
        this.resourceValueSortString = resourceValueSortString;
        this.resourceValueOrderString = resourceValueOrderString;
        this.sortOrder = sortOrder;
    }

    ContactSort(int resourceValueSortString, int resourceValueOrderString, Comparator<Contact> contactComparator) {
        this.resourceValueSortString = resourceValueSortString;
        this.resourceValueOrderString = resourceValueOrderString;
        this.contactComparator = contactComparator;
    }

    ContactSort(int resourceValueSortString, int resourceValueOrderString, String sortOrder, Comparator<Contact> contactComparator) {
        this.resourceValueSortString = resourceValueSortString;
        this.resourceValueOrderString = resourceValueOrderString;
        this.sortOrder = sortOrder;
        this.contactComparator = contactComparator;
    }

    public int getResourceValueString() {
        return resourceValueSortString;
    }

    public String getOrderByQuery() {
        return sortOrder;
    }

    public Comparator<Contact> getContactComparator() {
        return contactComparator;
    }

    /**
     * Find the ContactSort with resource value associated
     * @param resources Resources for retrieve String
     * @param valueSort String defined in strings.xml and begins with "pref_contacts_sort_list_value_"
     * @param valueOrder String defined in strings.xml and begins with "pref_contacts_value_list_value_"
     * @return ContactSort associated
     */
    public static ContactSort findContactSortByResourceValueString(Resources resources,
                                                                   String valueSort,
                                                                   String valueOrder) {
        for(ContactSort currentContactSort : values()){
            if(resources.getString(currentContactSort.resourceValueSortString).equals(valueSort)
                    && resources.getString(currentContactSort.resourceValueOrderString).equals(valueOrder)){
                return currentContactSort;
            }
        }
        return null;
    }
}
