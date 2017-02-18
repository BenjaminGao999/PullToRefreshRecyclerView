package com.adnonstop.normalsample.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class RVAdapter extends RecyclerView.Adapter {
    private static final int HEADER = 1;
    private static final int NORMAL = 2;
    private List<String> mData;
    private Context mContext;
    private LinearLayoutManager mLayoutManager;

    public RVAdapter(List<String> mData, Context mContext) {
        super();
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLayoutManager.findFirstVisibleItemPosition();
            }
        });
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NORMAL) {
            return new NormalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_rv, parent, false));
        } else {
            return new HeaderViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_refresh_header, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalViewHolder) {
            NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
            int index = position - 1;
            normalViewHolder.tvItem.setText(mData.get(index));
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return HEADER;
        } else {
            return NORMAL;
        }
    }

    static class NormalViewHolder extends RecyclerView.ViewHolder {


        public TextView tvItem;

        public NormalViewHolder(View itemView) {
            super(itemView);
            tvItem = (TextView) itemView.findViewById(R.id.id_tv_itemrv);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

}
