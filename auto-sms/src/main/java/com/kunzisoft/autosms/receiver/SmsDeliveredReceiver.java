package com.kunzisoft.autosms.receiver;

import com.kunzisoft.autosms.service.SmsDeliveredService;

public class SmsDeliveredReceiver extends WakefulBroadcastReceiver {

    @Override
    protected Class getServiceClass() {
        return SmsDeliveredService.class;
    }
}
