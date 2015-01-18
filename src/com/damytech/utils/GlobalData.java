package com.damytech.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GlobalData
{
    public static final String g_strPrefName = "YiTongConf";
    public static final String g_strBanner = "banner";
    public static final String g_strSplashImgPath = "splashimgpath";
    public static final String g_strLocalSplashImgPath = "localsplashimgpath";

    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
	          "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
	          "\\@" +
	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
	          "(" +
	          "\\." +
	          "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
	          ")+"
	      );

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^[+]?[0-9]{10,13}$");
	
	private static Toast g_Toast = null;
	public static void showToast(Context context, String toastStr)
	{
		if ((g_Toast == null) || (g_Toast.getView().getWindowVisibility() != View.VISIBLE))
		{
			g_Toast = Toast.makeText(context, toastStr, Toast.LENGTH_SHORT);
			g_Toast.show();
		}

		return;
	}

    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        List<String> pName = new ArrayList<String>();// 用于存储所有已安装程序的包名
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);// 判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }

	public static boolean isValidEmail(String strEmail)
	{
		return EMAIL_ADDRESS_PATTERN.matcher(strEmail).matches();
	}

    public static boolean isValidPhone(String strPhone)
    {
        return PHONE_NUMBER_PATTERN.matcher(strPhone).matches();
    }

    public static boolean isOnline(Context ctx)
    {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static boolean getExistBannerID(Context context, long id)
    {
        try {
            SharedPreferences pref = context.getSharedPreferences(g_strPrefName, Context.MODE_PRIVATE);
            String strBannerList = pref.getString(g_strBanner, "");
            String temp = strBannerList;

            while (temp.length() > 0)
            {
                try
                {
                    int nPos = temp.indexOf(',', 0);
                    if (nPos < 0 || nPos == 0)
                    {
                        return true;
                    }
                    String strVal = temp.substring(0, nPos);
                    long nOldID = Long.parseLong(strVal);
                    if (nOldID == id)
                    {
                        return true;
                    }
                    temp = temp.substring(nPos + 1);
                } catch (Exception ex) {
                    temp = "";
                }
            }

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(g_strBanner, strBannerList + Long.toString(id) + ",");
            editor.commit();

            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    public static void SetSplashImgPath(Context context, String imgpath)
    {
        SharedPreferences pref = context.getSharedPreferences(g_strPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(g_strSplashImgPath, imgpath);
        editor.commit();
    }

    public static String GetSplashImgPath(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences(g_strPrefName, Context.MODE_PRIVATE);
        return pref.getString(g_strSplashImgPath, "");
    }

    public static void setLocalSplashImgPath(Context ctx, String videoPath)
    {
        SharedPreferences pref = ctx.getSharedPreferences(g_strPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(g_strLocalSplashImgPath, videoPath);
        editor.commit();
    }

    public static String getLocalSplashImgPath(Context ctx)
    {
        SharedPreferences pref = ctx.getSharedPreferences(g_strPrefName, Context.MODE_PRIVATE);
        return pref.getString(g_strLocalSplashImgPath, "");
    }
}
