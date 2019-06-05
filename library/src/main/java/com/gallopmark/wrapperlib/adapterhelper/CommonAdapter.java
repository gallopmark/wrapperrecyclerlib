package com.gallopmark.wrapperlib.adapterhelper;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseRecyclerAdapter {
    protected List<T> mDatas;

    public CommonAdapter(Context context, List<T> mDatas) {
        super(context);
        this.mDatas = mDatas;
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    public void bindData(boolean isRefresh, List<T> datas) {
        if (datas == null) return;
        if (isRefresh) mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void clearData() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public boolean addItem(int position, T t) {
        if (t == null) return false;
        if (position < 0 || position > mDatas.size()) return false;
        if (mDatas.contains(t)) return false;
        mDatas.add(position, t);
        notifyItemInserted(position);
        return true;
    }

    public boolean addItems(int position, List<T> datas) {
        if (datas == null) return false;
        if (mDatas.containsAll(datas)) return false;
        mDatas.addAll(position, datas);
        notifyItemRangeInserted(position, datas.size());
        return true;
    }

    public boolean addItems(List<T> datas) {
        if (datas == null) return false;
        if (mDatas.containsAll(datas)) return false;
        mDatas.addAll(datas);
        int positionStart;
        if (getItemCount() - datas.size() >= 0) {
            positionStart = getItemCount() - datas.size();
        } else {
            positionStart = 0;
        }
        notifyItemRangeInserted(positionStart, datas.size());
        return true;
    }

    public boolean addItem(T t) {
        if (t == null) return false;
        if (mDatas.contains(t)) return false;
        boolean b = mDatas.add(t);
        notifyItemInserted(mDatas.size() - 1);
        return b;
    }

    public boolean updateItem(int position) {
        if (position < 0 || position >= mDatas.size()) return false;
        notifyItemChanged(position);
        return true;
    }

    public boolean updateItem(T t) {
        if (t == null) return false;
        int index = mDatas.indexOf(t);
        if (index >= 0) {
            mDatas.set(index, t);
            notifyItemChanged(index);
            return true;
        }
        return false;
    }

    public boolean updateItem(int position, T t) {
        if (position < 0 || position >= mDatas.size()) return false;
        if (t == null) return false;
        mDatas.set(position, t);
        notifyItemChanged(position);
        return true;
    }

    public boolean removeItem(int position) {
        if (position < 0 || position >= mDatas.size()) return false;
        mDatas.remove(position);
        notifyItemRemoved(position);
        return true;
    }

    public boolean removeItem(T t) {
        if (t == null) return false;
        int index = mDatas.indexOf(t);
        if (index >= 0) {
            mDatas.remove(index);
            notifyItemRemoved(index);
            return true;
        }
        return false;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        onBindHolder(holder, mDatas.get(position), position);
    }

    public abstract void onBindHolder(RecyclerHolder holder, T t, int position);

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }
}
