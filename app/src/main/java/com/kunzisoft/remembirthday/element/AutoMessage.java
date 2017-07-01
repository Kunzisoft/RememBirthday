package com.kunzisoft.remembirthday.element;

/**
 * Created by joker on 01/07/17.
 */

public class AutoMessage extends Reminder {

    private Type type;
    private String content;

    /**
     * Create default auto message
     */
    public AutoMessage() {
        type = Type.SMS;
        content = "";
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
