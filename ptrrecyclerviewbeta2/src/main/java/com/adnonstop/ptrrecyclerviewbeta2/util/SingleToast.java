package com.adnonstop.ptrrecyclerviewbeta2.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by admin on 2016/12/20.
 *       单例的吐司
 */

public class SingleToast {

    private static Toast toast;
    public static void singleToast(Context context, String content){
        if (toast == null) {
            toast = Toast.makeText(context,content, Toast.LENGTH_SHORT);
        }else {
            toast.setText(content);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
