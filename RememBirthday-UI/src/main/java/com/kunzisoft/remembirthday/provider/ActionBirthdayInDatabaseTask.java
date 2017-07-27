package com.kunzisoft.remembirthday.provider;

import android.app.Activity;
import android.os.AsyncTask;

import com.kunzisoft.remembirthday.element.DateUnknownYear;

/**
 * Created by joker on 19/04/17.
 */

public abstract class ActionBirthdayInDatabaseTask extends AsyncTask<Void, Void, Exception> {

    protected DateUnknownYear birthday;
    protected Activity context;

    protected CallbackActionBirthday callbackActionBirthday;
    protected CallbackActionBirthday.Action action;

    public ActionBirthdayInDatabaseTask(Activity context, DateUnknownYear birthday) {
        this.birthday = birthday;
        this.context = context;
        this.action = CallbackActionBirthday.Action.UNDEFINED;
    }

    @Override
    protected void onPostExecute(Exception exception) {
        if(callbackActionBirthday != null)
            callbackActionBirthday.afterActionBirthdayInDatabase(birthday, action, exception);
    }

    public CallbackActionBirthday getCallbackActionBirthday() {
        return callbackActionBirthday;
    }

    public void setCallbackActionBirthday(CallbackActionBirthday callbackActionBirthday) {
        this.callbackActionBirthday = callbackActionBirthday;
    }

    /**
     * Callback for do action after insert/update/delete birthday of contact in database
     */
    public interface CallbackActionBirthday {

        enum Action {
            UNDEFINED,
            ADD,
            UPDATE,
            REMOVE
        }

        void afterActionBirthdayInDatabase(DateUnknownYear birthday, Action action, Exception exception);
    }
}
