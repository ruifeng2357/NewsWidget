package com.damytech.CommService;

import com.damytech.HttpConn.AsyncHttpResponseHandler;
import com.damytech.STData.STAdvert;
import com.damytech.STData.STBanner;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommMgr {
	
	public static CommMgr commService = new CommMgr();
    public MainSvcMgr mainMgr = new MainSvcMgr();

	public CommMgr() {}

    public void GetNewVersion(AsyncHttpResponseHandler handler, String version)
    {
        mainMgr.GetNewVersion(handler, version);
    }

    public String parseGetNewVersion(JSONObject jsonObject)
    {
        return mainMgr.parseGetNewVersion(jsonObject);
    }

    public void GetAdvertList(AsyncHttpResponseHandler handler)
    {
        mainMgr.GetAdvertList(handler);
    }

    public String parseGetAdvertList(JSONObject jsonObject, ArrayList<STAdvert> dataList)
    {
        return mainMgr.parseGetAdvertList(jsonObject, dataList);
    }

    public void GetBannerList(AsyncHttpResponseHandler handler)
    {
        mainMgr.GetBannerList(handler);
    }

    public String parseGetBannerList(JSONObject jsonObject, ArrayList<STBanner> dataList)
    {
        return mainMgr.parseGetBannerList(jsonObject, dataList);
    }

    public void GetSiteAddr(AsyncHttpResponseHandler handler)
    {
        mainMgr.GetSiteAddr(handler);
    }

    public String parseGetSiteAddr(JSONObject jsonObject)
    {
        return mainMgr.parseGetSiteAddr(jsonObject);
    }

    public void GetSplashImgPath(AsyncHttpResponseHandler handler)
    {
        mainMgr.GetSplashImgPath(handler);
    }

    public String parseGetSplashImgPath(JSONObject jsonObject)
    {
        return mainMgr.parseGetSplashImgPath(jsonObject);
    }
}
