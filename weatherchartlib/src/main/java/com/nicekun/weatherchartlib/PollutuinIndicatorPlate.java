package com.nicekun.weatherchartlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class PollutuinIndicatorPlate extends View {

    public static final String TAG = PollutuinIndicatorPlate.class.getCanonicalName();

    private static final int GAP_ANGLE = 90;
    private static final int MAX_EXPRESS_STRING_LENGTH = 4;

    private int mCavasWidth;
    private int mCavasHeight;

    private int mFreshLevel1Color = Color.parseColor("#43D23D");
    private int mFreshLevel2Color = Color.parseColor("#D5D300");
    private int mFreshLevel3Color = Color.parseColor("#FFA013");
    private int mFreshLevel4Color = Color.parseColor("#EC4443");
    private int mFreshLevel5Color = Color.parseColor("#9517AC");
    private int mFreshLevel6Color = Color.parseColor("#8F0000");

    private int mLabelColor = Color.parseColor("#333333");

    private int mArcWidth = dp2px(16);
    private int mAllPadding = dp2px(40);
    private int mExpressMargin = dp2px(16);
    private int mIndicatorMargin = dp2px(2);

    private float mDiameter;

    private RectF mArcRect = new RectF();
    private Paint mProgressPaint;
    private Paint mLabelPaint;
    private Paint mIndicatorPaint;
    private int mAQI;
    private float mDeltaX;
    private float mDeltaY;

    public PollutuinIndicatorPlate(Context context) {
        super(context);
        init(context);
    }

    public PollutuinIndicatorPlate(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PollutuinIndicatorPlate(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mProgressPaint = new Paint();
        mProgressPaint.setStrokeWidth(mArcWidth);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);

        mLabelPaint = new Paint();
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setColor(mLabelColor);

        mIndicatorPaint = new Paint();
        mIndicatorPaint.setColor(mLabelColor);
        mIndicatorPaint.setAntiAlias(true);
        mIndicatorPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidthSize = 800;
        int minHeightSize = 800;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            if (widthSize < minWidthSize) {
                mCavasWidth = minWidthSize;
            } else {
                mCavasWidth = widthSize;
            }
        } else {
            mCavasWidth = minWidthSize;
        }

        mCavasWidth = minWidthSize;
        mCavasHeight = minHeightSize;

        mArcRect.top = mArcWidth / 2  + mAllPadding;
        mArcRect.left = mArcWidth / 2 + mAllPadding ;
        mArcRect.right = mCavasWidth - mArcWidth / 2  - mAllPadding;
        mArcRect.bottom = mCavasHeight - mArcWidth / 2 - mAllPadding;
        mDiameter =  mArcRect.right - mArcRect.left;

        double halfAngle = (GAP_ANGLE / 2 * Math.PI )/ 180;
        mDeltaX = (float) Math.sin(halfAngle) * mDiameter / 2;
        mDeltaY = (float) Math.cos(halfAngle) * mDiameter / 2;
        setMeasuredDimension(minWidthSize, minHeightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        canvas.save();

        canvas.rotate(90 + GAP_ANGLE / 2, mCavasWidth / 2, mCavasHeight / 2);

        mProgressPaint.setColor(mFreshLevel6Color);
        canvas.drawArc(mArcRect,0,  (360 - GAP_ANGLE), false, mProgressPaint);

        mProgressPaint.setStrokeCap(Paint.Cap.BUTT);
        mProgressPaint.setColor(mFreshLevel5Color);
        canvas.drawArc(mArcRect,0, (360 - GAP_ANGLE) * 300 / 500, false, mProgressPaint);

        mProgressPaint.setColor(mFreshLevel4Color);
        canvas.drawArc(mArcRect,0, (360 - GAP_ANGLE) * 200 / 500, false, mProgressPaint);

        mProgressPaint.setColor(mFreshLevel3Color);
        canvas.drawArc(mArcRect,0, (360 - GAP_ANGLE) * 150 / 500, false, mProgressPaint);

        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setColor(mFreshLevel1Color);
        canvas.drawArc(mArcRect,0, (360 - GAP_ANGLE) * 50 / 500, false, mProgressPaint);

        mProgressPaint.setStrokeCap(Paint.Cap.BUTT);
        mProgressPaint.setColor(mFreshLevel2Color);
        canvas.drawArc(mArcRect,(360 - GAP_ANGLE) * 50 / 500, (360 - GAP_ANGLE) * (100 - 50) / 500, false, mProgressPaint);

        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);

        canvas.restore();

        drawTextTips(canvas);

        drawIndicator(canvas);
    }

    private void drawIndicator(Canvas canvas) {
        RectF rectF = new RectF();
        rectF.top = 0;
        rectF.left = 0;
        rectF.right = mCavasWidth;
        rectF.bottom = mCavasHeight;
        int layerCount = canvas.saveLayer(rectF, mIndicatorPaint, Canvas.ALL_SAVE_FLAG);

        float deltaAngleShouldRotate = (mAQI - 500 / 2) * (360 - GAP_ANGLE) / 500 ;
        canvas.rotate(deltaAngleShouldRotate, mCavasWidth / 2 , mCavasHeight / 2);

        float centerX = (mArcRect.right + mArcRect.left) / 2;
        float centerY = (mArcRect.top + mArcRect.bottom) / 2;

        Path upArrowPath = new Path();
        upArrowPath.moveTo(centerX, centerY - mDiameter / 2 - mArcWidth / 2 - mIndicatorMargin);
        upArrowPath.lineTo(centerX + mArcWidth / 2, centerY - mDiameter / 2  - mIndicatorMargin - mArcWidth / 2 - mArcWidth);
        upArrowPath.lineTo(centerX, centerY - mDiameter / 2 - mArcWidth / 2 - mIndicatorMargin - mArcWidth * 3 / 4);
        upArrowPath.lineTo(centerX - mArcWidth / 2, centerY - mDiameter / 2 - mIndicatorMargin - mArcWidth / 2 - mArcWidth);
        upArrowPath.close();
        canvas.drawPath(upArrowPath, mIndicatorPaint);

        Path downArrowPath = new Path();
        downArrowPath.moveTo(centerX, centerY - mDiameter / 2 + mArcWidth / 2 + mIndicatorMargin);
        downArrowPath.lineTo(centerX + mArcWidth / 2, centerY - mDiameter / 2 + mArcWidth / 2 + mIndicatorMargin + mArcWidth);
        downArrowPath.lineTo(centerX, centerY - mDiameter / 2 + mArcWidth / 2 + mIndicatorMargin + mArcWidth * 3 / 4);
        downArrowPath.lineTo(centerX - mArcWidth / 2, centerY - mDiameter / 2 + mArcWidth / 2 + mIndicatorMargin + mArcWidth);
        downArrowPath.close();
        canvas.drawPath(downArrowPath, mIndicatorPaint);

        canvas.restoreToCount(layerCount);
    }

    private void drawTextTips(Canvas canvas) {
        float centerX = (mArcRect.right + mArcRect.left) / 2;
        float centerY = (mArcRect.top + mArcRect.bottom) / 2;

        String degreeExpress = evaluatePollutionDegree(mAQI);
        float expressTextSize =(mDeltaX * 2  - mExpressMargin * 2 )/ MAX_EXPRESS_STRING_LENGTH ;
        mLabelPaint.setTextSize(expressTextSize);
        canvas.drawText(degreeExpress,centerX - mDeltaX + mExpressMargin,centerY + mDeltaY, mLabelPaint);

        mLabelPaint.setTextSize(expressTextSize * 3 / 2);
        Rect rectNumber = measureText(mLabelPaint, mAQI + "");
        canvas.drawText(mAQI + "", centerX - rectNumber.width() / 2, centerY + rectNumber.height() / 2, mLabelPaint);

        mLabelPaint.setTextSize(expressTextSize * 2 / 3);
        canvas.drawText("AQI",centerX + rectNumber.width() / 2 + sp2px(4),centerY + rectNumber.height() / 2, mLabelPaint);
    }


    private int dp2px(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    private int sp2px(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
    }


    private String evaluatePollutionDegree(int aqi){
        if(aqi < 50){
            return "优";
        }else if(aqi < 100){
            return "良";
        }else if(aqi < 150){
            return "轻度污染";
        }else if(aqi < 200){
            return "中度污染";
        }else if(aqi < 300){
            return "重度污染";
        }else {
            return "严重污染";
        }
    }

    private Rect measureText(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    public void setAQI(int AQI) {
        mAQI = AQI;
        invalidate();
    }

    public int getAQI(){
        return mAQI;
    }
}
