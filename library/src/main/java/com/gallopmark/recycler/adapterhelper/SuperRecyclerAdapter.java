package com.gallopmark.recycler.adapterhelper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.List;

public abstract class SuperRecyclerAdapter<T> extends InnerAdapter<T> {

    protected SuperRecyclerAdapter(Context context, List<T> mDatas) {
        super(context, mDatas);
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerHolder(bindItemView(viewType, parent));
    }

    @NonNull
    protected abstract View bindItemView(int viewType, ViewGroup parent);
}
