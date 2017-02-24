package com.adnonstop.ptrrecyclerviewbeta2.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


import com.adnonstop.ptrrecyclerviewbeta2.R;
import com.adnonstop.ptrrecyclerviewbeta2.adapter.PTRAdapter;
import com.adnonstop.ptrrecyclerviewbeta2.callback.DataCallback;
import com.adnonstop.ptrrecyclerviewbeta2.callback.ItemClickListener;
import com.adnonstop.ptrrecyclerviewbeta2.callback.LoadMoreListener;
import com.adnonstop.ptrrecyclerviewbeta2.decoration.EmptyItemDecoration;
import com.adnonstop.ptrrecyclerviewbeta2.util.L;
import com.adnonstop.ptrrecyclerviewbeta2.util.SingleToast;
import com.adnonstop.ptrrecyclerviewbeta2.view.PTRRelativeLayout;

import java.util.ArrayList;


/**
 * @author gzq
 */
public class PTRActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PTRActivity";
    private RecyclerView mRecyclerview;
    private static int currentPage = 1;
    private static int PAGE_COUNT = 10;//要保证在分页加载下，首页数据应该至少比铺满全屏时要求的数据多一条
    private static int TOTAL_COUNT = 30;
    private View netOffView;//断网显示的view
    private View emptyView;
    private float mDownRawY;
    private PTRAdapter mptrAdapter;
    Handler mHandler = new Handler(Looper.getMainLooper());
    private ArrayList<String> newDatas;
    private PTRRelativeLayout ptrRl;
    private boolean isRefresh = false;//正在刷新？

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr_layout);

        initView();
        initAdapter();
        initData(true);
    }


    private void initView() {
        mRecyclerview = (RecyclerView) findViewById(R.id.id_rv);
        ptrRl = (PTRRelativeLayout) findViewById(R.id.id_ptrrl);
    }

    /**
     * @param isFirstRefersh 第一刷新数据？是，不弹吐司；否，弹吐司“刷新成功”
     * @desc 第一次请求数据和刷新数据都走这个方法
     */
    public void initData(final boolean isFirstRefersh) {

        if (ptrRl == null) {
            L.i(TAG, "ptrRelativeLayout == null");
            return;
        }

        if (isRefresh) {//避免同时多次刷新数据
            return;
        }

        if (isFirstRefersh) {
            ptrRl.setRefreshState(true);
        }

        /**
         * 回滚到初始状态
         */
        currentPage = 1;
        mptrAdapter.setInitState();

        getDataFromNet(1, new DataCallback<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> newdatas) {

                ptrRl.setRefreshState(false);//完成刷新

                isRefresh = false;//完成刷新

                //如果没有数据则显示空布局
                if (newdatas.size() == 0) {
                    emptyView = LayoutInflater.from(PTRActivity.this).inflate(R.layout.layout_empty_recordmission, null, false);
                    mptrAdapter.setEmptyView(ptrRl, emptyView);
                    Button btnFindMission = (Button) emptyView.findViewById(R.id.id_btn_empty_data);
                    btnFindMission.setOnClickListener(PTRActivity.this);
                } else {
                    if (!isFirstRefersh)
                        SingleToast.singleToast(PTRActivity.this, "刷新成功");
                }

                if (newdatas.size() == TOTAL_COUNT) {
                    mptrAdapter.setNoMoreDataState();
                }

                mptrAdapter.setNewDatas(newdatas);
            }

            @Override
            public void onFail(String msg) {
                //在已经有数据的情况下，刷新数据，突然断网，需要先清空数据
                mptrAdapter.clearData();
                ptrRl.setRefreshState(false);//完成刷新
                isRefresh = false;//完成刷新

                SingleToast.singleToast(PTRActivity.this, msg);


                netOffView = LayoutInflater.from(PTRActivity.this).inflate(R.layout.layout_net_off, null, false);
                mptrAdapter.setNetOffView(ptrRl, netOffView);
                Button btnNetOff = (Button) netOffView.findViewById(R.id.id_btn_net_off);
                btnNetOff.setOnClickListener(PTRActivity.this);
            }
        }, true);
    }

    /**
     * @desc 高度解耦。先搭建空台子，然后再演戏（加载数据）。
     */
    private void initAdapter() {
        ArrayList<String> initDatas = new ArrayList<>();//为了方便后续操作，要求构造方法的参数不能为null
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerview.addItemDecoration(new EmptyItemDecoration());
        mptrAdapter = new PTRAdapter(this, initDatas, mRecyclerview, ptrRl);
        mRecyclerview.setAdapter(mptrAdapter);

        //footerView添加loadMoreView
        mptrAdapter.setLoadingView(R.layout.view_loadmore);

        //设置加载更多的监听
        mptrAdapter.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMore();
            }
        });

        //设置item监听
        mptrAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(int position, View view) {
                SingleToast.singleToast(PTRActivity.this, "position = " + position);
            }
        });
    }

    /**
     * @desc 加载更多
     */
    private void loadMore() {

        getDataFromNet(++currentPage, new DataCallback<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> newdatas) {


                //最最关键的是显示没有更多数据的时机，这里已获取数据和总数据的比较来把握没有数据的时机！
                if ((newdatas.size()) == TOTAL_COUNT) {
                    mptrAdapter.setNoMoreDataState();
                }

                mptrAdapter.setNewDatas(newdatas);
                mptrAdapter.setLoadingState(false);//加载完成

            }

            @Override
            public void onFail(String msg) {
                mptrAdapter.setLoadingState(false);//加载完成
                mptrAdapter.setLoadFailedView(R.layout.view_failed_network, false);
            }
        }, false);
    }


    @Override
    public void onClick(View v) {
        //断网的监听
        if (v.getId() == R.id.id_btn_net_off) {
            ptrRl.removeView(netOffView);
            initData(false);
        }
        //请求数据为空的监听
        if (v.getId() == R.id.id_btn_empty_data) {
            //去做任务
            SingleToast.singleToast(this, "攒任务");
        }
//        在加载更多数据的过程中，切换footerView至网络状态不佳布局，点击事件在recyclerViewAdapter里做了
//        if (v.getId() == R.id.id_ll_container_failed_network) {
//        }
    }

    /**
     * @param pageNum
     * @param callback
     * @desc 模拟联网加载数据
     */
    private void getDataFromNet(int pageNum, final DataCallback<ArrayList<String>> callback, boolean isFromRefresh) {
        if (isFromRefresh) {
            isRefresh = true;//标记正在刷新数据
        }
        if (newDatas == null) {
            newDatas = new ArrayList<>();
        }
        if (pageNum == 1) {
            newDatas.clear();
        }
        for (int i = 0; i < PAGE_COUNT; i++) {
            newDatas.add("--item--" + i + "--pageNum = " + pageNum);
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                callback.onSuccess(newDatas);

//                callback.onFail("net off");
//
//                newDatas.clear();
//                callback.onSuccess(newDatas);
            }
        }, 1000);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownRawY = ev.getRawY();
                L.i(TAG, "mDownRawY = " + mDownRawY);
                if (mptrAdapter != null) {
                    mptrAdapter.setDowmRawY(mDownRawY);
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
