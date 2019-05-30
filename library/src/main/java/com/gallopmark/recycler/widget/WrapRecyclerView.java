package com.gallopmark.recycler.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/*实现带header和footer功能的RecyclerView*/
public class WrapRecyclerView extends RecyclerView {
    // 包裹了一层的头部底部Adapter
    private WrapRecyclerAdapter mWrapRecyclerAdapter;
    // 这个是列表数据的Adapter
    private Adapter mAdapter;

    public WrapRecyclerView(Context context) {
        super(context);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        // 为了防止多次设置Adapter
        if (mAdapter != null) {
            mAdapter.unregisterAdapterDataObserver(mDataObserver);
            mAdapter = null;
        }
        this.mAdapter = adapter;
        if (adapter instanceof WrapRecyclerAdapter) {
            mWrapRecyclerAdapter = (WrapRecyclerAdapter) adapter;
        } else {
            mWrapRecyclerAdapter = new WrapRecyclerAdapter(adapter);
        }
        super.setAdapter(mWrapRecyclerAdapter);
        // 注册一个观察者
        mAdapter.registerAdapterDataObserver(mDataObserver);
        // 解决GridLayout添加头部和底部也要占据一行
        mWrapRecyclerAdapter.adjustSpanSize(this);
    }

    // 添加头部
    public void addHeaderView(View view) {
        // 如果没有Adapter那么就不添加，也可以选择抛异常提示
        // 让他必须先设置Adapter然后才能添加，这里是仿照ListView的处理方式
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.addHeaderView(view);
        }
    }

    // 添加底部
    public void addFooterView(View view) {
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.addFooterView(view);
        }
    }

    // 移除头部
    public void removeHeaderView(View view) {
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.removeHeaderView(view);
        }
    }

    // 移除底部
    public void removeFooterView(View view) {
        if (mWrapRecyclerAdapter != null) {
            mWrapRecyclerAdapter.removeFooterView(view);
        }
    }

    private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyDataSetChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyDataSetChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemRemoved(positionStart);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyItemMoved没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyItemChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemChanged(positionStart);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyItemChanged没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemChanged(positionStart, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (mAdapter == null) return;
            // 观察者  列表Adapter更新 包裹的也需要更新不然列表的notifyItemInserted没效果
            if (mWrapRecyclerAdapter != mAdapter)
                mWrapRecyclerAdapter.notifyItemInserted(positionStart);
        }
    };

    public static class WrapRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        /**
         * SparseArrays map integers to Objects.  Unlike a normal array of Objects,
         * there can be gaps in the indices.  It is intended to be more memory efficient
         * than using a HashMap to map Integers to Objects, both because it avoids
         * auto-boxing keys and its data structure doesn't rely on an extra entry object
         * for each mapping.
         * <p>
         * SparseArray是一个<int , Object>的HashMap  比HashMap更高效
         */
        private SparseArray<View> mHeaderViews;
        private SparseArray<View> mFooterViews;

        // 基本的头部类型开始位置  用于viewType
        private static int BASE_ITEM_TYPE_HEADER = 10000000;
        // 基本的底部类型开始位置  用于viewType
        private static int BASE_ITEM_TYPE_FOOTER = 20000000;

        /**
         * 数据列表的Adapter
         */
        private RecyclerView.Adapter mAdapter;

        WrapRecyclerAdapter(RecyclerView.Adapter adapter) {
            this.mAdapter = adapter;
            mHeaderViews = new SparseArray<>();
            mFooterViews = new SparseArray<>();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // viewType 可能就是 SparseArray 的key
            if (isHeaderViewType(viewType)) {
                View headerView = mHeaderViews.get(viewType);
                return createHeaderFooterViewHolder(headerView);
            }
            if (isFooterViewType(viewType)) {
                View footerView = mFooterViews.get(viewType);
                return createHeaderFooterViewHolder(footerView);
            }
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        /**
         * 是不是底部类型
         */
        private boolean isFooterViewType(int viewType) {
            int position = mFooterViews.indexOfKey(viewType);
            return position >= 0;
        }

        /**
         * 创建头部或者底部的ViewHolder
         */
        private RecyclerView.ViewHolder createHeaderFooterViewHolder(View view) {
            return new RecyclerView.ViewHolder(view) {
            };
        }

        /**
         * 是不是头部类型
         */
        private boolean isHeaderViewType(int viewType) {
            int position = mHeaderViews.indexOfKey(viewType);
            return position >= 0;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (isHeaderPosition(position) || isFooterPosition(position)) {
                return;
            }
            // 计算一下位置
            final int adapterPosition = position - mHeaderViews.size();
            mAdapter.onBindViewHolder(holder, adapterPosition);
        }

        @Override
        public int getItemViewType(int position) {
            if (isHeaderPosition(position)) {
                // 直接返回position位置的key
                return mHeaderViews.keyAt(position);
            }
            if (isFooterPosition(position)) {
                // 直接返回position位置的key
                position = position - mHeaderViews.size() - mAdapter.getItemCount();
                return mFooterViews.keyAt(position);
            }
            // 返回列表Adapter的getItemViewType
            position = position - mHeaderViews.size();
            return mAdapter.getItemViewType(position);
        }

        /**
         * 是不是底部位置
         */
        private boolean isFooterPosition(int position) {
            return position >= (mHeaderViews.size() + mAdapter.getItemCount());
        }

        /**
         * 是不是头部位置
         */
        private boolean isHeaderPosition(int position) {
            return position < mHeaderViews.size();
        }

        @Override
        public int getItemCount() {
            // 条数三者相加 = 底部条数 + 头部条数 + Adapter的条数
            return mAdapter.getItemCount() + mHeaderViews.size() + mFooterViews.size();
        }

        // 添加头部
        void addHeaderView(View view) {
            int position = mHeaderViews.indexOfValue(view);
            if (position < 0) {
                mHeaderViews.put(BASE_ITEM_TYPE_HEADER++, view);
            }
            notifyDataSetChanged();
        }

        // 添加底部
        void addFooterView(View view) {
            int position = mFooterViews.indexOfValue(view);
            if (position < 0) {
                mFooterViews.put(BASE_ITEM_TYPE_FOOTER++, view);
            }
            notifyDataSetChanged();
        }

        // 移除头部
        void removeHeaderView(View view) {
            int index = mHeaderViews.indexOfValue(view);
            if (index < 0) return;
            mHeaderViews.removeAt(index);
            notifyDataSetChanged();
        }

        // 移除底部
        void removeFooterView(View view) {
            int index = mFooterViews.indexOfValue(view);
            if (index < 0) return;
            mFooterViews.removeAt(index);
            notifyDataSetChanged();
        }

        /**
         * 解决GridLayoutManager添加头部和底部不占用一行的问题
         */
        void adjustSpanSize(RecyclerView recycler) {
            if (recycler.getLayoutManager() instanceof GridLayoutManager) {
                final GridLayoutManager layoutManager = (GridLayoutManager) recycler.getLayoutManager();
                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        boolean isHeaderOrFooter = isHeaderPosition(position) || isFooterPosition(position);
                        return isHeaderOrFooter ? layoutManager.getSpanCount() : 1;
                    }
                });
            }
        }
    }
}
