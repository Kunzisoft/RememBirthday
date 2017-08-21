package com.kunzisoft.autosms.receiver;

import com.kunzisoft.autosms.service.SmsSenderService;

public class SmsSenderReceiver extends WakefulBroadcastReceiver {

    @Override
    protected Class getServiceClass() {
        return SmsSenderService.class;
    }
}
