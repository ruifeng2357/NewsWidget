package com.damytech.STData;

public class STServiceData {
	// Service Address
    public static String serviceAddr = "http://218.25.54.28:10241/Service.svc/";
//    public static String serviceAddr = "http://service.35888888.cn/Service.svc/";

    // Error Code
    public static final int ERR_SUCCESS = 0;
    public static final int ERR_FAIL = 500;

    // Command List
    public static String cmdGetNewVersion = "GetNewVersion";
    public static String cmdGetAdvertList = "GetAdvertList";
    public static String cmdGetBannerList = "GetBannerList";
    public static String cmdGetSiteAddr = "GetSiteAddr";
    public static String cmdGetSplashImgPath = "GetSplashImgPath";

	// Connection Info
	public static int connectTimeout = 4 * 1000; // 5 Seconds
    public static int connectImageTimeout = 19 * 1000; // 5 Seconds
}
