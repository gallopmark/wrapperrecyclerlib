package com.gallopmark.recycler.adapterhelper;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;


public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseRecyclerAdapter.RecyclerHolder> {

    protected Context mContext;
    protected LayoutInflater mLayoutInflater;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemChildClickListener onItemChildClickListener;
    private OnItemChildLongClickListener onItemChildLongClickListener;

    protected BaseRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
    }

    public Object getItem(int position) {
        return null;
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private SparseArray<View> holder;

        RecyclerHolder(View itemView) {
            super(itemView);
            holder = new SparseArray<>();
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        /**
         * 获取子控件
         *
         * @return </T>
         */
        @SuppressWarnings("unchecked")
        public final <T extends View> T obtainView(@IdRes int id) {
            View view = holder.get(id);
            if (view != null) return (T) view;
            view = itemView.findViewById(id);
            holder.put(id, view);
            return (T) view;
        }

        public RecyclerHolder bindChildClick(@IdRes int id) {
            obtainView(id).setOnClickListener(this);
            return this;
        }

        /**
         * 子控件绑定局部点击事件
         */
        public RecyclerHolder bindChildClick(View view) {
            view.setOnClickListener(this);
            return this;
        }

        public RecyclerHolder bindChildLongClick(@IdRes int id) {
            obtainView(id).setOnLongClickListener(this);
            return this;
        }

        public RecyclerHolder bindChildLongClick(View view) {
            view.setOnLongClickListener(this);
            return this;
        }

        /**
         * 文本控件赋值
         */
        public RecyclerHolder setText(@IdRes int id, CharSequence text) {
            ((TextView) obtainView(id)).setText(text);
            return this;
        }

        public RecyclerHolder setTypeface(@IdRes int id, Typeface tf) {
            ((TextView) obtainView(id)).setTypeface(tf);
            return this;
        }

        public RecyclerHolder setDrawableLeft(@IdRes int id, @DrawableRes int resId) {
            ((TextView) obtainView(id)).setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
            return this;
        }

        public RecyclerHolder setDrawableLeft(@IdRes int id, @Nullable Drawable drawable) {
            ((TextView) obtainView(id)).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            return this;
        }

        public RecyclerHolder setDrawableTop(@IdRes int id, @DrawableRes int resId) {
            ((TextView) obtainView(id)).setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);
            return this;
        }

        public RecyclerHolder setDrawableTop(@IdRes int id, @Nullable Drawable drawable) {
            ((TextView) obtainView(id)).setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            return this;
        }

        public RecyclerHolder setDrawableRight(@IdRes int id, @Nullable Drawable drawable) {
            ((TextView) obtainView(id)).setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            return this;
        }

        public RecyclerHolder setDrawableRight(@IdRes int id, @DrawableRes int resId) {
            ((TextView) obtainView(id)).setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0);
            return this;
        }

        public RecyclerHolder setDrawableBottom(@IdRes int id, @DrawableRes int resId) {
            ((TextView) obtainView(id)).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, resId);
            return this;
        }

        public RecyclerHolder setDrawableBottom(@IdRes int id, @Nullable Drawable drawable) {
            ((TextView) obtainView(id)).setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
            return this;
        }

        public RecyclerHolder setTextSize(@IdRes int id, float size) {
            ((TextView) obtainView(id)).setTextSize(size);
            return this;
        }

        public RecyclerHolder setTextSize(@IdRes int id, int unit, float size) {
            ((TextView) obtainView(id)).setTextSize(unit, size);
            return this;
        }

        //可以直接引用 R.color.xxx
        public RecyclerHolder setTextColor(@IdRes int id, @ColorInt int color) {
            ((TextView) obtainView(id)).setTextColor(color);
            return this;
        }

        public RecyclerHolder setTextColorRes(@IdRes int id, @ColorRes int color) {
            ((TextView) obtainView(id)).setTextColor(ContextCompat.getColor(mContext, color));
            return this;
        }

        public RecyclerHolder setImageResource(@IdRes int id, @DrawableRes int resId) {
            ((ImageView) obtainView(id)).setImageResource(resId);
            return this;
        }

        public RecyclerHolder setVisibility(@IdRes int id, int visibility) {
            obtainView(id).setVisibility(visibility);
            return this;
        }

        public RecyclerHolder setVisibility(@IdRes int id, boolean isVisible) {
            if (isVisible) obtainView(id).setVisibility(View.VISIBLE);
            else obtainView(id).setVisibility(View.GONE);
            return this;
        }

        public RecyclerHolder setInVisibility(@IdRes int id) {
            obtainView(id).setVisibility(View.INVISIBLE);
            return this;
        }

        public RecyclerHolder setBackgroundColor(@IdRes int id, @ColorInt int color) {
            obtainView(id).setBackgroundColor(color);
            return this;
        }

        public RecyclerHolder setBackgroundColorRes(@IdRes int id, @ColorRes int color) {
            obtainView(id).setBackgroundColor(ContextCompat.getColor(mContext, color));
            return this;
        }

        public RecyclerHolder setBackgroundResource(@IdRes int id, @DrawableRes int resId) {
            obtainView(id).setBackgroundResource(resId);
            return this;
        }

        public RecyclerHolder setChecked(@IdRes int id, boolean isChecked) {
            if (obtainView(id) instanceof CheckBox) {
                ((CheckBox) obtainView(id)).setChecked(isChecked);
            } else if (obtainView(id) instanceof RadioButton) {
                ((RadioButton) obtainView(id)).setChecked(isChecked);
            } else {
                ((Checkable) obtainView(id)).setChecked(isChecked);
            }
            return this;
        }

        public RecyclerHolder setProgress(@IdRes int id, int progress) {
            ((ProgressBar) obtainView(id)).setProgress(progress);
            return this;
        }

        public RecyclerHolder setProgress(@IdRes int id, int progress, int max) {
            ProgressBar progressBar = obtainView(id);
            progressBar.setProgress(progress);
            progressBar.setMax(max);
            return this;
        }

        public RecyclerHolder setMax(@IdRes int id, int max) {
            ((ProgressBar) obtainView(id)).setMax(max);
            return this;
        }

        public RecyclerHolder setOnClickListener(@IdRes int id, View.OnClickListener onClickListener) {
            obtainView(id).setOnClickListener(onClickListener);
            return this;
        }

        public RecyclerHolder setLayoutParams(@IdRes int id, ViewGroup.LayoutParams params) {
            obtainView(id).setLayoutParams(params);
            return this;
        }

        public RecyclerHolder setEnabled(@IdRes int id, boolean isEnabled) {
            obtainView(id).setEnabled(isEnabled);
            return this;
        }

        public RecyclerHolder setVerticalLayoutManager(@IdRes int id) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            setLayoutManager(id, layoutManager);
            return this;
        }

        public RecyclerHolder setHorizontalLayoutManager(@IdRes int id) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
            setLayoutManager(id, layoutManager);
            return this;
        }

        public RecyclerHolder setGridLayoutManager(@IdRes int id, int spanCount) {
            GridLayoutManager layoutManager = new GridLayoutManager(mContext, spanCount);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            setLayoutManager(id, layoutManager);
            return this;
        }

        public RecyclerHolder setGridLayoutManager(@IdRes int id, int spanCount, int orientation, boolean reverseLayout) {
            GridLayoutManager layoutManager = new GridLayoutManager(mContext, spanCount, orientation, reverseLayout);
            setLayoutManager(id, layoutManager);
            return this;
        }

        public RecyclerHolder setLayoutManager(@IdRes int id, @Nullable RecyclerView.LayoutManager layoutManager) {
            ((RecyclerView) obtainView(id)).setLayoutManager(layoutManager);
            return this;
        }

        public RecyclerHolder setAdapter(@IdRes int id, @Nullable RecyclerView.Adapter adapter) {
            ((RecyclerView) obtainView(id)).setAdapter(adapter);
            return this;
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClickListener != null && v.getId() == this.itemView.getId()) {
                onItemLongClickListener.onItemLongClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
                return true;
            } else if (onItemChildLongClickListener != null && v.getId() != this.itemView.getId()) {
                onItemChildLongClickListener.onItemChildLongClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
                return true;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null && v.getId() == this.itemView.getId()) {
                onItemClickListener.onItemClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
            } else if (onItemChildClickListener != null && v.getId() != this.itemView.getId()) {
                onItemChildClickListener.onItemChildClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BaseRecyclerAdapter adapter, RecyclerHolder holder, View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(BaseRecyclerAdapter adapter, RecyclerHolder holder, View view, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemChildClickListener {
        void onItemChildClick(BaseRecyclerAdapter adapter, RecyclerHolder holder, View view, int position);
    }

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener;
    }

    public interface OnItemChildLongClickListener {
        void onItemChildLongClick(BaseRecyclerAdapter adapter, RecyclerHolder holder, View view, int position);
    }

    public void setOnItemChildLongClickListener(OnItemChildLongClickListener onItemChildLongClickListener) {
        this.onItemChildLongClickListener = onItemChildLongClickListener;
    }
}
