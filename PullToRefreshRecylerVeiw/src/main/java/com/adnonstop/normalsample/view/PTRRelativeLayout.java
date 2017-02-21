package com.adnonstop.normalsample.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.adnonstop.normalsample.R;

/**
 * Author:　Created by benjamin
 * DATE :  2017/2/18 10:26
 */

public class PTRRelativeLayout extends RelativeLayout {

    private static final String TAG = "MyRelativeLayout";
    private RecyclerView mRV;
    private float downRawY;
    private float moveRawY;
    private float disY;
    private LinearLayoutManager mLLManager;
    private RelativeLayout.LayoutParams mRVLayoutParams;
    private Object dataFromNet;

    public PTRRelativeLayout(Context context) {
        super(context);
        initChild();
    }


    public PTRRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initChild();
    }

    public PTRRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initChild();
    }


    private void initChild() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                mRV = (RecyclerView) getChildAt(1);

                mLLManager = (LinearLayoutManager) mRV.getLayoutManager();

                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mRV != null) {
            RelativeLayout.LayoutParams mRVLayoutParams = (LayoutParams) mRV.getLayoutParams();
            int topMargin = mRVLayoutParams.topMargin;
            Log.i(TAG, "onInterceptTouchEvent: topMargin = " + topMargin);
            /**
             *拦截touch事件
             * recyclerView 的 touch事件 被阻拦
             */
            if (topMargin > 0) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downRawY = event.getRawY();
                Log.i(TAG, "onTouch: downRawY = " + downRawY);

                break;
            case MotionEvent.ACTION_MOVE:
                moveRawY = event.getRawY();
                Log.i(TAG, "onTouch: moveRawY = " + moveRawY);
                disY = moveRawY - downRawY;
                Log.i(TAG, "onTouch: disY = " + disY);

                if (mLLManager.findFirstVisibleItemPosition() == 0) {
                    if (disY < 0) {
                        setTopMargin(0);
                    } else {
                        setTopMargin((int) disY);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                /**
                 * ① 不刷新，弹回去
                 * ② 刷新，回弹到topMargin = progress_layout.height并联网获取数据，得到响应后弹回去
                 */
                if (disY < getResources().getDimension(R.dimen.threshold_refresh)) {
                    setTopMargin(0);
                } else {
                    setTopMargin((int) getResources().getDimension(R.dimen.pb_height_positive));
                    getDataFromNet();
                }

                break;
            default:
                break;
        }

        return true;
    }


    private void setTopMargin(int topMargin) {
        if (mRVLayoutParams == null) {
            mRVLayoutParams = (RelativeLayout.LayoutParams) mRV.getLayoutParams();
        }
        mRVLayoutParams.topMargin = topMargin;
        mRV.setLayoutParams(mRVLayoutParams);
    }

    /**
     * 联网获取数据
     */
    public void getDataFromNet() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                setTopMargin(0);
            }
        }, 1000);
    }

    /**
     * 因为从recyclerView的touch事件到myRelativeLayout的touch事件
     * myRelativelayout不走ACTION_DOWN，所以必须给downRawY赋初始值
     *
     * @param moveRawY recyclerView 的touch事件的当前值：moveRawY
     */
    public void setInitDownRawY(float moveRawY) {
        downRawY = moveRawY;
    }
}
