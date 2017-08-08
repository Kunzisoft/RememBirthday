package com.kunzisoft.remembirthday.activity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.kunzisoft.remembirthday.R;
import com.kunzisoft.remembirthday.provider.ContactProvider;

/**
 * Utility class for callback management
 * @author joker on 06/07/17.
 */
public class CallbackAction {

    /**
     * Displays a Toast message based on the action performed
     * @param context Context for make Toast
     * @param action Action performed
     * @param exception Exception if error, null if all is ok
     */
    public static void showMessage(Context context, ContactProvider.CallbackActionBirthday.Action action, Exception exception) {
        String message = "";
        switch (action) {
            case ADD:
                if (exception == null)
                    message = context.getString(R.string.activity_list_contacts_success_add);
                else {
                    Log.e(CallbackAction.class.getSimpleName(), exception.getMessage());
                    message = context.getString(R.string.activity_list_contacts_error_add);
                }
                break;
            case UPDATE:
                if(exception == null)
                    message = context.getString(R.string.activity_list_contacts_success_update);
                else {
                    Log.e(CallbackAction.class.getSimpleName(), exception.getMessage());
                    message = context.getString(R.string.activity_list_contacts_error_update);
                }
                break;
            case REMOVE:
                if(exception == null)
                    message = context.getString(R.string.activity_list_contacts_success_remove);
                else {
                    Log.e(CallbackAction.class.getSimpleName(), exception.getMessage());
                    message = context.getString(R.string.activity_list_contacts_error_remove);
                }
                break;
        }
        Toast infoToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        infoToast.show();
    }
}
