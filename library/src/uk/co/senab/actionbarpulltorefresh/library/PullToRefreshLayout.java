/*
 * Copyright 2013 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.senab.actionbarpulltorefresh.library;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.HeaderViewListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.ViewDelegate;

/**
 * The main component of the library. You wrap the views you wish to be 'pullable' within this layout.
 * This layout is setup by using the {@link ActionBarPullToRefresh} setup-wizard return by
 * @link ActionBarPullToRefresh#from(android.app.Activity)}.
 */
public class PullToRefreshLayout extends FrameLayout {

    private static final String LOG_TAG = "PullToRefreshLayout";

    private PullToRefreshAttacher mPullToRefreshAttacher;

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Manually set this Attacher's refreshing state. The header will be
     * displayed or hidden as requested.
     *
     * @param refreshing
     *            - Whether the attacher should be in a refreshing state,
     */
    public final void setRefreshing(boolean refreshing) {
        mPullToRefreshAttacher.setRefreshing(refreshing);
    }

    /**
     * @return true if this Attacher is currently in a refreshing state.
     */
    public final boolean isRefreshing() {
        return mPullToRefreshAttacher.isRefreshing();
    }

    /**
     * Call this when your refresh is complete and this view should reset itself
     * (header view will be hidden).
     *
     * This is the equivalent of calling <code>setRefreshing(false)</code>.
     */
    public final void setRefreshComplete() {
        mPullToRefreshAttacher.setRefreshComplete();
    }

    /**
     * Set a {@link uk.co.senab.actionbarpulltorefresh.library.listeners.HeaderViewListener} which is called when the visibility
     * state of the Header View has changed.
     *
     * @param listener
     */
    public final void setHeaderViewListener(HeaderViewListener listener) {
        mPullToRefreshAttacher.setHeaderViewListener(listener);
    }

    /**
     * @return The Header View which is displayed when the user is pulling, or
     *         we are refreshing.
     */
    public final View getHeaderView() {
        return mPullToRefreshAttacher.getHeaderView();
    }

    /**
     * @return The HeaderTransformer currently used by this Attacher.
     */
    public HeaderTransformer getHeaderTransformer() {
        return mPullToRefreshAttacher.getHeaderTransformer();
    }


    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        if (isEnabled() && mPullToRefreshAttacher != null && getChildCount() > 0) {
            return mPullToRefreshAttacher.onInterceptTouchEvent(event);
        }
        return false;
    }

    @Override
    public final boolean onTouchEvent(MotionEvent event) {
        if (isEnabled() && mPullToRefreshAttacher != null) {
            return mPullToRefreshAttacher.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PullToRefreshLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        // Destroy the PullToRefreshAttacher
        //if (mPullToRefreshAttacher != null) {
        //    mPullToRefreshAttacher.destroy();
        //}
        super.onDetachedFromWindow();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        if (mPullToRefreshAttacher != null) {
            mPullToRefreshAttacher.onConfigurationChanged(newConfig);
        }
        super.onConfigurationChanged(newConfig);
    }

    public void setPullToRefreshAttacher(PullToRefreshAttacher attacher) {
        mPullToRefreshAttacher = attacher;
        mPullToRefreshAttacher.clearRefreshableViews();
    }

    public void addAllChildrenAsPullable() {
        for (int i = 0, z = getChildCount(); i < z; i++) {
            addRefreshableView(getChildAt(i));
        }
    }

    public void addChildrenAsPullable(int[] viewIds) {
        for (int i = 0, z = viewIds.length; i < z; i++) {
            View view = findViewById(viewIds[i]);
            if (view != null) {
                addRefreshableView(findViewById(viewIds[i]));
            }
        }
    }
    public void addChildrenAsPullable(int id){
        View view = findViewById(id);
        if(view != null){
            addRefreshableView(view);
        }
    }

    void addChildrenAsPullable(View[] views) {
        for (int i = 0, z = views.length; i < z; i++) {
            if (views[i] != null) {
                addRefreshableView(views[i]);
            }
        }
    }

    void addRefreshableView(View view) {
        if (mPullToRefreshAttacher != null) {
            mPullToRefreshAttacher.addRefreshableView(view, getViewDelegateFromLayoutParams(view,getContext()));
        }
    }
    public void addRefreshableView(View view,ViewDelegate delegate) {
        if (mPullToRefreshAttacher != null) {
            mPullToRefreshAttacher.addRefreshableView(view, delegate);
        }
    }

   private static ViewDelegate getViewDelegateFromLayoutParams(View view, Context context) {
        if (view != null && view.getLayoutParams() instanceof LayoutParams) {
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            String clazzName = lp.getViewDelegateClassName();

            if (!TextUtils.isEmpty(clazzName)) {
                // Lets convert any relative class names (i.e. .XYZViewDelegate)
                final int firstDot = clazzName.indexOf('.');
                if (firstDot == -1) {
                    clazzName = context.getPackageName() + "" + clazzName;
                } else if (firstDot == 0) {
                    clazzName = context.getPackageName() + clazzName;
                }
                return InstanceCreationUtils.instantiateViewDelegate(context, clazzName);
            }
        }
        return null;
    }


    static class LayoutParams extends FrameLayout.LayoutParams {
        private final String mViewDelegateClassName;

        LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.PullToRefreshView);
            mViewDelegateClassName = a
                    .getString(R.styleable.PullToRefreshView_ptrViewDelegateClass);
            a.recycle();
        }

        String getViewDelegateClassName() {
            return mViewDelegateClassName;
        }
    }
}
