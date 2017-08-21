package com.kunzisoft.autosms.receiver;

import com.kunzisoft.autosms.service.SmsSentService;

public class SmsSentReceiver extends WakefulBroadcastReceiver {

    @Override
    protected Class getServiceClass() {
        return SmsSentService.class;
    }
}
