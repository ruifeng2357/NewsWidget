package com.damytech.yitongwidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import com.damytech.CommService.CommMgr;
import com.damytech.HttpConn.JsonHttpResponseHandler;
import com.damytech.utils.GlobalData;
import com.damytech.utils.ResolutionSet;
import android.os.*;
import com.damytech.utils.SmartImageView.SmartImageView;
import org.json.JSONObject;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class WellComeActivity extends Activity {
    RelativeLayout mainLayout;
    boolean bInitialized = false;

    SmartImageView imgBack = null;

    private String upgrade_url = "";
    private String local_file_path = "";

    private String imgPath = "";
    private String localImgPath = "";

    private ProgressDialog progDialog = null;
    public JsonHttpResponseHandler handlerNewVersion;

    private boolean bImgFlag = true, bDownloadFlag = true;

    ProgressDialog dialog = null;
    private JsonHttpResponseHandler handlerSplash = new JsonHttpResponseHandler()
    {
        @Override
        public void onSuccess(JSONObject response) {
            super.onSuccess(response);

            imgPath = CommMgr.commService.parseGetSplashImgPath(response);
        }

        @Override
        public void onFailure(Throwable e, JSONObject errorResponse) {
            super.onFailure(e, errorResponse);
        }

        @Override
        public void onFinish() {
            super.onFinish();

            String oldSplashPath = GlobalData.GetSplashImgPath(WellComeActivity.this);
            if ( imgPath == null || (imgPath != null && imgPath.length() == 0))
            {
                if (oldSplashPath.length() == 0)
                {
                    imgBack.setImageResource(R.drawable.noimage);
                }
                else
                {
                    try {
                        Uri imgPath = Uri.parse(GlobalData.getLocalSplashImgPath(WellComeActivity.this));
                        imgBack.setImageURI(imgPath);
                    }
                    catch (Exception ex)
                    {
                        imgBack.setImageResource(R.drawable.noimage);
                    }
                }

                String version = "";
                try {
                    PackageInfo info = WellComeActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                    version = info.versionName;
                    CommMgr.commService.GetNewVersion(handlerNewVersion, version);
                }catch (Exception ex) {}

                return;
            }

            if (imgPath != null && imgPath.length() > 0)
            {
                if (imgPath.equals(GlobalData.GetSplashImgPath(WellComeActivity.this)) == true)
                {
                    String strLocalSplashPath = GlobalData.getLocalSplashImgPath(WellComeActivity.this);
                    try
                    {
                        Uri imgPath = Uri.parse(strLocalSplashPath);
                        imgBack.setImageURI(imgPath);
                    }
                    catch(Exception ex)
                    {
                        imgBack.setImageResource(R.drawable.noimage);
                    }

                    String version = "";
                    try {
                        PackageInfo info = WellComeActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                        version = info.versionName;
                        CommMgr.commService.GetNewVersion(handlerNewVersion, version);
                    }catch (Exception ex) {}
                }
                else
                {
                    DownloadSplashImg();
                }
            }
        }
    };

    private void DownloadSplashImg()
    {
        Thread thr = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    int nBytesRead = 0, nByteWritten = 0;
                    byte[] buf = new byte[1024];

                    URLConnection urlConn = null;
                    URL fileUrl = null;
                    InputStream inStream = null;
                    OutputStream outStream = null;

                    File dir_item = null, file_item = null;

                    // Show progress dialog
                    runOnUiThread(runnable_showProgressimg);

                    // Downloading file from address
                    fileUrl = new URL(imgPath);
                    urlConn = fileUrl.openConnection();
                    inStream = urlConn.getInputStream();
                    localImgPath = imgPath.substring(imgPath.lastIndexOf("/") + 1);
                    dir_item = new File(Environment.getExternalStorageDirectory(), "Download");
                    dir_item.mkdirs();
                    file_item = new File(dir_item, localImgPath);

                    localImgPath = file_item.getAbsolutePath();

                    outStream = new BufferedOutputStream(new FileOutputStream(file_item));

                    while (bImgFlag && ((nBytesRead = inStream.read(buf)) != -1))
                    {
                        outStream.write(buf, 0, nBytesRead);
                        nByteWritten += nBytesRead;
                        UpdateProgressImg(nByteWritten);
                    }

                    if (bImgFlag == true)
                    {
                        UpdateProgressImg(getResources().getString(R.string.download_success));

                        inStream.close();
                        outStream.flush();
                        outStream.close();
                        /////////////////////////////////////////////////////////////////////////
                    }

                    // Hide progress dialog
                    runOnUiThread(runnable_hideProgressimg);

                    // Finish downloading and install
                    runOnUiThread(runnable_finish_downloadimg);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    runOnUiThread(runnable_downloadimg_error);
                }
            }
        });

        thr.start();
    }

    private Runnable runnable_showProgressimg = new Runnable() {
        @Override
        public void run() {
            showProgressimg();
        }
    };

    public void showProgressimg()
    {
        if (dialog == null)
        {
            dialog = new ProgressDialog(WellComeActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getResources().getString(R.string.waiting));
            dialog.setCancelable(true);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    bImgFlag = false;
                }
            });
        }

        if (dialog.isShowing())
            return;

        dialog.show();
    }

    private Runnable runnable_hideProgressimg = new Runnable() {
        @Override
        public void run() {
            showProgressimg();
        }
    };

    public void hideProgressimg()
    {
        if (dialog != null)
            dialog.dismiss();
    }

    Runnable runnable_finish_downloadimg = new Runnable()
    {
        public void run()
        {
            if (bImgFlag == true)
            {
                GlobalData.SetSplashImgPath(WellComeActivity.this, imgPath);
                GlobalData.setLocalSplashImgPath(WellComeActivity.this, localImgPath);
                try {
                    Uri imgPath = Uri.parse(GlobalData.getLocalSplashImgPath(WellComeActivity.this));
                    imgBack.setImageURI(imgPath);
                }
                catch (Exception ex)
                {
                    imgBack.setImageResource(R.drawable.noimage);
                }

                hideProgressimg();
            }

            String version = "";
            try {
                PackageInfo info = WellComeActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                version = info.versionName;
                CommMgr.commService.GetNewVersion(handlerNewVersion, version);
            }catch (Exception ex) {}
        }
    };

    Runnable runnable_downloadimg_error = new Runnable() {
        @Override
        public void run() {
            GlobalData.showToast(WellComeActivity.this, getResources().getString(R.string.download_fail));
            hideProgressimg();
            if (GlobalData.GetSplashImgPath(WellComeActivity.this).length() == 0)
            {
                imgBack.setImageResource(R.drawable.noimage);
            }
            else
            {
                try {
                    Uri splashPath = Uri.parse(GlobalData.getLocalSplashImgPath(WellComeActivity.this));
                    imgBack.setImageURI(splashPath);
                }
                catch (Exception ex)
                {
                    imgBack.setImageResource(R.drawable.noimage);
                }
            }

            String version = "";
            try {
                PackageInfo info = WellComeActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                version = info.versionName;
                CommMgr.commService.GetNewVersion(handlerNewVersion, version);
            }catch (Exception ex) {}
        }
    };

    private void UpdateProgressImg(int nValue)
    {
        String strValue;
        strValue = String.format("%.2fMB", nValue / 1024.0f / 1024.0f );
        UpdateProgressImg(strValue);
    }

    private void UpdateProgressImg(final String szMsg)
    {
        Runnable runnable_update = new Runnable() {
            @Override
            public void run() {
                dialog.setMessage(szMsg);
            }
        };

        runOnUiThread(runnable_update);
    }

    public Handler handler= new Handler() {
        public void handleMessage(Message msg){
            startActivity(new Intent(WellComeActivity.this, MainActivity.class));
            WellComeActivity.this.finish();
        }
    };
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome);

        NewsService.m_ctxMain = getApplicationContext();
        WellComeActivity.this.startService(new Intent(WellComeActivity.this, NewsService.class));

        imgBack = (SmartImageView) findViewById(R.id.viewWellCome_Back);

        mainLayout = (RelativeLayout)findViewById(R.id.rlWellComeBack);
        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (bInitialized == false)
                        {
                            Rect r = new Rect();
                            mainLayout.getLocalVisibleRect(r);
                            ResolutionSet._instance.setResolution(r.width(), r.height());
                            ResolutionSet._instance.iterateChild(findViewById(R.id.rlWellComeBack));
                            bInitialized = true;
                        }
                    }
                }
        );

        initHandler();

        CommMgr.commService.GetSplashImgPath(handlerSplash);
    }

    private void initHandler()
    {
        handlerNewVersion = new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(JSONObject jsonData)
            {
                String strURL = CommMgr.commService.parseGetNewVersion(jsonData);

                if (strURL.length() > 0)
                {
                    upgrade_url = strURL;
                    upgradeApp();
                }
                else
                    handler.sendEmptyMessageDelayed(0, 2000);
            }

            @Override
            public void onFailure(Throwable ex, String exception) {}

            @Override
            public void onFinish() {}
        };
    }

    public void upgradeApp()
    {
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(WellComeActivity.this);
        builder.setTitle(getResources().getString(R.string.app_name));
        builder.setMessage(getString(R.string.oldversion_error) + "\n" + getResources().getString(R.string.updateapp));
        builder.setPositiveButton(getResources().getString(R.string.Dialog_Ok), click_UpgradeBtn_Listener);
        builder.setNegativeButton(getResources().getString(R.string.Dialog_Cancel), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                handler.sendEmptyMessageDelayed(0, 10);
            }
        });
        builder.show();
    }

    private DialogInterface.OnClickListener click_UpgradeBtn_Listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            InstallNewApp();
        }
    };

    private void InstallNewApp()
    {
        Thread thr = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    int nBytesRead = 0, nByteWritten = 0;
                    byte[] buf = new byte[1024];

                    URLConnection urlConn = null;
                    URL fileUrl = null;
                    InputStream inStream = null;
                    OutputStream outStream = null;

                    File dir_item = null, file_item = null;

                    runOnUiThread(runnable_showProgress);

                    fileUrl = new URL(upgrade_url);
                    urlConn = fileUrl.openConnection();
                    inStream = urlConn.getInputStream();
                    local_file_path = upgrade_url.substring(upgrade_url.lastIndexOf("/") + 1);
                    dir_item = new File(Environment.getExternalStorageDirectory(), "Download");
                    dir_item.mkdirs();
                    file_item = new File(dir_item, local_file_path);

                    outStream = new BufferedOutputStream(new FileOutputStream(file_item));

                    while ( bDownloadFlag && ((nBytesRead = inStream.read(buf)) != -1))
                    {
                        outStream.write(buf, 0, nBytesRead);
                        nByteWritten += nBytesRead;
                        UpdateProgress(nByteWritten);
                    }

                    if (bDownloadFlag == true)
                    {
                        UpdateProgress(getResources().getString(R.string.download_success));

                        inStream.close();
                        outStream.flush();
                        outStream.close();
                        /////////////////////////////////////////////////////////////////////////

                        runOnUiThread(runnable_hideProgress);
                        runOnUiThread(runnable_finish_download);
                    }
                    else
                    {
                        runOnUiThread(runnable_download_error);
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    runOnUiThread(runnable_download_error);
                }
            }
        });

        thr.start();
    }


    private void UpdateProgress(int nValue)
    {
        String strValue;
        strValue = String.format("%.2fMB", nValue / 1024.0f / 1024.0f );
        UpdateProgress(strValue);
    }

    private void UpdateProgress(final String szMsg)
    {
        Runnable runnable_update = new Runnable() {
            @Override
            public void run() {
                progDialog.setMessage(szMsg);
            }
        };

        runOnUiThread(runnable_update);
    }

    private Runnable runnable_showProgress = new Runnable() {
        @Override
        public void run() {
            showProgress();
        }
    };

    private Runnable runnable_hideProgress = new Runnable() {
        @Override
        public void run() {
            showProgress();
        }
    };

    public void showProgress()
    {
        if (progDialog == null)
        {
            progDialog = new ProgressDialog(WellComeActivity.this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setMessage(getResources().getString(R.string.waiting));
            progDialog.setCancelable(false);
            progDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    bDownloadFlag = false;
                }
            });
        }

        if (progDialog.isShowing())
            return;

        progDialog.show();
    }

    public void hideProgress()
    {
        if (progDialog != null)
            progDialog.dismiss();
    }

    Runnable runnable_finish_download = new Runnable()
    {
        public void run()
        {
            Intent intent_install = new Intent( Intent.ACTION_VIEW);
            intent_install.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString() + "/download/" + local_file_path)), "application/vnd.android.package-archive");
            intent_install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent_install);

            WellComeActivity.this.finish();
        }
    };

    Runnable runnable_download_error = new Runnable() {
        @Override
        public void run() {
            GlobalData.showToast(WellComeActivity.this, getResources().getString(R.string.download_fail));
            hideProgress();

            handler.sendEmptyMessageDelayed(0, 2000);
        }
    };
}
