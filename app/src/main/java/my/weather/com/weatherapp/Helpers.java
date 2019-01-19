package my.weather.com.weatherapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Helpers {
    public static String convertStringForDisplay(String string) {
        switch (string) {
            case "Validation Error":
                return "مشکل در اطلاعات ارسالی.";
            default:
                return string;
        }
    }

    public static String getBase64ForBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        }

        return null;
    }

    public static String loadJSONFromAssets(Context context, String fileName) {
        String str = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            str = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return str;
    }
}
