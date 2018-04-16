package com.kunzisoft.remembirthday.exception;

/**
 * Created by joker on 15/07/17.
 */

public class NoPhoneNumberException extends Exception {

    public NoPhoneNumberException() {
        super("No Phone Number for this contact");
    }
}
