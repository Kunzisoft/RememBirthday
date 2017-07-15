package com.kunzisoft.remembirthday.element;

/**
 * Created by joker on 15/07/17.
 */

public class PhoneNumber {

    private String number;
    private int type;

    public PhoneNumber(String number, int type) {
        this.number = number;
        this.type = type;
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
}
