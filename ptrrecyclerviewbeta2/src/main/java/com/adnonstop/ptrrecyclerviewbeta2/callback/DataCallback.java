package com.adnonstop.ptrrecyclerviewbeta2.callback;

import java.util.ArrayList;

/**
 * Author:　Created by benjamin
 * DATE :  2017/2/20 11:17
 */

public interface DataCallback<T> {
    void onSuccess(T newdatas);

    void onFail(String msg);
}
