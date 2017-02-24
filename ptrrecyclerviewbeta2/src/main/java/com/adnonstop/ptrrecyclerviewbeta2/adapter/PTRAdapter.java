package com.adnonstop.ptrrecyclerviewbeta2.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adnonstop.ptrrecyclerviewbeta2.R;
import com.adnonstop.ptrrecyclerviewbeta2.callback.ItemClickListener;
import com.adnonstop.ptrrecyclerviewbeta2.callback.LoadMoreListener;
import com.adnonstop.ptrrecyclerviewbeta2.util.L;
import com.adnonstop.ptrrecyclerviewbeta2.util.ViewInflaterUtil;
import com.adnonstop.ptrrecyclerviewbeta2.view.PTRRelativeLayout;
import com.adnonstop.ptrrecyclerviewbeta2.viewholder.CommonViewHolder;

import java.util.ArrayList;

/**
 * @author gzq
 * @description
 */

public class PTRAdapter extends RecyclerView.Adapter {
    private static final String TAG = "PTRAdapter";
    private final RecyclerView mRecyclerView;
    private final AppCompatActivity mContext;
    private final PTRRelativeLayout mptrRelativeLayout;
    private LoadMoreListener mLoadMoreListener;
    private boolean isLoading = false;//正在加载？
    private boolean isLoadMoreEnable = true;//没有更多数据的标识
    private int lastVisibleItemPosition;
    private int totalItemCount;

    private View mLoadingView; //分页加载中view
    private View mLoadFailedView; //分页加载失败view
    private View mLoadEndView; //分页加载结束view
    private RelativeLayout mFooterLayout;//footer view
    private ArrayList<String> mDatas = new ArrayList<>();//真实的数据
    private ItemClickListener mOnItemClickListener;
    private int firstVisibleItemPosition;
    private boolean isAutoLoadMore = false;//自动加载更多
    private float mDownRawY;
    private float moveRawY;
    private float disY;
    private LinearLayoutManager mLLManager;
    private double mNewState;
    private RelativeLayout.LayoutParams mRVLayoutParams;
    private final int VIEW_FOOTER = 101;//itemType  =  footerView
    private final int VIEW_ITEM = 102;//itemType = itemView
    private AppCompatActivity mActivity;//该适配器绑定的Activity

    public PTRAdapter(AppCompatActivity context, ArrayList<String> datas, RecyclerView recyclerView, PTRRelativeLayout ptrRelativeLayout) {
        mContext = context;
        if (datas == null || recyclerView == null) {
            throw new RuntimeException("构造参数不能为null");
        }
        mptrRelativeLayout = ptrRelativeLayout;
        mDatas = datas;
        mRecyclerView = recyclerView;
        ptrRlbindActivity();
        handlePTR();
    }

    /**
     * 为PTRRelativeLayout绑定Activity
     */
    private void ptrRlbindActivity() {
        L.i(TAG, "ptrRlbindActivity()");
        mptrRelativeLayout.setActivity(mContext);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_FOOTER) {

            if (mFooterLayout == null) {
                mFooterLayout = new RelativeLayout(mContext);
            }
            return CommonViewHolder.create(mFooterLayout);

        } else {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);
            return CommonViewHolder.create(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int itemViewType = holder.getItemViewType();
        if (itemViewType != VIEW_FOOTER) {
            final CommonViewHolder itemViewHolder = (CommonViewHolder) holder;
            itemViewHolder.setText(R.id.id_tv_item, mDatas.get(position));
            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position, itemViewHolder.itemView);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size() == 0 ? 0 : mDatas.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mDatas.size()) {
            return VIEW_FOOTER;
        } else {
            return VIEW_ITEM;
        }
    }


    //real data
    public void setNewDatas(ArrayList<String> records) {
        mDatas = records;
        notifyDataSetChanged();
        notFullScreen();//第一调用时，在onLoadMore之前，而onLoadMore调用loadMore(),loadMore填充了footerView，
        // 所以要在每一次刷新数据时重新判断
    }


    //为recyclerView添加加载更多的监听
    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    //为item设置监听
    public void setOnItemClickListener(ItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    //处理加载更多
    private void handlePTR() {

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mNewState = newState;
                if (mLLManager == null) {
                    mLLManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                }

                L.i(TAG, "linearLayoutManager.findLastVisibleItemPosition() = "
                        + (mLLManager.findLastVisibleItemPosition())
                        + "; getItemCount() - 1 = " + (getItemCount() - 1));
                /**
                 * 加载更多的真正时机
                 */
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    L.i(TAG, "newState == RecyclerView.SCROLL_STATE_IDLE");
                    if (!isLoading && mLLManager.findLastVisibleItemPosition() == getItemCount() - 1) {
                        //此时是刷新状态
                        if (mLoadMoreListener != null && isLoadMoreEnable) {//isLoadMoreEnable 为了不重复加载数据
                            mLoadMoreListener.onLoadMore();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mLLManager == null) {
                    mLLManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                }
                totalItemCount = mLLManager.getItemCount();
                lastVisibleItemPosition = mLLManager.findLastVisibleItemPosition();
                firstVisibleItemPosition = mLLManager.findFirstVisibleItemPosition();

                L.i(TAG, "totalItemCount =" + totalItemCount +
                        "-----" + "lastVisibleItemPosition =" + lastVisibleItemPosition +
                        "-----" + "firstVisibleItemPosition = " + firstVisibleItemPosition);

                //防止数据占不满全屏的情况,隐藏footerView
                notFullScreen();


                //加载更多的操作应该在onScrollStateChanged里做的；这里做是为了实现自动加载，填满列表；只会在第一次自动加载时使用。
                if (isAutoLoadMore && mLLManager.findLastVisibleItemPosition() == getItemCount() - 1) {
                    //此时是刷新状态
                    if (mLoadMoreListener != null && isLoadMoreEnable) {//isLoadMoreEnable 为了不重复加载数据
                        mLoadMoreListener.onLoadMore();
                    }
                } else if (isAutoLoadMore) {
                    isAutoLoadMore = false;
                }
            }
        });

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDownRawY = event.getRawY();
                        L.i(TAG, "onClick: mDownRawY = " + mDownRawY);

                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveRawY = event.getRawY();
//                        Log.i(TAG, "onClick: moveRawY = " + moveRawY);
                        Log.i(TAG, "onTouch: mDownRawY = " + mDownRawY);
                        disY = moveRawY - mptrRelativeLayout.getmDownRawY();
                        L.i(TAG, "onClick: disY = " + disY);

                        if (mLLManager == null) {
                            mLLManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                        }
                        if (mLLManager.findFirstVisibleItemPosition() == 0 && mNewState == RecyclerView.SCROLL_STATE_DRAGGING && disY > mContext.getResources().getDimension(R.dimen.threshold_show_enable)) {
                            L.i(TAG, "onClick: start show progressbar");
                            setTopMargin(1);
                            mptrRelativeLayout.setInitDownRawY(moveRawY);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        L.i(TAG, "RecyclerView.setOnTouchListener UP ");
                        setTopMargin(0);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

    }

    private void setTopMargin(int topMargin) {
        if (mRVLayoutParams == null) {
            mRVLayoutParams = (RelativeLayout.LayoutParams) mRecyclerView.getLayoutParams();
        }
        mRVLayoutParams.topMargin = topMargin;
        mRecyclerView.setLayoutParams(mRVLayoutParams);
    }

    /**
     * 判断是否隐藏footerView
     */
    public void notFullScreen() {
        if (lastVisibleItemPosition - firstVisibleItemPosition == totalItemCount - 1 && mFooterLayout != null) {
            mFooterLayout.removeAllViews();
        }
    }

    /**
     * @param loadingState true:正在加载
     */
    public void setLoadingState(boolean loadingState) {
        isLoading = loadingState;
    }

    public void setLoadMoreEnable(boolean b) {
        isLoadMoreEnable = b;
    }

    /**
     * 清空footer view
     */
    private void removeFooterView() {
        mFooterLayout.removeAllViews();
    }

    /**
     * @param footerView 为FooterView更换UI
     */
    private void addFooterView(View footerView) {
        if (footerView == null) {
            return;
        }

        if (mFooterLayout == null) {
            mFooterLayout = new RelativeLayout(mContext);
        }
        removeFooterView();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mContext.getResources().getDimension(R.dimen.footerview));
        mFooterLayout.addView(footerView, params);
    }

    /**
     * @param loadingView 当加载更多时，展示给用于的UI
     */
    public void setLoadingView(View loadingView) {
        mLoadingView = loadingView;
        addFooterView(mLoadingView);
    }

    public void setLoadingView(int loadingId) {
        setLoadingView(ViewInflaterUtil.inflate(mContext, loadingId));
    }

    /**
     * @param loadEndView 当没有更多数据时，展示给用户的UI
     */
    public void setLoadNoMoreView(View loadEndView) {
        mLoadEndView = loadEndView;
        addFooterView(mLoadEndView);
    }

    public void setLoadNoMoreView(int loadEndId) {
        setLoadNoMoreView(ViewInflaterUtil.inflate(mContext, loadEndId));
    }

    /**
     * @param loadFailedView 当加载更多过程中，因为网络不佳，导致联网走了onFail方法，在footView中展示给用户的提示UI
     * @param isReloadEnable true： 点击footerView ，重新联网获取数据； false:仅仅起展示作用。
     */
    public void setLoadFailedView(View loadFailedView, boolean isReloadEnable) {
        if (loadFailedView == null) {
            return;
        }
        mLoadFailedView = loadFailedView;
        addFooterView(mLoadFailedView);
        mLoadFailedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFooterView(mLoadingView);
                if (mLoadMoreListener != null) {
                    mLoadMoreListener.onLoadMore();
                    setLoadingState(true);
                }
            }
        });
    }

    public void setLoadFailedView(int loadFailedId, boolean isReloadEnable) {
        setLoadFailedView(ViewInflaterUtil.inflate(mContext, loadFailedId), isReloadEnable);
    }

    public void clearData() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    /**
     * @param downRawY
     * @deprecated 获取touch down 事件 的监听，转由 ptrRelativeLayout内部实现
     */
    public void setDowmRawY(float downRawY) {
        mDownRawY = downRawY;
    }

    /**
     * @param ptrRl
     * @param emptyView 数据为空展示的view
     */
    public void setEmptyView(PTRRelativeLayout ptrRl, View emptyView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ptrRl.addView(emptyView, layoutParams);
    }

    /**
     * @param ptrRl
     * @param netOffView 断网view
     */
    public void setNetOffView(PTRRelativeLayout ptrRl, View netOffView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ptrRl.addView(netOffView, layoutParams);
    }

    /**
     * @param activity 该适配器绑定的Activity
     */
    public void setmActivity(AppCompatActivity activity) {
        mActivity = activity;
    }

    /**
     * 回滚Adapter到初始状态
     */
    public void setInitState() {
        setLoadMoreEnable(true);
        setLoadingView(R.layout.view_loadmore);
    }

    /**
     * 设置没有更多数据了
     */
    public void setNoMoreDataState() {
        setLoadMoreEnable(false);
        setLoadNoMoreView(R.layout.view_nomoredata);
    }
}
