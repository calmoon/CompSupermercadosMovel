package com.dispmoveis.compsupermercadosmovel.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class Util {

    public static BigDecimal currencyToBigDecimal(String currencyStr, Locale locale) {
        String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance(locale).getCurrency().getSymbol());

        String cleanString = currencyStr.replaceAll(replaceable, "");

        BigDecimal parsed;
        try {
            parsed = new BigDecimal(cleanString).setScale(
                    2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR
            );
        }
        catch (NumberFormatException e) {
            parsed = new BigDecimal(0);
        }

        return parsed;
    }

    public static void setBitmapFromURL(ImageView imageView, String imageUrl) {
        String[] allowedTypes = new String[] {
                "image/jpeg",
                "image/jpg",
                "image/png",
                "image/bmp",
                "image/gif",
                "binary/octet-stream",
                "application/octet-stream"
        };
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(imageUrl, new BinaryHttpResponseHandler(allowedTypes) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                Bitmap image = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
                if (image != null) {
                    imageView.setImageBitmap(image);
                } else {
                    onFailure(statusCode, headers, binaryData, new NullPointerException());
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
                Log.e("HTTP_BITMAP_URL_FAIL",
                        "Failed to load bitmap from URL. Error: " + error.getMessage()
                                + ". HTTP Status: " + statusCode
                                + ". URL: " + imageUrl);
            }
        });
    }

    public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels;
        int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5); // +0.5 for correct rounding to int.
        return noOfColumns;
    }

    public static Bitmap getBitmap(String imageLocation, int newWidth, int newHeight) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageLocation, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(photoW/newWidth, photoH/newHeight);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(imageLocation, bmOptions);
    }

    public static Bitmap getBitmap(Context context, Uri imageLocation, int newWidth, int newHeight) throws FileNotFoundException {

        InputStream is = context.getContentResolver().openInputStream(imageLocation);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(photoW/newWidth, photoH/newHeight);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        is = context.getContentResolver().openInputStream(imageLocation);
        return BitmapFactory.decodeStream(is, null, bmOptions);
    }

    // Converte um IS para uma string
    public static String inputStream2String(InputStream is, String charset) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                is, charset), 8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
    }

    public static Bitmap base642Bitmap(String myImageData)
    {
        byte[] imageAsBytes = Base64.decode(myImageData.getBytes(),Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

}

