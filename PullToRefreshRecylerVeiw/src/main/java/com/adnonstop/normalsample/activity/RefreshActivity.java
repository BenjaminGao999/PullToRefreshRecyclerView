package com.adnonstop.normalsample.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.adnonstop.normalsample.R;
import com.adnonstop.normalsample.adapter.SimpleRVAdapter;
import com.adnonstop.normalsample.view.MyRelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class RefreshActivity extends AppCompatActivity {


    private static final String TAG = "RefreshActivity";
    private RecyclerView mRV;
    private SimpleRVAdapter mAdapter;
    private LinearLayoutManager mLLManager;
    private float downRawY;
    private float moveRawY;
    private float disY;
    private RelativeLayout.LayoutParams mRVLayoutParams;
    private String newStateDesc;
    private int mNewState;
    private MyRelativeLayout mMyRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);

        initView();
        initListener();
    }


    private void initView() {
        mMyRL = (MyRelativeLayout) findViewById(R.id.id_container_myrl);
        mRV = (RecyclerView) findViewById(R.id.id_rv_refresh);
        mLLManager = new LinearLayoutManager(this);
        mRV.setLayoutManager(mLLManager);
        mRV.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mAdapter = new SimpleRVAdapter(getDatas(), this);
        mRV.setAdapter(mAdapter);
    }

    private void initListener() {

        mRV.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downRawY = event.getRawY();
                        Log.i(TAG, "onTouch: downRawY = " + downRawY);

                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveRawY = event.getRawY();
//                        Log.i(TAG, "onTouch: moveRawY = " + moveRawY);
                        disY = moveRawY - downRawY;
                        Log.i(TAG, "onTouch: disY = " + disY);

                        if (mLLManager.findFirstVisibleItemPosition() == 0 && mNewState == RecyclerView.SCROLL_STATE_DRAGGING && disY > getResources().getDimension(R.dimen.threshold_show_enable)) {
                            Log.i(TAG, "onTouch: start show progressbar");
                            setTopMargin(1);
                            mMyRL.setInitDownRawY(moveRawY);
                        }

                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                    default:
                        break;
                }

                return false;
            }
        });


        mRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mNewState = newState;
            }

        });

    }

    private void setTopMargin(int topMargin) {
        if (mRVLayoutParams == null) {
            mRVLayoutParams = (RelativeLayout.LayoutParams) mRV.getLayoutParams();
        }
        mRVLayoutParams.topMargin = topMargin;
        mRV.setLayoutParams(mRVLayoutParams);
    }

    private List<String> getDatas() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("item" + i);
        }
        return list;
    }
}
