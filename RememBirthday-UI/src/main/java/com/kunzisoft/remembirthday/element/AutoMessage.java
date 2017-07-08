package com.kunzisoft.remembirthday.element;

import java.util.Date;

/**
 * Created by joker on 01/07/17.
 */

public class AutoMessage extends Reminder {

    private Type type;
    private String content;

    /**
     * Create default auto message
     */
    public AutoMessage(Date anniversary, int hourOfDay, int minuteOfHour, int deltaDay) {
        super(anniversary, hourOfDay, minuteOfHour, deltaDay);
        type = Type.SMS;
        content = "";
    }

    public AutoMessage(Date anniversary, int hourOfDay, int minuteOfHour) {
        this(anniversary, hourOfDay, minuteOfHour, 0);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public enum Type {
        SMS,
        EMAIL
    }
}
