package com.gallopmark.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

/*自定义pagerAdapter实现多item*/
public abstract class CommonPagerAdapter<T> extends PagerAdapter {
    private View convertView;
    protected Context mContext;
    protected List<T> mDatas;
    private OnItemClickListener<T> onItemClickListener;

    public CommonPagerAdapter(Context mContext, List<T> mData) {
        this.mContext = mContext;
        this.mDatas = mData;
    }

    public void addItem(T t) {
        mDatas.add(t);
        notifyDataSetChanged();
    }

    public void addItems(List<T> items) {
        mDatas.addAll(items);
        notifyDataSetChanged();
    }

    public void removeItem(T t) {
        mDatas.remove(t);
        notifyDataSetChanged();
    }

    public void removeAll(List<T> items) {
        mDatas.removeAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        int itemType = getItemViewType(position);
        this.convertView = LayoutInflater.from(mContext).inflate(bindView(itemType), container, false);
        convertView.setClickable(true);
        // 处理视图和数据
        convert(convertView, mDatas.get(position), position);
        // 处理条目的触摸事件
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(mDatas.get(position), position);
                }
            }
        });
        container.addView(convertView);
        return convertView;
    }

    protected abstract void convert(@NonNull View convertView, @NonNull T item, int realPosition);

    protected int getItemViewType(int position) {
        return 0;
    }

    public abstract int bindView(int viewType);

    public <V extends View> V obtainView(int id) {
        return convertView.findViewById(id);
    }

    /**
     * 文本控件赋值
     */
    public void setText(@IdRes int id, CharSequence text) {
        ((TextView) obtainView(id)).setText(text);
    }

    public void setTypeface(@IdRes int id, Typeface tf) {
        ((TextView) obtainView(id)).setTypeface(tf);
    }

    public void setDrawableLeft(@IdRes int id, @DrawableRes int resId) {
        ((TextView) obtainView(id)).setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
    }

    public void setDrawableLeft(@IdRes int id, Drawable drawable) {
        ((TextView) obtainView(id)).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    public void setDrawableRight(@IdRes int id, Drawable drawable) {
        ((TextView) obtainView(id)).setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    public void setDrawableRight(@IdRes int id, @DrawableRes int resId) {
        ((TextView) obtainView(id)).setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0);
    }

    public void setTextSize(@IdRes int id, float size) {
        ((TextView) obtainView(id)).setTextSize(size);
    }

    public void setTextSize(@IdRes int id, int unit, float size) {
        ((TextView) obtainView(id)).setTextSize(unit, size);
    }

    //可以直接引用 R.color.xxx
    public void setTextColor(@IdRes int id, @ColorInt int color) {
        ((TextView) obtainView(id)).setTextColor(color);
    }

    public void setTextColorRes(@IdRes int id, @ColorRes int color) {
        ((TextView) obtainView(id)).setTextColor(ContextCompat.getColor(mContext, color));
    }

    public void setImageResource(@IdRes int id, @DrawableRes int resId) {
        ((ImageView) obtainView(id)).setImageResource(resId);
    }

    public void setVisibility(@IdRes int id, int visibility) {
        obtainView(id).setVisibility(visibility);
    }

    public void setVisibility(@IdRes int id, boolean isVisiable) {
        if (isVisiable) obtainView(id).setVisibility(View.VISIBLE);
        else obtainView(id).setVisibility(View.GONE);
    }

    public void setInVisibility(@IdRes int id) {
        obtainView(id).setVisibility(View.INVISIBLE);
    }

    public void setBackgroundColor(@IdRes int id, @ColorInt int color) {
        obtainView(id).setBackgroundColor(color);
    }

    public void setBackgroundColorRes(@IdRes int id, @ColorRes int color) {
        obtainView(id).setBackgroundColor(ContextCompat.getColor(mContext, color));
    }

    public void setBackgroundResource(@IdRes int id, @DrawableRes int resId) {
        obtainView(id).setBackgroundResource(resId);
    }

    public void setChecked(@IdRes int id, boolean isChecked) {
        if (obtainView(id) instanceof CheckBox) {
            ((CheckBox) obtainView(id)).setChecked(isChecked);
        } else if (obtainView(id) instanceof RadioButton) {
            ((RadioButton) obtainView(id)).setChecked(isChecked);
        } else {
            ((Checkable) obtainView(id)).setChecked(isChecked);
        }
    }

    public void setProgress(@IdRes int id, int progress) {
        ((ProgressBar) obtainView(id)).setProgress(progress);
    }

    public void setProgress(@IdRes int id, int progress, int max) {
        ProgressBar progressBar = obtainView(id);
        progressBar.setProgress(progress);
        progressBar.setMax(max);
    }

    public void setMax(@IdRes int id, int max) {
        ((ProgressBar) obtainView(id)).setMax(max);
    }

    public void setOnClickListener(@IdRes int id, View.OnClickListener onClickListener) {
        obtainView(id).setOnClickListener(onClickListener);
    }

    public void setLayoutParams(@IdRes int id, ViewGroup.LayoutParams params) {
        obtainView(id).setLayoutParams(params);
    }

    public void setEnabled(@IdRes int id, boolean isEnabled) {
        obtainView(id).setEnabled(isEnabled);
    }

    public void setVerticalLayoutManager(@IdRes int id) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(id, layoutManager);
    }

    public void setHorizontalLayoutManager(@IdRes int id) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(id, layoutManager);
    }

    public void setGridLayoutManager(@IdRes int id, int spanCount) {
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, spanCount);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        setLayoutManager(id, layoutManager);
    }

    public void setGridLayoutManager(@IdRes int id, int spanCount, int orientation, boolean reverseLayout) {
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, spanCount, orientation, reverseLayout);
        setLayoutManager(id, layoutManager);
    }

    public void setLayoutManager(@IdRes int id, @Nullable RecyclerView.LayoutManager layoutManager) {
        ((RecyclerView) obtainView(id)).setLayoutManager(layoutManager);
    }

    public void setAdapter(@IdRes int id, @Nullable RecyclerView.Adapter adapter) {
        ((RecyclerView) obtainView(id)).setAdapter(adapter);
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T t, int position);
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
