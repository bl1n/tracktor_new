package com.elegion.tracktor.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.elegion.tracktor.App;

import java.io.ByteArrayOutputStream;

public class ScreenshotMaker {

    private static Context getContext() {
        return App.getContext();
    }

    private static String getCompress() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return preferences.getString("compress", "");
    }


    public static Bitmap makeScreenshot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static String toBase64(Bitmap bitmap) {
        return Base64.encodeToString(getBytes(bitmap, getCompress()), Base64.DEFAULT);
    }

    public static Bitmap fromBase64(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private static byte[] getBytes(Bitmap bm, String compress) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, Integer.parseInt(compress), byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}