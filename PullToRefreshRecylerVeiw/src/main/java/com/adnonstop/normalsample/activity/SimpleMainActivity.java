package com.adnonstop.normalsample.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.adnonstop.normalsample.R;
import com.adnonstop.normalsample.adapter.SimpleRVAdapter;

import java.util.ArrayList;
import java.util.List;

public class SimpleMainActivity extends AppCompatActivity {

    private static final String TAG = "SimpleMainActivity";
    private static final float DISY_MAX = 60f;
    private RecyclerView mRV;
    private SimpleRVAdapter mSimpleAdapter;
    private LinearLayoutManager mllManager;
    private Handler mHandler = new Handler();
    private long delayedTime = 1000;
    private float density;
    private List<String> mlist;
    private boolean isRefreshEnable;
    private FrameLayout mflRefresh;
    private FrameLayout mflRV;
    private boolean isFirstItem;
    private boolean isRefresh;
    private double predisY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simpleactivity_main);
        initView();
        init();
        initListener();
        density = getResources().getDisplayMetrics().density;
    }

    private void initView() {
        mflRefresh = (FrameLayout) findViewById(R.id.id_fl_refresh);
    }


    private void init() {
        mRV = (RecyclerView) findViewById(R.id.id_rv);
        mllManager = new LinearLayoutManager(this);
        mRV.setLayoutManager(mllManager);
        mRV.setOverScrollMode(View.OVER_SCROLL_NEVER);
        setRVSimpleAdapter();
    }

    private void setRVSimpleAdapter() {
        mSimpleAdapter = new SimpleRVAdapter(getDatas(), this);
        mRV.setAdapter(mSimpleAdapter);
    }


    private void initListener() {

        mRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if ((mllManager.findFirstVisibleItemPosition() == 0)) {
//                    Log.i(TAG, "onScrollStateChanged: >>>>>>>refresh");
                    isFirstItem = true;
                } else {
                    isFirstItem = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                Log.i(TAG, "onScrolled: dy = " + dy);
                if ((mllManager.findFirstVisibleItemPosition() == 0)) {
//                    Log.i(TAG, "onScrollStateChanged: >>>>>>>refresh");
                    isFirstItem = true;
                } else {
                    isFirstItem = false;
                }
            }
        });

        mRV.setOnTouchListener(new View.OnTouchListener() {

            private float v1;
            private int topMargin;
            private float disY;
            private float moveY;
            private float downY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
//            Log.i(TAG, "onTouch: ");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        Log.i(TAG, "onTouch: down = " + event.getY());
                        downY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
//                        Log.i(TAG, "onTouch: move = " + event.getY());
                        predisY = disY;
                        moveY = event.getY();
                        disY = moveY - downY;
//                        Log.i(TAG, "onTouch: disY = " + disY);
                        v1 = density * DISY_MAX;
                        if (disY > v1) {
                            if (predisY > disY) {
                                if (isRefreshEnable) {
                                    isRefreshEnable = true;
                                    setRefreshTopMargin(0);
                                    refreshData();
                                } else {
                                    isRefreshEnable = false;
                                    setRefreshTopMargin((int) (-density * DISY_MAX));
                                }
                            } else {

                                isRefreshEnable = true;
                                float v2 = (disY - 2 * v1) / 2;
                                setRefreshTopMargin((int) v2);
                            }
                        } else {
                            isRefreshEnable = false;
                        }


                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i(TAG, "onTouch: up");
                        if (isRefreshEnable) {
                            setRefreshTopMargin(0);
                            refreshData();
                        } else {
                            setRefreshTopMargin((int) (-density * DISY_MAX));
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });


//        mRV.setOnTouchListener(new View.OnTouchListener() {
//
//            private float disY;
//            private float moveY;
//            private float downY;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
////            Log.i(TAG, "onTouch: ");
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
////                        Log.i(TAG, "onTouch: down = " + event.getY());
//                        downY = event.getY();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
////                        Log.i(TAG, "onTouch: move = " + event.getY());
//                        moveY = event.getY();
//                        disY += moveY - downY;
//                        downY = moveY;
////                        Log.i(TAG, "onTouch: disY = " + disY);
//                        float threshold_show = density * DISY_MAX;
////                        if (disY > threshold_show) {
////                            isRefreshEnable = true;
////                            float v2 = (disY - 2 * threshold_show) / 2;
////                            setRefreshTopMargin((int) v2);
////                        } else {
////                            isRefreshEnable = false;
////                        }
//                        float dy = -threshold_show + disY;
//                        if (dy <= -threshold_show) {
////                            setRefreshTopMargin((int) -threshold_show);
//                        } else {
//                            setRefreshTopMargin((int) dy);
//                        }
//
//                        break;
//                    case MotionEvent.ACTION_UP:
////                        Log.i(TAG, "onTouch: up");
////                        if (isRefreshEnable) {
////                            setRefreshTopMargin(0);
////                            refreshData();
////                        }
//                        break;
//                    default:
//                        break;
//                }
//                return false;
//            }
//        });

//        mRV.setOnTouchListener(new View.OnTouchListener() {
//            private boolean isShow;
//            private float threshold_show = density * getResources().getDimension(R.dimen.threshold_show);
//            private int topMargin;
//            private float disY;
//            private float moveY;
//            private float downY;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        Log.i(TAG + "DY", "onTouch: down = " + event.getRawY());
//                        downY = event.getRawY();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        if (isFirstItem) {
//
//                            predisY = disY;
//
//                            Log.i(TAG + "MY", "onTouch: move = " + event.getRawY());
//                            moveY = event.getRawY();
//
//
//                            disY = moveY - downY;
//
//
//                            Log.i(TAG + "YY", "onTouch: disY = " + disY);
//
//                            if (disY < 0) {
//                                Log.i(TAG, "onTouch: 手指向上滑动");
//                                /**
//                                 * 敏感度为true？
//                                 * yes: 回弹 ，当 topMargin = 本来的，敏感度设置为false；
//                                 * 否则，啥也不做。
//                                 */
//
//
//                            }
//
//                            if (disY > 0) {
////                                Log.i(TAG, "onTouch: 手指向下滑动");
////                                Log.i(TAG, "onTouch: density * getResources().getDimension(R.dimen.threshold_show)" + getResources().getDimension(R.dimen.threshold_show));
//                            /*disY > threshold
//                            * yes:标记 刷新数据为 true
//                            *
//                            * no: 标记 刷新数据为 false
//                            *
//                            * ① 设置敏感度 threshold_show
//                            *
//                            * 当 disY > threshold_show, 逐渐开始露出 progressBar，并设置阻尼,并标记敏感度为true;一旦标记敏感度为true,onTouchEvent返回true；
//                            *
//                            *
//                            * 当 topMargin > thresholdForRefresh,标记刷新数据更多为 true；否则，标记刷新数据为false
//                            *
//                            *
//                            * */
//                                if (disY > getResources().getDimension(R.dimen.threshold_show)) {
//                                    Log.i(TAG, "onTouch: disY = " + disY);
//
//
//                                    isShow = true;
//                                    threshold_show = getResources().getDimension(R.dimen.threshold_show);
//                                    float dy = (disY - threshold_show) / 100;
//
//                                    float realDY = dy;
//
//
//                                    if (disY < predisY) {
//                                        realDY = -dy;
//                                    }
//
//                                    setRefreshTopMargin((int) realDY);
//
//
//                                } else {
//                                    isShow = false;
//                                }
//
//                                if (disY > getResources().getDimension(R.dimen.threshold_refresh)) {
//                                    isRefresh = true;
//                                } else {
//                                    isRefresh = false;
//                                }
//
//                            }
//
//
////                            threshold_show = density * DISY_MAX;
////                            if (disY > threshold_show) {
////                                isRefreshEnable = true;
////                                float v2 = (disY - 2 * threshold_show) / 2;
////                                setRefreshTopMargin((int) v2);
////                            } else {
////                                isRefreshEnable = false;
////                            }
////
////                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mRV.getLayoutParams();
////                            topMargin = layoutParams.topMargin;
////                            Log.i(TAG, "onTouch:layoutParams.topMargin =  " + topMargin);
//                        }
//
//                        break;
//                    case MotionEvent.ACTION_UP:
////                        Log.i(TAG, "onTouch: up");
////                        if (isRefreshEnable) {
////                            setRefreshTopMargin(0);
////                            refreshData();
////                        }
//                        break;
//                    default:
//                        break;
//                }
//                if (isShow) {
//                    Log.i(TAG, "onTouch: isShow = " + isShow);
//                    return true;
//                } else {
//                    Log.i(TAG, "onTouch: isShow = " + isShow);
//                    return false;
//
//                }
//            }
//        });
    }

    private void refreshData() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int topMargin = (int) (density * 60);
                setRefreshTopMargin(-topMargin);
            }
        }, delayedTime);
    }

    private void setRefreshTopMargin(int topMargin) {
        LinearLayout.LayoutParams lllayoutParams = (LinearLayout.LayoutParams) mRV.getLayoutParams();
//        int topMarginTemp = lllayoutParams.topMargin;
        lllayoutParams.topMargin = topMargin;
        mRV.setLayoutParams(lllayoutParams);
    }

    private List<String> getDatas() {
        mlist = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mlist.add("item" + i);
        }
        return mlist;
    }
}
