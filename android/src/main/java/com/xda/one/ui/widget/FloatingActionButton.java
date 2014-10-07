package com.xda.one.ui.widget;

import com.xda.one.util.CompatUtils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

// import android.graphics.Outline;

public class FloatingActionButton extends ImageView {

    private final Context mContext;

    private Paint mButtonPaint;

    private int mScreenHeight;

    private float mCurrentY;

    public FloatingActionButton(final Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);

        mContext = context;

        init(Color.BLUE);
    }

    public FloatingActionButton(final Context context) {
        super(context);

        mContext = context;

        init(Color.BLUE);
    }

    private void init(final int color) {
        final WindowManager windowManager = (WindowManager) mContext.getSystemService(Context
                .WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        mScreenHeight = size.y;

        setClickable(true);

        if (CompatUtils.hasL()) {
            /*setElevation(getResources().getDimension(R.dimen.fab_elevation));

            final int diameter = getResources().getDimensionPixelSize(R.dimen.fab_size);

            final Outline outline = new Outline();
            outline.setOval(0, 0, diameter, diameter);

            setOutline(outline);
            setClipToOutline(true);*/
        } else {
            setLayerType(LAYER_TYPE_SOFTWARE, null);

            super.setBackgroundColor(Color.TRANSPARENT);
            updatePreLBackgroundColor(color);
        }
        invalidate();
    }

    private void updatePreLBackgroundColor(int color) {
        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setColor(color);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setShadowLayer(10f, 0.0f, 3.5f, Color.argb(100, 0, 0, 0));
    }

    @Override
    protected void onDraw(@NonNull final Canvas canvas) {
        if (!CompatUtils.hasL()) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() / 2f) - 10f,
                    mButtonPaint);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(@NonNull final MotionEvent event) {
        if (CompatUtils.hasL()) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                setAlpha(1.0f);
                break;
            case MotionEvent.ACTION_DOWN:
                setAlpha(0.6f);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setBackgroundColor(final int fabColor) {
        if (CompatUtils.hasL()) {
            super.setBackgroundColor(fabColor);
        } else {
            updatePreLBackgroundColor(fabColor);
            invalidate();
        }
    }

    public void hide() {
        if (getVisibility() == VISIBLE) {
            mCurrentY = getY();
            ObjectAnimator hideAnimation = ObjectAnimator.ofFloat(this, "Y", mScreenHeight);
            hideAnimation.setInterpolator(new AccelerateInterpolator());
            hideAnimation.start();
            setVisibility(GONE);
        }
    }

    public void show() {
        if (getVisibility() != VISIBLE) {
            final ObjectAnimator showAnimation = ObjectAnimator.ofFloat(this, "Y", mCurrentY);
            showAnimation.setInterpolator(new DecelerateInterpolator());
            showAnimation.start();
            setVisibility(VISIBLE);
        }
    }
}