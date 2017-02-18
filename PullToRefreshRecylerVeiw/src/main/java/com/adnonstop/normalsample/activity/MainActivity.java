package com.adnonstop.normalsample.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.adnonstop.normalsample.R;
import com.adnonstop.normalsample.adapter.RVAdapter;
import com.adnonstop.normalsample.adapter.SimpleRVAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRV;
    private RVAdapter mAdapter;
    private SimpleRVAdapter mSimpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initListener();
    }


    private void init() {
        mRV = (RecyclerView) findViewById(R.id.id_rv);
        mRV.setLayoutManager(new LinearLayoutManager(this));
        setRVAdapterWithHeader();
    }


    private void setRVAdapterWithHeader() {
        mAdapter = new RVAdapter(getDatas(), this);
        mRV.setAdapter(mAdapter);
    }

    private void initListener() {

    }

    private List<String> getDatas() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("item" + i);
        }
        return list;
    }
}
