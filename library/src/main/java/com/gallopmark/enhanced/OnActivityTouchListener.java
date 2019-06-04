package com.gallopmark.enhanced;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

/**
 * Closing swipe options when clicked anywhere outside of the recyclerView:
 * Make your Activity implement RecyclerTouchListener.RecyclerTouchListenerHelper and store the touchListener
 * private OnActivityTouchListener touchListener;
 *
 * @Override public void setOnActivityTouchListener(OnActivityTouchListener listener) {
 * this.touchListener = listener;
 * }
 * Override dispatchTouchEvent() of your Activity and pass the MotionEvent variable to the touchListener
 * @Override public boolean dispatchTouchEvent(MotionEvent ev) {
 * if (touchListener != null) touchListener.getTouchCoordinates(ev);
 * return super.dispatchTouchEvent(ev);
 * }
 */
public interface OnActivityTouchListener {
    void touchCoordinates(@NonNull MotionEvent ev);
}
