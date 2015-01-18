package com.damytech.yitongwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.damytech.utils.GlobalData;

public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		NewsService.m_ctxMain = context;
		Intent serviceIntent = new Intent(context, NewsService.class);
		context.startService(serviceIntent);
        GlobalData.showToast(context, "OK");
	}
}
