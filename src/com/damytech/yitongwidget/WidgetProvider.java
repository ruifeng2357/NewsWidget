package com.damytech.yitongwidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.damytech.HttpConn.AsyncHttpClient;
import com.damytech.HttpConn.AsyncHttpResponseHandler;
import com.damytech.STData.STPost;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class WidgetProvider extends AppWidgetProvider
{
    public static int nCount = 0;
    public static ComponentName mService = null;
    public static Context mContext = null;
    public static ArrayList<STPost> arrPost = new ArrayList<STPost>();
    public static AsyncHttpResponseHandler handlerPost = new AsyncHttpResponseHandler()
    {
        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);

            try {
                JSONObject jsonObj = new JSONObject(content);
                int nRetcode = jsonObj.getInt("SVCC_RETVAL");
                if (nRetcode == 0)
                {
                    arrPost.clear();
                    JSONArray result = jsonObj.getJSONArray("SVCC_DATA");
                    for (int i = 0; i < result.length(); i++)
                    {
                        JSONObject data = result.getJSONObject(i);
                        STPost newPost = new STPost();
                        newPost.Id = data.getLong("Id");
                        newPost.Content = data.getString("Content");
                        newPost.ViewURL = data.getString("ViewURL");

                        arrPost.add(newPost);
                        SetSelText(newPost.Content, i, mContext);
                        SetSelPost(newPost.ViewURL, i, mContext);
                    }

                    if (arrPost != null)
                    {
                        SetTotalCount(arrPost.size(), mContext);
                        mService = mContext.startService(new Intent(mContext, UpdatePostService.class));
                    }
                }
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        @Override
        public void onFailure(Throwable error, String content)
        {
            super.onFailure(error, content);
        }
    };

    public void GetAdvertList(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();

        try {
            url = "http://218.25.54.28:10241/Service.SVC/GetPostList";
            client.setTimeout(4000);
            client.get(url, handler);
        }
        catch (Exception e) {
            e.printStackTrace();
            if (handler != null)
                handler.onFailure(null, e.getMessage());
        }
    }

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        mContext = context;
        GetAdvertList(handlerPost);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.init_layout);
        Intent intent = new Intent(context, WellComeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.imgBack, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds[0], remoteViews);
	}

    public static class UpdatePostService extends Service implements Runnable
    {
        private Handler mHandler;
        private static final int TIMER_PERIOD = 1 * 10000;

        private long preTime, curTime;

        @Override
        public void onCreate()
        {
            mHandler = new Handler();
        }

        @Override
        public void onStart(Intent intent, int startId)
        {
            preTime = System.currentTimeMillis();
            mHandler.postDelayed(this, TIMER_PERIOD);
        }

        @Override
        public IBinder onBind(Intent intent)
        {
            return null;
        }

        @Override
        public void run()
        {
            try {
                curTime = System.currentTimeMillis();

                RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.init_layout);

                long CUR_PERIOD = curTime - preTime;
                if( CUR_PERIOD > TIMER_PERIOD )
                {
                    nCount = GetSelNo(mContext);
                    views.setTextViewText(R.id.lblContent, GetSelText(mContext, nCount));
                    String strURL = GetSelPost(mContext, GetSelNo(mContext));
                    if (strURL != null && strURL.length() > 0 ) {
                        Intent intentPost = new Intent(mContext, PostActivity.class);
                        intentPost.putExtra("ViewURL", strURL);
                        intentPost.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                        PendingIntent pendingIntentPost = PendingIntent.getActivity(mContext, 0, intentPost, 0);
                        views.setOnClickPendingIntent(R.id.lblContent, pendingIntentPost);
                    }
                    preTime = curTime;
                    if (GetTotalCount(mContext) != 0)
                        nCount = (nCount+1) % GetTotalCount(mContext);
                    SetSelNo(nCount, mContext);
                }

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                ComponentName componentName = new ComponentName(this, WidgetProvider.class);
                appWidgetManager.updateAppWidget(componentName, views);

                mHandler.postDelayed(this, TIMER_PERIOD);
            } catch (Exception ex) {
            }
        }
    }

    public static int GetSelNo(Context context)
    {
        int nSelNo = 0;
        SharedPreferences pref = context.getSharedPreferences("YiTong", Context.MODE_PRIVATE);
        nSelNo = pref.getInt("SelNo", 0);

        return nSelNo;
    }

    public static void SetSelNo(int nNo, Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("YiTong", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("SelNo", nNo);
        editor.commit();

        return;
    }

    public static int GetTotalCount(Context context)
    {
        int nSelNo = 0;
        SharedPreferences pref = context.getSharedPreferences("YiTong", Context.MODE_PRIVATE);
        nSelNo = pref.getInt("TotalCount", 0);

        return nSelNo;
    }

    public static void SetTotalCount(int nNo, Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("YiTong", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("TotalCount", nNo);
        editor.commit();

        return;
    }

    public static String GetSelText(Context context, int i)
    {
        String selText = "";
        SharedPreferences pref = context.getSharedPreferences("YiTong", Context.MODE_PRIVATE);
        selText = pref.getString("SelText" + i, "");

        return selText;
    }

    public static void SetSelText(String selText, int i, Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("YiTong", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("SelText" + i, selText);
        editor.commit();

        return;
    }

    public static String GetSelPost(Context context, int i)
    {
        String selText = "";
        SharedPreferences pref = context.getSharedPreferences("YiTong", Context.MODE_PRIVATE);
        selText = pref.getString("SelPost" + i, "");

        return selText;
    }

    public static void SetSelPost(String selText, int i, Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("YiTong", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("SelPost" + i, selText);
        editor.commit();

        return;
    }
}
