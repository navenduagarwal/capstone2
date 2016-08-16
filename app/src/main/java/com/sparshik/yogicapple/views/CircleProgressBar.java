package com.sparshik.yogicapple.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.google.android.gms.cast.TextTrackStyle;

/**
 * Circular progress bar
 */

public class CircleProgressBar extends ProgressBar {
    private static final String TAG = "CircleProgressBar";
    private static final int TIME = 2000;
    private Bitmap canvasBitmap;
    private Paint cleanPaint;
    private Paint clearPaint;
    private int mAngle;
    private Rect mCanvasDistRect;
    private Rect mCanvasRect;
    private float mCenterX;
    private float mCenterY;
    private int mCurrentDegress;
    private long mCurrentTime;
    private int mGapDegress;
    private float mHeight;
    private int mInnerColor;
    private float mProgressRadius;
    private float mSize;
    private long mStartTime;
    private float mStartX;
    private float mStartY;
    private int mStrokeColor;
    private RectF mStrokeOval;
    private float mStrokeRadius;
    private float mStrokeWidth;
    private boolean mUseRing;
    private float mWidth;
    private Paint normalPaint;
    private Paint strokePaint;
    private Canvas tempCanvas;

    public CircleProgressBar(Context context) {
        super(context);
        this.mGapDegress = 30;
        this.mAngle = 360 - this.mGapDegress;
        this.mCurrentDegress = -1;
        this.mStartTime = -1;
        this.mUseRing = false;
        this.cleanPaint = new Paint();
        this.clearPaint = new Paint();
        this.normalPaint = new Paint();
        this.strokePaint = new Paint();
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mGapDegress = 30;
        this.mAngle = 360 - this.mGapDegress;
        this.mCurrentDegress = -1;
        this.mStartTime = -1;
        this.mUseRing = false;
        this.cleanPaint = new Paint();
        this.clearPaint = new Paint();
        this.normalPaint = new Paint();
        this.strokePaint = new Paint();
        setProperties(attrs);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mGapDegress = 30;
        this.mAngle = 360 - this.mGapDegress;
        this.mCurrentDegress = -1;
        this.mStartTime = -1;
        this.mUseRing = false;
        this.cleanPaint = new Paint();
        this.clearPaint = new Paint();
        this.normalPaint = new Paint();
        this.strokePaint = new Paint();
        setProperties(attrs);
    }

    private void setProperties(AttributeSet attrs) {
        setIndeterminate(false);
        String aStrokeWidth = attrs.getAttributeValue(null, "strokeWidth");
        String aStrokeColor = attrs.getAttributeValue(null, "strokeColor");
        String aInnerColor = attrs.getAttributeValue(null, "innerColor");
        if (aStrokeWidth != null) {
            int value = Integer.parseInt(aStrokeWidth.substring(0, aStrokeWidth.length() - 2));
            String unit = aStrokeWidth.substring(aStrokeWidth.length() - 2);
            int unitType = 0;
            if (unit.equals("dp")) {
                unitType = 1;
            } else if (unit.equals("sp")) {
                unitType = 2;
            } else if (unit.equals("pt")) {
                unitType = 3;
            } else if (unit.equals("mm")) {
                unitType = 5;
            }
            setStrokeWidth(TypedValue.applyDimension(unitType, (float) value, getContext().getResources().getDisplayMetrics()));
        } else {
            setStrokeWidth(TypedValue.applyDimension(1, 20.0f, getContext().getResources().getDisplayMetrics()));
        }
        if (aInnerColor != null) {
            setInnerColor(Color.parseColor(aInnerColor));
        } else {
            setInnerColor(-1879048192);
        }
        if (aStrokeColor != null) {
            setStrokeColor(Color.parseColor(aStrokeColor));
        } else {
            setStrokeColor(-1879048192);
        }
    }


    public void setStrokeColor(int color) {
        this.mStrokeColor = color;
        replaceColors();
        invalidate();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = (float) w;
        this.mHeight = (float) h;
        this.mSize = Math.min(this.mWidth, this.mHeight);
        this.mStartX = (this.mWidth - this.mSize) / 2.0f;
        this.mStartY = (this.mHeight - this.mSize) / 2.0f;
        this.mCenterX = this.mWidth / 2.0f;
        this.mCenterY = this.mHeight / 2.0f;
        this.mStrokeRadius = (Math.min(this.mWidth, this.mHeight) - this.mStrokeWidth) / 2.0f;
        this.mProgressRadius = ((Math.min(this.mWidth, this.mHeight) / 2.0f) - this.mStrokeWidth) + TextTrackStyle.DEFAULT_FONT_SCALE;
        this.mCanvasDistRect = new Rect((int) this.mStartX, (int) this.mStartY, (int) (this.mStartX + this.mSize), (int) (this.mStartY + this.mSize));
        this.canvasBitmap = null;
        this.mStrokeOval = new RectF(this.mStartX + (this.mStrokeWidth / 2.0f), this.mStartY + (this.mStrokeWidth / 2.0f), (this.mStartX + this.mSize) - (this.mStrokeWidth / 2.0f), (this.mStartY + this.mSize) - (this.mStrokeWidth / 2.0f));
    }

    protected synchronized void onDraw(Canvas canvas) {
        if (this.canvasBitmap == null) {
            redoCanvasBitmap(canvas);
            this.clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            replaceColors();
        }
        this.tempCanvas.drawPaint(this.clearPaint);
        this.tempCanvas.drawCircle(this.mCenterX, this.mCenterY, this.mProgressRadius, this.normalPaint);
        this.tempCanvas.drawRect(0.0f, 0.0f, this.mWidth, this.mStrokeWidth + ((this.mProgressRadius * ((100.0f - ((((float) getProgress()) * 100.0f) / ((float) getMax()))) / 100.0f)) * 2.0f), this.clearPaint);
        if (!this.mUseRing || getProgress() <= 0 || getProgress() >= 100) {
            this.tempCanvas.drawCircle(this.mCenterX, this.mCenterY, this.mStrokeRadius, this.strokePaint);
            canvas.drawBitmap(this.canvasBitmap, this.mCanvasRect, this.mCanvasDistRect, this.cleanPaint);
        } else {
            if (this.mStartTime == -1) {
                this.mStartTime = System.currentTimeMillis();
            }
            this.mCurrentTime = System.currentTimeMillis();
            while (this.mCurrentTime > this.mStartTime + 2000) {
                this.mStartTime += 2000;
            }
            this.mCurrentDegress = (((int) ((((double) (this.mCurrentTime - this.mStartTime)) / 2000.0d) * 360.0d)) - 90) + (this.mGapDegress / 2);
            this.tempCanvas.drawArc(this.mStrokeOval, (float) this.mCurrentDegress, (float) this.mAngle, false, this.strokePaint);
            this.tempCanvas.drawCircle(this.mCenterX + (this.mStrokeRadius * ((float) Math.cos(Math.toRadians((double) this.mCurrentDegress)))), this.mCenterY + (this.mStrokeRadius * ((float) Math.sin(Math.toRadians((double) this.mCurrentDegress)))), this.mStrokeWidth / 2.0f, this.normalPaint);
            this.tempCanvas.drawCircle(this.mCenterX + (this.mStrokeRadius * ((float) Math.cos(Math.toRadians((double) (this.mCurrentDegress + this.mAngle))))), this.mCenterY + (this.mStrokeRadius * ((float) Math.sin(Math.toRadians((double) (this.mCurrentDegress + this.mAngle))))), this.mStrokeWidth / 2.0f, this.normalPaint);
            canvas.drawBitmap(this.canvasBitmap, this.mCanvasRect, this.mCanvasDistRect, this.cleanPaint);
            invalidate();
        }
    }

    private void redoCanvasBitmap(Canvas canvas) {
        int tempSize = Math.min(canvas.getWidth(), canvas.getHeight());
        int scaleFactor = 1;
        boolean ok = false;
        while (!ok) {
            try {
                int finalSize = tempSize / scaleFactor;
                this.canvasBitmap = Bitmap.createBitmap(finalSize, finalSize, Bitmap.Config.ARGB_8888);
                this.mCanvasRect = new Rect(0, 0, finalSize, finalSize);
                ok = true;
            } catch (OutOfMemoryError e) {
                Log.e(TAG, "Could not allocate memory for progress bar", e);
                scaleFactor *= 2;
            }
        }
        this.tempCanvas = new Canvas(this.canvasBitmap);
    }

    private void replaceColors() {
        this.normalPaint.setColor(this.mInnerColor);
        this.normalPaint.setStyle(Paint.Style.FILL);
        this.normalPaint.setAntiAlias(true);
        this.strokePaint.setColor(this.mStrokeColor);
        this.strokePaint.setStyle(Paint.Style.STROKE);
        this.strokePaint.setStrokeWidth(this.mStrokeWidth);
        this.strokePaint.setAntiAlias(true);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!(this.canvasBitmap == null || this.canvasBitmap.isRecycled())) {
            this.canvasBitmap.recycle();
        }
        this.canvasBitmap = null;
    }

    public int getInnerColor() {
        return this.mInnerColor;
    }

    public void setInnerColor(int color) {
        this.mInnerColor = color;
        replaceColors();
        invalidate();
    }

    public float getStrokeWidth() {
        return this.mStrokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.mStrokeWidth = strokeWidth;
        replaceColors();
        invalidate();
    }

    public void setUseRing(boolean mUseRing) {
        this.mUseRing = mUseRing;
    }
}
