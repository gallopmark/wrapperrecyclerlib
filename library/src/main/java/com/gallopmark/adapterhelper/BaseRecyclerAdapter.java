package com.gallopmark.adapterhelper;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.*;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemChildClickListener onItemChildClickListener;
    private OnItemChildLongClickListener onItemChildLongClickListener;

    protected BaseRecyclerAdapter(Context context) {
        this.mContext = context;
    }

    protected abstract int bindView(int viewType);

    public Object getItem(int position) {
        return null;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerHolder(LayoutInflater.from(mContext).inflate(bindView(viewType), parent, false));
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

        public void bindChildClick(@IdRes int id) {
            obtainView(id).setOnClickListener(this);
        }

        /**
         * 子控件绑定局部点击事件
         */
        public void bindChildClick(View view) {
            view.setOnClickListener(this);
        }

        public void bindChildLongClick(@IdRes int id) {
            obtainView(id).setOnLongClickListener(this);
        }

        public void bindChildLongClick(View view) {
            view.setOnLongClickListener(this);
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
