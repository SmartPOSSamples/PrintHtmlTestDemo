package com.cloudpos.demo.printdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cloudpos.Device;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.demo.printdemo.util.HtmlFileUtils;
import com.cloudpos.printer.PrinterDevice;
import com.cloudpos.printer.PrinterHtmlListener;
import com.cloudpos.sdk.impl.AbstractDevice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static Context mContext;
    private WebView webView;
    private String htmlContent;
    private ExecutorService threadPool;
    private PrinterSingleton printer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button htmlBtn = findViewById(R.id.printHtml);
        Button imageBtn = findViewById(R.id.printImage);
        Button htmlSyncBtn = findViewById(R.id.printHtmlSync);
        Button imageSyncBtn = findViewById(R.id.printImageSync);
        mContext = this;
        threadPool = Executors.newSingleThreadExecutor();
        htmlBtn.setOnClickListener(this);
        imageBtn.setOnClickListener(this);
        htmlSyncBtn.setOnClickListener(this);
        imageSyncBtn.setOnClickListener(this);
        printer = PrinterSingleton.getInstance(mContext);
        webView = (WebView) findViewById(R.id.wv_webview);
        webView.enableSlowWholeDocumentDraw();
        try {
            htmlContent = HtmlFileUtils.readHtmlFile(getAssets(), "language.html");
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.printHtml:
                printer.printHtmlAsync(htmlContent);
                break;
            case R.id.printImage:
                printer.printImageAsync(htmlContent);
                break;
            case R.id.printHtmlSync:
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        printer.printHtmlSync(htmlContent);
                    }
                });
                break;
            case R.id.printImageSync:
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        printer.printImageSync(htmlContent);
                    }
                });
                break;
            default:
                break;
        }
    }

}