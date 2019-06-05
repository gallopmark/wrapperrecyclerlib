package com.gallopmark.wrapperlib.swipehelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.gallopmark.wrapperlib.R;

public class SwipeItemLayout extends ViewGroup {
    enum Mode {
        RESET, DRAG, FLING, CLICK
    }

    private Mode touchMode;
    private View mainItemView;
    private boolean mInLayout = false;
    private int scrollOffset;
    private int maxScrollOffset;
    private ScrollRunnable scrollRunnable;
    public SwipeItemLayout(Context context) {
        this(context, null);
    }

    public SwipeItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchMode = Mode.RESET;
        scrollOffset = 0;
        scrollRunnable = new ScrollRunnable(context);
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void open() {
        if (scrollOffset != -maxScrollOffset) {
            if (touchMode == Mode.FLING)
                scrollRunnable.abort();
            scrollRunnable.startScroll(scrollOffset, -maxScrollOffset);
        }
    }

    public void close() {
        if (scrollOffset != 0) {
            if (touchMode == Mode.FLING)
                scrollRunnable.abort();
            scrollRunnable.startScroll(scrollOffset, 0);
        }
    }

    void fling(int xVel) {
        scrollRunnable.startFling(scrollOffset, xVel);
    }

    void revise() {
        if (scrollOffset < -maxScrollOffset / 2)
            open();
        else
            close();
    }

    private void ensureChildren() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            ViewGroup.LayoutParams tempLp = childView.getLayoutParams();
            if (!(tempLp instanceof LayoutParams))
                throw new IllegalStateException("缺少layout参数");
            LayoutParams lp = (LayoutParams) tempLp;
            if (lp.itemType == 0x01) {
                mainItemView = childView;
            }
        }
        if (mainItemView == null)
            throw new IllegalStateException("main item不能为空");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //确定children
        ensureChildren();
        //先测量main
        LayoutParams lp = (LayoutParams) mainItemView.getLayoutParams();
        measureChildWithMargins(
                mainItemView,
                widthMeasureSpec, getPaddingLeft() + getPaddingRight(),
                heightMeasureSpec, getPaddingTop() + getPaddingBottom());
        setMeasuredDimension(
                mainItemView.getMeasuredWidth() + getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin
                , mainItemView.getMeasuredHeight() + getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin);
        //测试menu
        int menuWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int menuHeightSpec = MeasureSpec.makeMeasureSpec(mainItemView.getMeasuredHeight(), MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); i++) {
            View menuView = getChildAt(i);
            lp = (LayoutParams) menuView.getLayoutParams();
            if (lp.itemType == 0x01)
                continue;
            measureChildWithMargins(menuView, menuWidthSpec, 0, menuHeightSpec, 0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mInLayout = true;
        //确定children
        ensureChildren();
        int pl = getPaddingLeft();
        int pt = getPaddingTop();
        int pr = getPaddingRight();
        int pb = getPaddingBottom();
        LayoutParams lp;
        //layout main
        lp = (LayoutParams) mainItemView.getLayoutParams();
        mainItemView.layout(
                pl + lp.leftMargin,
                pt + lp.topMargin,
                getWidth() - pr - lp.rightMargin,
                getHeight() - pb - lp.bottomMargin);

        //layout menu
        int totalLength = 0;
        int menuLeft = mainItemView.getRight() + lp.rightMargin;
        for (int i = 0; i < getChildCount(); i++) {
            View menuView = getChildAt(i);
            lp = (LayoutParams) menuView.getLayoutParams();

            if (lp.itemType == 0x01)
                continue;

            int tempLeft = menuLeft + lp.leftMargin;
            int tempTop = pt + lp.topMargin;
            menuView.layout(
                    tempLeft,
                    tempTop,
                    tempLeft + menuView.getMeasuredWidth() + lp.rightMargin,
                    tempTop + menuView.getMeasuredHeight() + lp.bottomMargin);

            menuLeft = menuView.getRight() + lp.rightMargin;
            totalLength += lp.leftMargin + lp.rightMargin + menuView.getMeasuredWidth();
        }

        maxScrollOffset = totalLength;
        scrollOffset = scrollOffset < -maxScrollOffset / 2 ? -maxScrollOffset : 0;

        offsetChildrenLeftAndRight(scrollOffset);

        mInLayout = false;
    }

    void offsetChildrenLeftAndRight(int delta) {
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            ViewCompat.offsetLeftAndRight(childView, delta);
        }
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(scrollRunnable);
        touchMode = Mode.RESET;
        scrollOffset = 0;
    }

    //展开的情况下，拦截down event，避免触发点击main事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                View pointView = ViewHelper.findTopChildUnder(this, x, y);
                if (pointView != null && pointView == mainItemView && scrollOffset != 0)
                    return true;
                break;
            }

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_UP: {
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                View pointView = ViewHelper.findTopChildUnder(this, x, y);
                if (pointView != null && pointView == mainItemView && touchMode == Mode.CLICK && scrollOffset != 0)
                    return true;
            }
        }

        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                View pointView =  ViewHelper.findTopChildUnder(this, x, y);
                if (pointView != null && pointView == mainItemView && scrollOffset != 0)
                    return true;
                break;
            }

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_UP: {
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                View pointView =  ViewHelper.findTopChildUnder(this, x, y);
                if (pointView != null && pointView == mainItemView && touchMode == Mode.CLICK && scrollOffset != 0) {
                    close();
                    return true;
                }
            }
        }

        return false;
    }

    void setTouchMode(Mode mode) {
        if (mode == touchMode)
            return;
        if (touchMode == Mode.FLING)
            removeCallbacks(scrollRunnable);
        touchMode = mode;
    }

    public Mode getTouchMode() {
        return touchMode;
    }

    boolean trackMotionScroll(int deltaX) {
        if (deltaX == 0)
            return true;

        boolean over = false;
        int newLeft = scrollOffset + deltaX;
        if ((deltaX > 0 && newLeft > 0) || (deltaX < 0 && newLeft < -maxScrollOffset)) {
            over = true;
            newLeft = Math.min(newLeft, 0);
            newLeft = Math.max(newLeft, -maxScrollOffset);
        }

        offsetChildrenLeftAndRight(newLeft - scrollOffset);
        scrollOffset = newLeft;
        return over;
    }

    private final Interpolator sInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    private class ScrollRunnable implements Runnable {
        private Scroller scroller;
        private boolean abort;
        private int minVelocity;

        ScrollRunnable(Context context) {
            scroller = new Scroller(context, sInterpolator);
            abort = false;

            ViewConfiguration configuration = ViewConfiguration.get(context);
            minVelocity = configuration.getScaledMinimumFlingVelocity();
        }

        void startScroll(int startX, int endX) {
            if (startX != endX) {
                setTouchMode(Mode.FLING);
                abort = false;
                scroller.startScroll(startX, 0, endX - startX, 0, 500);
                ViewCompat.postOnAnimation(SwipeItemLayout.this, this);
            }
        }

        void startFling(int startX, int xVel) {
            if (xVel > minVelocity && startX != 0) {
                startScroll(startX, 0);
                return;
            }
            if (xVel < -minVelocity && startX != -maxScrollOffset) {
                startScroll(startX, -maxScrollOffset);
                return;
            }
            startScroll(startX, startX > -maxScrollOffset / 2 ? 0 : -maxScrollOffset);
        }

        void abort() {
            if (!abort) {
                abort = true;
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                    removeCallbacks(this);
                }
            }
        }

        @Override
        public void run() {
            if (!abort) {
                boolean more = scroller.computeScrollOffset();
                int curX = scroller.getCurrX();
                boolean atEdge = false;
                if (curX != scrollOffset)
                    atEdge = trackMotionScroll(curX - scrollOffset);
                if (more && !atEdge) {
                    ViewCompat.postOnAnimation(SwipeItemLayout.this, this);
                } else {
                    removeCallbacks(this);
                    if (!scroller.isFinished())
                        scroller.abortAnimation();
                    setTouchMode(Mode.RESET);
                }
            }
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams ? (LayoutParams) p : new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int itemType = -1;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.SwipeItemLayout_Layout);
            itemType = a.getInt(R.styleable.SwipeItemLayout_Layout_layout_itemType, -1);
            a.recycle();
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            itemType = source.itemType;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }
}
