package com.adnonstop.ptrrecyclerviewbeta2.view;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adnonstop.ptrrecyclerviewbeta2.R;
import com.adnonstop.ptrrecyclerviewbeta2.activity.PTRActivity;
import com.adnonstop.ptrrecyclerviewbeta2.util.L;

/**
 * Author:　Created by benjamin
 * DATE :  2017/2/18 15:10
 */

public class PTRRelativeLayout extends RelativeLayout {
    private static final String TAG = "PTRRelativeLayout";
    private RecyclerView mRV;
    private float downRawY;
    private float moveRawY;
    private float disY;
    private LinearLayoutManager mLLManager;
    private LayoutParams mRVLayoutParams;
    private AppCompatActivity mActivity;

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
            LayoutParams mRVLayoutParams = (LayoutParams) mRV.getLayoutParams();
            int topMargin = mRVLayoutParams.topMargin;
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
                L.i(TAG, "onClick: downRawY = " + downRawY);

                break;
            case MotionEvent.ACTION_MOVE:
                moveRawY = event.getRawY();
                L.i(TAG, "onClick: moveRawY = " + moveRawY);
                disY = moveRawY - downRawY;
                L.i(TAG, "onClick: disY = " + disY);

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
                if (disY < getResources().getDimension(R.dimen.prt_threshold_refresh)) {
                    setTopMargin(0);
                } else {
                    setTopMargin((int) getResources().getDimension(R.dimen.ptr_pb_height));
                    getDataFromNet();
                }
                L.i(TAG, "com.adnonstop.missionhall.views.PTRRelativeLayout UP  mRVLayoutParams.topMargin = " + mRVLayoutParams.topMargin);

                break;
            default:
                break;
        }

        return true;
    }


    public void setTopMargin(int topMargin) {
        if (mRV == null)
            return;
        if (mRVLayoutParams == null) {
            mRVLayoutParams = (LayoutParams) mRV.getLayoutParams();
        }
        mRVLayoutParams.topMargin = topMargin;
        mRV.setLayoutParams(mRVLayoutParams);
    }

    /**
     * 联网获取数据
     */
    public void getDataFromNet() {
        if (mActivity != null) {
            if (mActivity instanceof PTRActivity) {
                PTRActivity mActivity = (PTRActivity) this.mActivity;
                mActivity.initData(false);
            }
            /**
             * 根据需要可以添加更多的if
             */
        }

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

    public void setActivity(AppCompatActivity activity) {
        mActivity = activity;
    }


}
