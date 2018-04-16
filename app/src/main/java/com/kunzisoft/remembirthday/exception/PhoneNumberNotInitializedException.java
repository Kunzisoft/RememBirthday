package com.kunzisoft.remembirthday.exception;

/**
 * Created by joker on 15/07/17.
 */

public class PhoneNumberNotInitializedException extends Exception {

    public PhoneNumberNotInitializedException() {
        super("Phone number has not been initialized, use the 'setPhoneNumbers' or 'setPhoneNumber' method of contact");
    }
}
