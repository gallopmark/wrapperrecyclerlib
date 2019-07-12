package com.gallopMark.adapterlib;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.gallopmark.recycler.adapterhelper.CommonAdapter;

import java.util.List;

/**
 * Created by gallop on 2019/7/12.
 * Copyright holike possess 2019.
 */
public class MyAdapter extends CommonAdapter<String> {
    protected MyAdapter(Context context, List<String> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int bindView(int viewType) {
        return 0;
    }

    @Override
    public void onBindHolder(RecyclerHolder holder, String s, int position) {

    }
}
