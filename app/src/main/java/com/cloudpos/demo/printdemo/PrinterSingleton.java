package com.cloudpos.demo.printdemo;

import android.content.Context;
import android.graphics.Bitmap;

import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.PrinterDevice;
import com.cloudpos.printer.PrinterHtmlListener;

public class PrinterSingleton {

    private PrinterDevice device;
    private Context context;
    private static volatile PrinterSingleton instance;

    private PrinterSingleton(Context mContext) {
        if (device == null) {
            context = mContext;
            device = (PrinterDevice) POSTerminal.getInstance(mContext).getDevice("cloudpos.device.printer");
            try {
                device.open();
            } catch (DeviceException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static PrinterSingleton getInstance(Context mContext) {
        if (instance == null) {
            synchronized (PrinterSingleton.class) {
                if (instance == null) {
                    instance = new PrinterSingleton(mContext);
                }
            }
        }
        return instance;
    }

    public void printHtmlAsync(String htmlContent) {
        try {
            device.printHTML(htmlContent, printerHtmlListener);
        } catch (DeviceException e) {
            throw new RuntimeException(e);
        }
    }

    public void printImageAsync(String htmlContent) {
        try {
            device.convertHTML2image(context, htmlContent, printerHtmlListener);
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    public synchronized void printHtmlSync(String htmlContent) {
        try {
            device.printHTML(context, htmlContent);
        } catch (DeviceException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void printImageSync(String htmlContent) {
        try {
            Bitmap bitmap = device.convertHTML2image(htmlContent);
            device.printBitmap(bitmap);
            bitmap.recycle();
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    private final PrinterHtmlListener printerHtmlListener = new PrinterHtmlListener() {
        @Override
        public void onGet(Bitmap bitmap, int i) {
            if (bitmap != null) {
                try {
                    device.printBitmap(bitmap);
                    bitmap.recycle();
                } catch (DeviceException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void onFinishPrinting(int i) {

        }
    };
}


