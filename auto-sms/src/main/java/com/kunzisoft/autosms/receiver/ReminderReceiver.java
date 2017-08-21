package com.kunzisoft.autosms.receiver;

import com.kunzisoft.autosms.service.ReminderService;

public class ReminderReceiver extends WakefulBroadcastReceiver {

    @Override
    protected Class getServiceClass() {
        return ReminderService.class;
    }
}
