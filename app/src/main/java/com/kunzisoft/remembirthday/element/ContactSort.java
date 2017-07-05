package com.kunzisoft.remembirthday.element;

import android.provider.ContactsContract;

import java.util.Comparator;

/**
 * Class to manage the sort of contacts in the lists. <br />
 * Since some elements of the lists are not homogeneous (birthdays: -03-28, 2017-03-28, 03-28), it is possible to sort in the query with "sortOrder" or after having retrieved the list with "Comparator"
 * @author joker on 05/07/17.
 */
public enum ContactSort {

    CONTACT_SORT_BY_NAME(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY),
    CONTACT_SORT_BY_NAME_DESC(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " DESC"),
    CONTACT_SORT_BY_ANNIVERSARY(ContactsContract.CommonDataKinds.Event.START_DATE),
    CONTACT_SORT_BY_ANNIVERSARY_DESC(ContactsContract.CommonDataKinds.Event.START_DATE + " DESC"),
    CONTACT_SORT_BY_ANNIVERSARY_DAYS_LEFT(new Comparator<Contact>() {
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
    CONTACT_SORT_BY_ANNIVERSARY_DAYS_LEFT_DESC(new Comparator<Contact>() {
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

    private String sortOrder = null;
    private Comparator<Contact> contactComparator = null;

    /**
     * Define the last parameter 'sortOrder' of CursorLoader @see <a href="https://developer.android.com/reference/android/content/CursorLoader.html">CursorLoader Doc</a>
     * @param sortOrder part of "ORDER BY" SQL query
     */
    ContactSort(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    ContactSort(Comparator<Contact> contactComparator) {
        this.contactComparator = contactComparator;
    }

    ContactSort(String sortOrder, Comparator<Contact> contactComparator) {
        this.sortOrder = sortOrder;
        this.contactComparator = contactComparator;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public Comparator<Contact> getContactComparator() {
        return contactComparator;
    }
}
