package com.adnonstop.normalsample.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adnonstop.normalsample.R;

import java.util.List;

/**
 * Author:　Created by benjamin
 * DATE :  2017/2/17 9:39
 */

public class SimpleRVAdapter extends RecyclerView.Adapter {
    private static final String TAG = "SimpleRVAdapter";
    private List<String> mData;
    private Context mContext;


    public SimpleRVAdapter(List<String> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_rv, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalViewHolder) {
            NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            normalViewHolder.tvItem.setText(mData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    static class NormalViewHolder extends RecyclerView.ViewHolder {


        public TextView tvItem;

        public NormalViewHolder(View itemView) {
            super(itemView);
            tvItem = (TextView) itemView.findViewById(R.id.id_tv_itemrv);
        }
    }

}
