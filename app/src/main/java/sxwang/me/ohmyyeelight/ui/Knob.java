package sxwang.me.ohmyyeelight.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import sxwang.me.ohmyyeelight.R;
import sxwang.me.ohmyyeelight.Utils;

/**
 * Created by Shaoxing on 22/04/2017.
 */

public class Knob extends View {

    private static final String TAG = "Knob";

    private float mRadius;
    private float mDegree;
    @ColorInt
    private int mAccentColor;
    private Paint mPaint;
    private RectF mIndicatorRectF;
    private LinearGradient mGradient;

    private PointF mLastTouchPoint;
    private float mLastTouchDegree;
    private VelocityTracker mVelocityTracker;
    private ViewConfiguration mViewConfiguration;

    private OnTwistListener mOnTwistListener;

    public Knob(Context context) {
        this(context, null);
    }

    public Knob(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Knob(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorRectF = new RectF(0, 0, 12, 64);
        mLastTouchPoint = new PointF();
        mGradient = new LinearGradient(0, 0, 0, 0,
                new int[]{Color.parseColor("#FFFFFF"), Color.parseColor("#DDDDDD")},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        mViewConfiguration = ViewConfiguration.get(context);
        TypedValue v = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.colorAccent, v, true)) {
            mAccentColor = v.data;
        }

        // to draw shadow effect, disable hardware acceleration
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Knob(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        if (wMode == MeasureSpec.EXACTLY) {
            if (hMode == MeasureSpec.AT_MOST) {
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Math.min(wSize, hSize), MeasureSpec.EXACTLY));
                return;
            }
        } else if (wMode == MeasureSpec.AT_MOST) {
            if (hMode == MeasureSpec.EXACTLY) {
                setMeasuredDimension(Math.max(getSuggestedMinimumWidth(), Math.min(wSize, hSize)), getMeasuredHeight());
                super.onMeasure(MeasureSpec.makeMeasureSpec(Math.min(wSize, hSize), MeasureSpec.EXACTLY), heightMeasureSpec);
                return;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mGradient = new LinearGradient(0, 0, 0, h,
                new int[]{Color.parseColor("#464646"), Color.parseColor("#161616")},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float size = Math.min(canvas.getWidth(), canvas.getHeight());
        mRadius = size * 2 / 5;

        mPaint.setShader(mGradient);
        mPaint.setShadowLayer(8, 0, 4, Color.argb(127, 0, 0, 0));
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, mRadius, mPaint);

        mPaint.setShader(null);
        mPaint.clearShadowLayer();
        mPaint.setColor(mAccentColor);
        mPaint.setStyle(Paint.Style.FILL);

        mIndicatorRectF.offsetTo(canvas.getWidth() / 2 - mIndicatorRectF.width() / 2, canvas.getHeight() / 2 - mRadius + 24);

        canvas.save();
        canvas.rotate(mDegree, canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.drawRoundRect(mIndicatorRectF, mIndicatorRectF.width() / 2, mIndicatorRectF.width() / 2, mPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float dx = event.getX() - getWidth() / 2, dy = event.getY() - getHeight() / 2;
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN && Utils.squareSum(dx, dy) > mRadius * mRadius) {
            return false;
        }
        getParent().requestDisallowInterceptTouchEvent(true);

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.clear();
        }
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(event);
        }

        // knob center based coordinate
        double x = event.getX() - getWidth() / 2, y = event.getY() - getHeight() / 2;
        float degree = (float) Math.toDegrees(Math.atan2(y, x));

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchPoint.set(event.getX(), event.getY());
                mLastTouchDegree = degree;
                break;
            case MotionEvent.ACTION_MOVE:
                float twisted = degree - mLastTouchDegree;
//                Log.d(TAG, "twisted: " + (twisted));
                mDegree = (mDegree + twisted) % 360;
                mLastTouchPoint.set(event.getX(), event.getY());
                mLastTouchDegree = degree;
                invalidate();

                mVelocityTracker.computeCurrentVelocity(1);
                if (Math.hypot(mVelocityTracker.getXVelocity(), mVelocityTracker.getYVelocity()) <= mViewConfiguration.getScaledMinimumFlingVelocity() && mOnTwistListener != null) {
                    mOnTwistListener.onTwist(this, twisted);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mVelocityTracker != null) {
                    mVelocityTracker.clear();
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
        }
        return true;
    }

    public OnTwistListener getOnTwistListener() {
        return mOnTwistListener;
    }

    public void setOnTwistListener(OnTwistListener onTwistListener) {
        mOnTwistListener = onTwistListener;
    }

    interface OnTwistListener {
        void onTwist(Knob knob, float degrees);
    }
}
