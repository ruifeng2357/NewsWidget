package com.damytech.yitongwidget;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import com.damytech.CommService.CommMgr;
import com.damytech.HttpConn.JsonHttpResponseHandler;
import com.damytech.STData.STBanner;
import com.damytech.utils.GlobalData;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class NewsService extends IntentService {
	public static Context m_ctxMain = null;
	private ArrayList<STBanner> m_stBanner = new ArrayList<STBanner>();
	
    private static final int THIRTY_SECOND = 1000 * 30;

	public NewsService() {
		super("");
	}

	public NewsService(String name) {
        super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				while (true)
				{
					try
					{
						CommMgr.commService.GetBannerList(handlerBanner);
                        Thread.sleep(THIRTY_SECOND);
                    }
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
		}, 100);
		return START_STICKY;
	}

	private JsonHttpResponseHandler handlerBanner = new JsonHttpResponseHandler()
	{
		int result = 0;

		@Override
		public void onSuccess(JSONObject jsonData)
		{
			result = 1;
            m_stBanner.clear();
			CommMgr.commService.parseGetBannerList(jsonData, m_stBanner);
		}

		@Override
		public void onFailure(Throwable ex, String exception)
		{
		}

		@Override
		public void onFinish()
		{
			if (result == 1 && m_stBanner != null)
			{
				for (int i = 0; i < m_stBanner.size(); i++)
				{
					if (GlobalData.getExistBannerID(getApplicationContext(), m_stBanner.get(i).Id) == false)
					{
						if (m_ctxMain != null)
						{
							int icon = R.drawable.ic_launcher;
							long when = System.currentTimeMillis();

							Intent notificationIntent = new Intent(m_ctxMain, NotificationActivity.class);
							notificationIntent.putExtra("BANNER", m_stBanner.get(i));
							notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Random random = new Random(System.currentTimeMillis());
                            PendingIntent intent = PendingIntent.getActivity(m_ctxMain, random.nextInt(10000), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                            Notification.Builder notif_builder = new Notification.Builder(m_ctxMain);
                            notif_builder.setSmallIcon(icon);
                            notif_builder.setContentTitle(m_stBanner.get(i).Title);
                            notif_builder.setContentText("新的消息");
                            notif_builder.setWhen(when);
                            notif_builder.setContentIntent(intent);
                            notif_builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                            notif_builder.setAutoCancel(true);

                            NotificationManager notificationManager = (NotificationManager)m_ctxMain.getSystemService(Context.NOTIFICATION_SERVICE);
							notificationManager.notify(random.nextInt(10000), notif_builder.build());
						}
					}
				}
			}
		}
	};		
}
