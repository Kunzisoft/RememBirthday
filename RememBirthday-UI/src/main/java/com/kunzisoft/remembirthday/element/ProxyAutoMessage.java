package com.kunzisoft.remembirthday.element;

import android.util.Log;

import com.kunzisoft.autosms.model.AutoSms;
import com.kunzisoft.remembirthday.exception.NoPhoneNumberException;
import com.kunzisoft.remembirthday.exception.PhoneNumberNotInitializedException;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by joker on 20/08/17.
 */
public class ProxyAutoMessage {

    private static final String TAG = "ProxyAutoMessage";

    public static AutoMessage getFromAutoSms(Date anniversary, AutoSms autoSms) {
        DateTime dateTimeAnniversary = new DateTime(anniversary);

        DateTime dateTimeScheduled = new DateTime(autoSms.getDateScheduled());
        dateTimeScheduled = new DateTime(DateUnknownYear.getNextAnniversary(dateTimeScheduled.toDate()));

        int minutes = Minutes.minutesBetween(dateTimeScheduled, dateTimeAnniversary).getMinutes();

        Log.e(TAG, "dateTime : " + dateTimeScheduled + " dateTimeAnniversary " + dateTimeAnniversary + " minutes " + minutes);

        AutoMessage autoMessage = new AutoMessage(anniversary, minutes);
        autoMessage.setId(autoSms.getDateCreated().getTime());
        autoMessage.setContent(autoSms.getMessage());
        return autoMessage;
    }

    public static AutoSms getAutoSms(Contact contact, AutoMessage autoMessage) {
        AutoSms autoSms = new AutoSms();
        if (contact.getLookUpKey().equals("")) {
            Log.e(TAG, "Unknown lookup key of contact");
        } else {
            autoSms.setRecipientLookup(contact.getLookUpKey());
        }
        try {
            autoSms.setRecipientPhoneNumber(contact.getMainPhoneNumber().getNumber());
            autoSms.setMessage(autoMessage.getContent());
            if(autoMessage.getId() != AutoMessage.ID_UNDEFINED)
                autoSms.setDateCreated(new Date(autoMessage.getId()));
            else
                autoSms.setDateCreated(new Date());
            autoSms.setDateScheduled(autoMessage.getDate());
            autoSms.setStatus(AutoSms.Status.PENDING);
        } catch (PhoneNumberNotInitializedException |NoPhoneNumberException e) {
            Log.e(TAG, "Unable to create Auto-SMS, phone number can't be open");
        }
        //TODO Proxy
        return autoSms;
    }

    public static List<AutoMessage> getFromAutoSmsList(Date anniversary, List<AutoSms> autoSmsList) {
        List<AutoMessage> autoMessages = new ArrayList<>();
        for(AutoSms autoSms : autoSmsList) {
            autoMessages.add(getFromAutoSms(anniversary, autoSms));
        }
        return autoMessages;
    }
}
