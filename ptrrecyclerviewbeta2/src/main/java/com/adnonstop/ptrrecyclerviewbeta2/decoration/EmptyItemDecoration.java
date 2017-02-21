package com.adnonstop.ptrrecyclerviewbeta2.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.adnonstop.ptrrecyclerviewbeta2.R;

/**
 * Author:　Created by benjamin
 * DATE :  2017/2/20 12:37
 */

public class EmptyItemDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = (int) parent.getResources().getDimension(R.dimen.empty_item_deco);
    }
}
