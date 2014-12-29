package com.xda.one.ui.widget;

import com.xda.one.R;
import com.xda.one.util.CompatUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

public class FloatingActionButton extends ImageView {

    private Paint mButtonPaint;

    public FloatingActionButton(final Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);

        init(Color.BLUE);
    }

    public FloatingActionButton(final Context context) {
        super(context);

        init(Color.BLUE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init(final int color) {
        setClickable(true);

        if (CompatUtils.hasElevation()) {
            final int diameter = getResources().getDimensionPixelSize(R.dimen.fab_size);
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(final View view, final Outline outline) {
                    outline.setOval(0, 0, diameter, diameter);
                }
            });

            setClipToOutline(true);
            setElevation(getResources().getDimension(R.dimen.fab_elevation));
        } else {
            ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, null);

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
    protected void onDraw(final Canvas canvas) {
        if (!CompatUtils.hasLollipop()) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() / 2f) - 10f,
                    mButtonPaint);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (CompatUtils.hasLollipop()) {
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
        if (CompatUtils.hasLollipop()) {
            super.setBackgroundColor(fabColor);
        } else {
            updatePreLBackgroundColor(fabColor);
            invalidate();
        }
    }
}