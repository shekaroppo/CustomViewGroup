package shekar.com.customviewgroup.elasticviewgroup;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import shekar.com.customviewgroup.R;
import timber.log.Timber;

/**
 * Created by Shekar on 4/26/16.
 */
public class ElasticViewGroup extends ViewGroup {

    private View mRecyclerView;
    private TextView mTextView;
    private float layoutTop = 0;
    private float initTouchX = 0;
    private float initInterceptX = 0;
    private final static float OVER_SCROLL_STRETCH_FACTOR = 0.40f;
    private View mHiddenView;
    private DecelerateInterpolator mInterpolator;

    public ElasticViewGroup(Context context) {
        super(context);
    }

    public ElasticViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTextView(context);
    }

    public ElasticViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTextView(context);
    }

    private void initTextView(Context context) {
        mInterpolator=new DecelerateInterpolator();
        LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHiddenView =inflater.inflate(R.layout.hidden_view, this,false);
        mTextView = (TextView) mHiddenView.findViewById(R.id.hidden_text_view);
        addView(mHiddenView);
    }

    private void initChildViews() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof RecyclerView) {
                mRecyclerView = child;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initChildViews();
                mRecyclerView.measure(widthMeasureSpec, heightMeasureSpec);
        final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                (int) ((mRecyclerView.getMeasuredWidth() * OVER_SCROLL_STRETCH_FACTOR)), MeasureSpec.EXACTLY);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        // mHiddenView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        // Above measure call wont work as we need margins so instead call measureChildWithMargins
        measureChildWithMargins(mHiddenView, childWidthMeasureSpec, 0, childHeightMeasureSpec, 0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childTop = this.getPaddingTop();
        final int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();
        int newTop = (int) layoutTop;
        final LayoutParams layoutParams = (LayoutParams) mHiddenView.getLayoutParams();
        int hiddenViewLeft=-mHiddenView.getMeasuredWidth() + newTop;
        int hiddenViewTop=childTop+layoutParams.topMargin;
        int hiddenViewRight=newTop-layoutParams.rightMargin;
        int hiddenViewBottom=childBottom-layoutParams.bottomMargin;;
        mRecyclerView.layout(newTop, childTop, childRight, childBottom);
        mHiddenView.layout(hiddenViewLeft,hiddenViewTop,hiddenViewRight, hiddenViewBottom);
        float interpolatorFactor=newTop/(childRight * OVER_SCROLL_STRETCH_FACTOR);
        float accelratedInterpolatorFactor=mInterpolator.getInterpolation(interpolatorFactor);
        float translateValue=((-mTextView.getMeasuredWidth()/2)+((newTop/2)*accelratedInterpolatorFactor));
        Timber.d("onLayout(): "+translateValue);
        mTextView.setTranslationX(translateValue<0?translateValue:0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                initInterceptX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - initInterceptX;
                initInterceptX = ev.getX();
                if (dx > 0 && !mRecyclerView.canScrollHorizontally(-1)) {
                    initTouchX = initInterceptX;
                    return true;
                }
                break;

        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            resetToPosition(0, Math.abs((int) layoutTop));
            return true;
        } else {
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    initTouchX = event.getX();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getX() - initTouchX;
                    initTouchX = event.getX();
                    if (deltaX > 0) {
                        onScrollRight(deltaX);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    resetToPosition(0, Math.abs((int) layoutTop));
                    return true;
            }
            return super.onTouchEvent(event);
        }
    }

    private void onScrollRight(float deltaY) {
        layoutTop += deltaY * OVER_SCROLL_STRETCH_FACTOR;
        requestLayout();
    }

    private void resetToPosition(float toX, int duration) {
        ValueAnimator xAnimator = ValueAnimator.ofFloat(layoutTop, toX)
                .setDuration(duration);
        xAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutTop = (float) animation.getAnimatedValue();
                requestLayout();
            }
        });
        xAnimator.start();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        Timber.d("generateLayoutParams(): ====");
        return new ElasticViewGroup.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        Timber.d("generateDefaultLayoutParams(): ====");
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        Timber.d("generateLayoutParams(): ====");
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /**
     * Custom per-child layout information.
     */
    public static class LayoutParams extends MarginLayoutParams {


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}

