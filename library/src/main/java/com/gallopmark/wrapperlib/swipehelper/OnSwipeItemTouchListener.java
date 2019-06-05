package com.gallopmark.wrapperlib.swipehelper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

public class OnSwipeItemTouchListener implements RecyclerView.OnItemTouchListener {
    private SwipeItemLayout captureItem;
    private float lastMotionX;
    private float lastMotionY;
    private VelocityTracker velocityTracker;

    private int activePointerId;

    private int touchSlop;
    private int maximumVelocity;

    private boolean parentHandled;
    private boolean probingParentProcess;

    private boolean ignoreActions = false;

    public OnSwipeItemTouchListener(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        maximumVelocity = configuration.getScaledMaximumFlingVelocity();
        activePointerId = -1;
        parentHandled = false;
        probingParentProcess = false;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent ev) {
        if (probingParentProcess)
            return false;

        boolean intercept = false;
        final int action = ev.getActionMasked();
        if (action != MotionEvent.ACTION_DOWN && ignoreActions)
            return true;
        if (action != MotionEvent.ACTION_DOWN && (captureItem == null || parentHandled))
            return false;
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                ignoreActions = false;
                parentHandled = false;
                activePointerId = ev.getPointerId(0);
                final float x = ev.getX();
                final float y = ev.getY();
                lastMotionX = x;
                lastMotionY = y;
                boolean pointOther = false;
                SwipeItemLayout pointItem = null;
                //首先知道ev针对的是哪个item
                View pointView = ViewHelper.findTopChildUnder(rv, (int) x, (int) y);
                if (!(pointView instanceof SwipeItemLayout)) {
                    //可能是head view或bottom view
                    pointOther = true;
                } else
                    pointItem = (SwipeItemLayout) pointView;
                //此时的pointOther=true，意味着点击的view为空或者点击的不是item
                //还没有把点击的是item但是不是capture item给过滤出来
                if (!pointOther && (captureItem == null || captureItem != pointItem))
                    pointOther = true;

                //点击的是capture item
                if (!pointOther) {
                    SwipeItemLayout.Mode mode = captureItem.getTouchMode();
                    //如果它在fling，就转为drag
                    //需要拦截，并且requestDisallowInterceptTouchEvent
                    boolean disallowIntercept = false;
                    if (mode == SwipeItemLayout.Mode.FLING) {
                        captureItem.setTouchMode(SwipeItemLayout.Mode.DRAG);
                        disallowIntercept = true;
                        intercept = true;
                    } else {//如果是expand的，就不允许parent拦截
                        captureItem.setTouchMode(SwipeItemLayout.Mode.CLICK);
                        if (captureItem.getScrollOffset() != 0)
                            disallowIntercept = true;
                    }

                    if (disallowIntercept) {
                        final ViewParent parent = rv.getParent();
                        if (parent != null)
                            parent.requestDisallowInterceptTouchEvent(true);
                    }
                } else {//capture item为null或者与point item不一样
                    //直接将其close掉
                    if (captureItem != null &&
                            captureItem.getScrollOffset() != 0) {
                        captureItem.close();
                        ignoreActions = true;
                        return true;
                    }
                    captureItem = null;
                    if (pointItem != null) {
                        captureItem = pointItem;
                        captureItem.setTouchMode(SwipeItemLayout.Mode.CLICK);
                    }
                }

                //如果parent处于fling状态，此时，parent就会转为drag。应该将后续move都交给parent处理
                probingParentProcess = true;
                parentHandled = rv.onInterceptTouchEvent(ev);
                probingParentProcess = false;
                if (parentHandled) {
                    intercept = false;
                    //在down时，就被认定为parent的drag，所以，直接交给parent处理即可
                    if (captureItem != null && captureItem.getScrollOffset() != 0)
                        captureItem.close();
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                final int actionIndex = ev.getActionIndex();
                activePointerId = ev.getPointerId(actionIndex);
                lastMotionX = ev.getX(actionIndex);
                lastMotionY = ev.getY(actionIndex);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int actionIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(actionIndex);
                if (pointerId == activePointerId) {
                    final int newIndex = actionIndex == 0 ? 1 : 0;
                    activePointerId = ev.getPointerId(newIndex);
                    lastMotionX = ev.getX(newIndex);
                    lastMotionY = ev.getY(newIndex);
                }
                break;
            }

            //down时，已经将capture item定下来了。所以，后面可以安心考虑event处理
            case MotionEvent.ACTION_MOVE: {
                final int activePointerIndex = ev.findPointerIndex(activePointerId);
                if (activePointerIndex == -1)
                    break;

                final int x = (int) (ev.getX(activePointerIndex) + .5f);
                final int y = (int) ((int) ev.getY(activePointerIndex) + .5f);

                int deltaX = (int) (x - lastMotionX);
                int deltaY = (int) (y - lastMotionY);
                final int xDiff = Math.abs(deltaX);
                final int yDiff = Math.abs(deltaY);

                SwipeItemLayout.Mode mode = captureItem.getTouchMode();

                if (mode == SwipeItemLayout.Mode.CLICK) {
                    //如果capture item是open的，下拉有两种处理方式：
                    //  1、下拉后，直接close item
                    //  2、只要是open的，就拦截所有它的消息，这样如果点击open的，就只能滑动该capture item
                    if (xDiff > touchSlop && xDiff > yDiff) {
                        captureItem.setTouchMode(SwipeItemLayout.Mode.DRAG);
                        final ViewParent parent = rv.getParent();
                        parent.requestDisallowInterceptTouchEvent(true);

                        deltaX = deltaX > 0 ? deltaX - touchSlop : deltaX + touchSlop;
                    } else/* if(yDiff>touchSlop)*/ {
                        probingParentProcess = true;
                        parentHandled = rv.onInterceptTouchEvent(ev);
                        probingParentProcess = false;

                        if (parentHandled && captureItem.getScrollOffset() != 0)
                            captureItem.close();
                    }
                }
                mode = captureItem.getTouchMode();
                if (mode == SwipeItemLayout.Mode.DRAG) {
                    intercept = true;
                    lastMotionX = x;
                    lastMotionY = y;

                    //对capture item进行拖拽
                    captureItem.trackMotionScroll(deltaX);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
                SwipeItemLayout.Mode mode = captureItem.getTouchMode();
                if (mode == SwipeItemLayout.Mode.DRAG) {
                    final VelocityTracker velocityTracker = this.velocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, maximumVelocity);
                    int xVel = (int) velocityTracker.getXVelocity(activePointerId);
                    captureItem.fling(xVel);
                    intercept = true;
                }
                cancel();
                break;
            case MotionEvent.ACTION_CANCEL:
                captureItem.revise();
                cancel();
                break;
        }

        return intercept;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent ev) {
        if (ignoreActions)
            return;

        final int action = ev.getActionMasked();
        final int actionIndex = ev.getActionIndex();

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
                activePointerId = ev.getPointerId(actionIndex);

                lastMotionX = ev.getX(actionIndex);
                lastMotionY = ev.getY(actionIndex);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                final int pointerId = ev.getPointerId(actionIndex);
                if (pointerId == activePointerId) {
                    final int newIndex = actionIndex == 0 ? 1 : 0;
                    activePointerId = ev.getPointerId(newIndex);

                    lastMotionX = ev.getX(newIndex);
                    lastMotionY = ev.getY(newIndex);
                }
                break;
            //down时，已经将capture item定下来了。所以，后面可以安心考虑event处理
            case MotionEvent.ACTION_MOVE: {
                final int activePointerIndex = ev.findPointerIndex(activePointerId);
                if (activePointerIndex == -1)
                    break;

                final float x = ev.getX(activePointerIndex);
                final float y = (int) ev.getY(activePointerIndex);

                int deltaX = (int) (x - lastMotionX);

                if (captureItem != null && captureItem.getTouchMode() == SwipeItemLayout.Mode.DRAG) {
                    lastMotionX = x;
                    lastMotionY = y;

                    //对capture item进行拖拽
                    captureItem.trackMotionScroll(deltaX);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
                if (captureItem != null) {
                    SwipeItemLayout.Mode mode = captureItem.getTouchMode();
                    if (mode == SwipeItemLayout.Mode.DRAG) {
                        final VelocityTracker velocityTracker = this.velocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, maximumVelocity);
                        int xVel = (int) velocityTracker.getXVelocity(activePointerId);
                        captureItem.fling(xVel);
                    }
                }
                cancel();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (captureItem != null)
                    captureItem.revise();
                cancel();
                break;

        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private void cancel() {
        parentHandled = false;
        activePointerId = -1;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }
}
