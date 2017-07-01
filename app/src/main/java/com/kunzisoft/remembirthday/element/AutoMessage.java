package com.kunzisoft.remembirthday.element;

import java.util.Date;

/**
 * Created by joker on 01/07/17.
 */

public class AutoMessage {

    public static final long ID_UNDEFINED = -1;

    private long id;
    private Type type;
    private String content;
    private Date date;

    /**
     * Create default auto message
     */
    public AutoMessage() {
        id = ID_UNDEFINED;
        type = Type.SMS;
        content = "";
        // TODO create delta date
        date = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public enum Type {
        SMS,
        EMAIL
    }
}
