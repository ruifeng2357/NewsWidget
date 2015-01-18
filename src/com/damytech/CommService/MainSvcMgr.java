package com.damytech.CommService;

import com.damytech.HttpConn.AsyncHttpClient;
import com.damytech.HttpConn.AsyncHttpResponseHandler;
import com.damytech.HttpConn.RequestParams;
import com.damytech.STData.*;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainSvcMgr {

    public void GetNewVersion(AsyncHttpResponseHandler handler, String version)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            url = STServiceData.serviceAddr + STServiceData.cmdGetNewVersion;
            param.put("version", version);
            client.setTimeout(STServiceData.connectTimeout);
            client.get(url, param, handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String parseGetNewVersion(JSONObject jsonObject)
    {
        int retResult = STServiceData.ERR_FAIL;

        try {
            retResult = jsonObject.getInt("SVCC_RETVAL");
            if (STServiceData.ERR_SUCCESS != retResult)
                return "";
            else
                return jsonObject.getString("SVCC_DATA");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public void GetAdvertList(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            url = STServiceData.serviceAddr + STServiceData.cmdGetAdvertList;
            client.setTimeout(STServiceData.connectTimeout);
            client.get(url, param, handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String parseGetAdvertList(JSONObject jsonObject, ArrayList<STAdvert> dataList)
    {
        String retMsg = "";
        String basePath = "";
        int retResult = STServiceData.ERR_FAIL;

        dataList.clear();
        try {
            retResult = jsonObject.getInt("SVCC_RETVAL");
            basePath = jsonObject.getString("SVCC_BASEURL");
            if (STServiceData.ERR_SUCCESS != retResult)
            {
                retMsg = jsonObject.getString("SVCC_RETMSG");
            }
            else
            {
                JSONArray jsonArray = jsonObject.getJSONArray("SVCC_DATA");
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject item = jsonArray.getJSONObject(i);

                    STAdvert stInfo = new STAdvert();
                    stInfo.Id= item.getLong("Id");
                    stInfo.Title = item.getString("Title");
                    stInfo.ImgPath = basePath + item.getString("ImgPath");

                    dataList.add(stInfo);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retMsg;
    }

    public void GetBannerList(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            url = STServiceData.serviceAddr + STServiceData.cmdGetBannerList;
            client.setTimeout(STServiceData.connectTimeout);
            client.get(url, param, handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String parseGetBannerList(JSONObject jsonObject, ArrayList<STBanner> dataList)
    {
        String retMsg = "";
        int retResult = STServiceData.ERR_FAIL;

        dataList.clear();
        try {
            retResult = jsonObject.getInt("SVCC_RETVAL");
            if (STServiceData.ERR_SUCCESS != retResult)
            {
                retMsg = jsonObject.getString("SVCC_RETMSG");
            }
            else
            {
                JSONArray jsonArray = jsonObject.getJSONArray("SVCC_DATA");
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject item = jsonArray.getJSONObject(i);

                    STBanner stInfo = new STBanner();
                    stInfo.Id= item.getLong("Id");
                    stInfo.Title = item.getString("Title");
                    stInfo.LinkURL = item.getString("LinkURL");

                    dataList.add(stInfo);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return retMsg;
    }

    public void GetSiteAddr(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            url = STServiceData.serviceAddr + STServiceData.cmdGetSiteAddr;
            client.setTimeout(STServiceData.connectTimeout);
            client.get(url, param, handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String parseGetSiteAddr(JSONObject jsonObject)
    {
        int retResult = STServiceData.ERR_FAIL;

        try {
            retResult = jsonObject.getInt("SVCC_RETVAL");
            if (STServiceData.ERR_SUCCESS != retResult)
                return "";
            else
                return jsonObject.getString("SVCC_DATA");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public void GetSplashImgPath(AsyncHttpResponseHandler handler)
    {
        String url = "";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams param = new RequestParams();

        try {
            url = STServiceData.serviceAddr + STServiceData.cmdGetSplashImgPath;
            client.setTimeout(STServiceData.connectTimeout);
            client.get(url, null, handler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String parseGetSplashImgPath(JSONObject jsonObject)
    {
        int retResult = STServiceData.ERR_FAIL;

        try {
            retResult = jsonObject.getInt("SVCC_RETVAL");
            if (STServiceData.ERR_SUCCESS != retResult)
                return "";
            else
                return jsonObject.getString("SVCC_DATA");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }
}
