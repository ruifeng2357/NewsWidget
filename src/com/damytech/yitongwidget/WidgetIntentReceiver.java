package com.damytech.yitongwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WidgetIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent)
    {
		if(intent.getAction().equals("com.damytech.intent.action.RUN_PROGRAM"))
        {
		}
	}
}
