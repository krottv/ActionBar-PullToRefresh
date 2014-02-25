package uk.co.senab.actionbarpulltorefresh.library;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class DialogHeaderTransformer extends HeaderTransformer {
    private static final int PROGRESS_BAR_STYLE_INSIDE = 0;
    private static final int PROGRESS_BAR_STYLE_OUTSIDE = 1;

    private SmoothProgressBar mHeaderProgressBar;

    private int mProgressDrawableColor;

    private int mProgressBarStyle;
    private int mProgressBarHeight = RelativeLayout.LayoutParams.WRAP_CONTENT;

    private final Interpolator mInterpolator = new AccelerateInterpolator();

    public DialogHeaderTransformer() {
        final int min = getMinimumApiLevel();
        if (Build.VERSION.SDK_INT < min) {
            throw new IllegalStateException("This HeaderTransformer is designed to run on SDK "
                    + min
                    + "+. If using ActionBarSherlock or ActionBarCompat you should use the appropriate provided extra.");
        }
    }

    @Override
    public void onViewCreated(Activity activity, View headerView) {
        // Get ProgressBar and TextView
        this.mHeaderProgressBar = (SmoothProgressBar)headerView;

        mProgressDrawableColor = activity.getResources()
                .getColor(R.color.default_progress_bar_color);

        // Setup the View styles
        setupViewsFromStyles(activity);

        applyProgressBarStyle();

        // Apply any custom ProgressBar colors and corner radius
        applyProgressBarSettings();

        onReset();
    }

    @Override
    public void onConfigurationChanged(Activity activity, Configuration newConfig) {
        setupViewsFromStyles(activity);
    }

    @Override
    public void onReset() {
        // Reset Progress Bar
        if (mHeaderProgressBar != null) {
            mHeaderProgressBar.setVisibility(View.VISIBLE);
            mHeaderProgressBar.setProgress(0);
            mHeaderProgressBar.setIndeterminate(false);
        }
    }

    @Override
    public void onPulled(float percentagePulled) {
        if (mHeaderProgressBar != null) {
            mHeaderProgressBar.setIndeterminate(false);
            mHeaderProgressBar.setVisibility(View.VISIBLE);
            float progress = mInterpolator.getInterpolation(percentagePulled);
            mHeaderProgressBar.setProgress(Math.round(mHeaderProgressBar.getMax() * progress));
        }
    }

    @Override
    public void onRefreshStarted() {

        Log.i("TAG AUDIO", "onRefreshStarted " + (mHeaderProgressBar != null));

        if (mHeaderProgressBar != null) {
            mHeaderProgressBar.setVisibility(View.VISIBLE);
            mHeaderProgressBar.setIndeterminate(true);
            mHeaderProgressBar.invalidate();
        }

    }

    @Override
    public void onReleaseToRefresh() {
        if (mHeaderProgressBar != null) {
            mHeaderProgressBar.setProgress(mHeaderProgressBar.getMax());
        }
    }

    @Override
    public void onRefreshMinimized() {

    }

    @Override
    public boolean showHeaderView() {
        boolean wasInvisible = mHeaderProgressBar.getVisibility() != View.VISIBLE;
        mHeaderProgressBar.setVisibility(View.VISIBLE);
        mHeaderProgressBar.setProgress(0);
        return wasInvisible;
    }

    @Override
    public boolean hideHeaderView() {
        boolean wasVisible = mHeaderProgressBar.getVisibility() != View.GONE;
        mHeaderProgressBar.setVisibility(View.GONE);
        mHeaderProgressBar.setProgress(0);
        return wasVisible;
    }

    /**
     * Set color to apply to the progress bar.
     * <p/>
     * The best way to apply a color is to load the color from resources: {@code
     * setProgressBarColor(getResources().getColor(R.color.your_color_name))}.
     *
     * @param color The color to use.
     */
    public void setProgressBarColor(int color) {
        if (color != mProgressDrawableColor) {
            mProgressDrawableColor = color;
            applyProgressBarSettings();
        }
    }

    /**
     * Set the progress bar style. {@code style} must be one of {@link #PROGRESS_BAR_STYLE_OUTSIDE}
     * or {@link #PROGRESS_BAR_STYLE_INSIDE}.
     */
    public void setProgressBarStyle(int style) {
        if (mProgressBarStyle != style) {
            mProgressBarStyle = style;
            applyProgressBarStyle();
        }
    }

    /**
     * Set the progress bar height.
     */
    public void setProgressBarHeight(int height) {
        if (mProgressBarHeight != height) {
            mProgressBarHeight = height;
            applyProgressBarStyle();
        }
    }


    private void setupViewsFromStyles(Activity activity) {
        final TypedArray styleAttrs = obtainStyledAttrsFromThemeAttr(activity,
                R.attr.ptrHeaderStyle, R.styleable.PullToRefreshHeader);

        // Retrieve the Action Bar size from the app theme or the Action Bar's style


        // Retrieve the Progress Bar Color the style
        if (styleAttrs.hasValue(R.styleable.PullToRefreshHeader_ptrProgressBarColor)) {
            mProgressDrawableColor = styleAttrs.getColor(
                    R.styleable.PullToRefreshHeader_ptrProgressBarColor, mProgressDrawableColor);
        }

        mProgressBarStyle = styleAttrs.getInt(
                R.styleable.PullToRefreshHeader_ptrProgressBarStyle, PROGRESS_BAR_STYLE_OUTSIDE);

        if(styleAttrs.hasValue(R.styleable.PullToRefreshHeader_ptrProgressBarHeight)) {
            mProgressBarHeight = styleAttrs.getDimensionPixelSize(
                    R.styleable.PullToRefreshHeader_ptrProgressBarHeight, mProgressBarHeight);
        }

        styleAttrs.recycle();
    }

    private void applyProgressBarStyle() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, mProgressBarHeight);

        switch (mProgressBarStyle) {
            case PROGRESS_BAR_STYLE_INSIDE:
                lp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.ptr_content);
                break;
            case PROGRESS_BAR_STYLE_OUTSIDE:
                lp.addRule(RelativeLayout.BELOW, R.id.ptr_content);
                break;
        }

        mHeaderProgressBar.setLayoutParams(lp);
    }

    private void applyProgressBarSettings() {
        if (mHeaderProgressBar != null) {
            final int strokeWidth = mHeaderProgressBar.getResources()
                    .getDimensionPixelSize(R.dimen.ptr_progress_bar_stroke_width);

            mHeaderProgressBar.setIndeterminateDrawable(
                    new SmoothProgressDrawable.Builder(mHeaderProgressBar.getContext())
                            .color(mProgressDrawableColor)
                            .strokeWidth(strokeWidth)
                            .build());

            ShapeDrawable shape = new ShapeDrawable();
            shape.setShape(new RectShape());
            shape.getPaint().setColor(mProgressDrawableColor);
            ClipDrawable clipDrawable = new ClipDrawable(shape, Gravity.CENTER, ClipDrawable.HORIZONTAL);

            mHeaderProgressBar.setProgressDrawable(clipDrawable);
        }
    }

    protected int getMinimumApiLevel() {
        return Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    protected static TypedArray obtainStyledAttrsFromThemeAttr(Context context, int themeAttr,
                                                               int[] styleAttrs) {
        // Need to get resource id of style pointed to from the theme attr
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(themeAttr, outValue, true);
        final int styleResId =  outValue.resourceId;

        // Now return the values (from styleAttrs) from the style
        return context.obtainStyledAttributes(styleResId, styleAttrs);
    }
}
