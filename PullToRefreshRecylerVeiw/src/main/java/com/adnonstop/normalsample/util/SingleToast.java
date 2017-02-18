package com.adnonstop.normalsample.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Author:　Created by benjamin
 * DATE :  2017/2/18 21:30
 */

public class SingleToast {
    private static Toast toast;

    public static void singleToast(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.show();
    }

}
