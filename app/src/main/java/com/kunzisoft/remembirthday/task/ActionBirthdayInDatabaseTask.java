package com.kunzisoft.remembirthday.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.kunzisoft.remembirthday.element.DateUnknownYear;

/**
 * Created by joker on 19/04/17.
 */

public abstract class ActionBirthdayInDatabaseTask extends AsyncTask<Void, Void, Exception> {

    protected long contactId;
    protected DateUnknownYear birthday;
    protected Activity context;

    protected CallbackActionBirthday callbackActionBirthday;

    public ActionBirthdayInDatabaseTask(long contactId, DateUnknownYear birthday, Activity context) {
        this.contactId = contactId;
        this.birthday = birthday;
        this.context = context;
    }

    @Override
    protected void onPostExecute(Exception exception) {
        if(callbackActionBirthday != null)
            callbackActionBirthday.afterActionBirthdayInDatabase(exception);
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
        void afterActionBirthdayInDatabase(Exception exception);
    }
}
