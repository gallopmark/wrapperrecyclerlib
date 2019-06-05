package com.gallopmark.decorationhepler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/*LinearLayoutManager分割线*/
public class LinearItemDecoration extends RecyclerView.ItemDecoration {
    private Paint mPaint;
    private Drawable mDivider;
    private int mDividerHeight = 2;//分割线高度
    private int mOrientation;//列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
    private int margin; //分割线margin marginLeft&marginRight or marginTop&marginBottom
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private LinearItemDecoration(int orientation) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("请输入正确的参数！");
        }
        mOrientation = orientation;
    }

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param orientation 列表方向
     */
    public LinearItemDecoration(Context context, int orientation) {
        this(orientation);
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    public LinearItemDecoration(Context context, int orientation, int margin) {
        this(context, orientation);
        this.margin = margin;
    }

    /**
     * 自定义分割线
     *
     * @param orientation 列表方向
     * @param drawable    分割线图片
     */
    public LinearItemDecoration(int orientation, @Nullable Drawable drawable) {
        this(orientation);
        mDivider = drawable;
        if (mDivider != null)
            mDividerHeight = mDivider.getIntrinsicHeight();
    }

    public LinearItemDecoration(int orientation, @Nullable Drawable drawable, int margin) {
        this(orientation, drawable);
        this.margin = margin;
    }

    /**
     * 自定义分割线
     *
     * @param orientation   列表方向
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    public LinearItemDecoration(int orientation, int dividerHeight, int dividerColor) {
        this(orientation);
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public LinearItemDecoration(int orientation, int dividerHeight, int dividerColor, int margin) {
        this(orientation, dividerHeight, dividerColor);
        this.margin = margin;
    }

    //获取分割线尺寸
    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, mDividerHeight);
        } else {
            outRect.set(0, 0, mDividerHeight, 0);
        }
    }

    //绘制分割线
    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    /**
     * 绘制纵向列表时的分隔线  这时分隔线是横着的
     * 每次 left相同，top根据child变化，right相同，bottom也变化
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        if (mDivider == null && mPaint == null) return;
        final int left = parent.getPaddingLeft() + margin;
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight() - margin;
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            } else {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    /**
     * 绘制横向列表时的分隔线  这时分隔线是竖着的
     * l、r 变化； t、b 不变
     */
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        if (mDivider == null && mPaint == null) return;
        final int top = parent.getPaddingTop() + margin;
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom() - margin;
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            } else {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

}
