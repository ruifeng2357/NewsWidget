package com.damytech.yitongwidget;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.damytech.STData.STBanner;
import com.damytech.utils.GlobalData;
import com.damytech.utils.ResolutionSet;

import java.util.List;

public class PostActivity extends Activity {
    private WebView webData;
    private ProgressBar prgsStatus = null;

    private String stViewURL = "";

    RelativeLayout mainLayout;
    boolean bInitialized = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //stViewURL = getIntent().getStringExtra("ViewURL");
        int nPos = WidgetProvider.GetSelNo(PostActivity.this);
        int nTotal = WidgetProvider.GetTotalCount(PostActivity.this);
        nPos = (nPos + 2 * nTotal - 1) % nTotal;
        stViewURL = WidgetProvider.GetSelPost(PostActivity.this, nPos);
        if (stViewURL == null || stViewURL.length() == 0)
            PostActivity.this.finish();

        prgsStatus = (ProgressBar) findViewById(R.id.prgsPost_Status);

        webData = (WebView) findViewById(R.id.webPost_Site);
        webData.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if (url == null) {
                    return false;
                }
                if (url.startsWith("lngxet://")){
                    String packageName = url.trim().replace("lngxet://", "");
                    if(packageName == null || packageName.length() < 1 ||! GlobalData.isAvilible(PostActivity.this, packageName)) {
                        return false;
                    }
                    launchAppByPackageName(packageName);
                    return true;
                } else {
                    view.loadUrl(url);
                }
                return false;
            };

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                prgsStatus.setVisibility(View.GONE);
            }
        });
        webData.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
        webData.getSettings().setLoadWithOverviewMode(true);
        webData.getSettings().setUseWideViewPort(true);
        webData.getSettings().setBuiltInZoomControls(false);
        webData.getSettings().setDisplayZoomControls(false);
        webData.getSettings().setSupportZoom(false);
        webData.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webData.getSettings().setDefaultTextEncodingName("UTF-8");
        webData.getSettings().setJavaScriptEnabled(true);
        webData.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        webData.loadUrl(stViewURL);

        mainLayout = (RelativeLayout)findViewById(R.id.rlPostBack);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(R.id.rlPostBack));
                            bInitialized = true;
                        }
                    }
                }
        );
    }

    private void launchAppByPackageName(String packageName) {
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageName);

        List<ResolveInfo> resolveInfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveInfo = resolveInfoList.iterator().next();
        if (resolveInfo != null) {
            String activityPackageName = resolveInfo.activityInfo.packageName;
            String className = resolveInfo.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName componentName = new ComponentName( activityPackageName, className);

            intent.setComponent(componentName);
            startActivity(intent);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }
}
