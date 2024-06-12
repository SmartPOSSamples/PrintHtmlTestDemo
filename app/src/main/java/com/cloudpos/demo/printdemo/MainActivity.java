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
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.demo.printdemo.util.HtmlFileUtils;
import com.cloudpos.printer.PrinterDevice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private PrinterDevice device;
    private Context mContext;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button htmlBtn = findViewById(R.id.printHtml);
        Button imageBtn = findViewById(R.id.printImage);
        mContext = this;
        htmlBtn.setOnClickListener(this);
        imageBtn.setOnClickListener(this);
        if (device == null) {
            device = (PrinterDevice) POSTerminal.getInstance(mContext).getDevice("cloudpos.device.printer");
        }
        try {
            device.open();
        } catch (DeviceException e) {
            throw new RuntimeException(e);
        }
        webView = (WebView) findViewById(R.id.wv_webview);
        webView.enableSlowWholeDocumentDraw();
        try {
            String htmlContent = HtmlFileUtils.readHtmlFile(getAssets(), "language.html");
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void printHtml() {
        try {
            String htmlContent = HtmlFileUtils.readHtmlFile(getAssets(), "language.html");
            device.printHTML(mContext, htmlContent, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printImage() {
        try {
            String htmlContent = HtmlFileUtils.readHtmlFile(getAssets(), "language.html");
            Bitmap bitmap = device.convertHTML2image(htmlContent);
            device.printBitmap(bitmap);
            bitmap.recycle();
            device.cutPaper();
        } catch (DeviceException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.printHtml:
                printHtml();
                break;
            case R.id.printImage:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        printImage();
                    }
                }).start();

                break;
            default:
                break;
        }
    }

    private void saveBitmapAsImage(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/print.png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}