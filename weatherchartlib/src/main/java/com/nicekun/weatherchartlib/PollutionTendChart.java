package com.nicekun.weatherchartlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class PollutionTendChart extends View {
    private static final String TAG = PollutionTendChart.class.getCanonicalName();

    private Paint mAxisPaint;
    private Paint mLabelPaint;
    private Paint mDataLinePaint;
    private Paint mDataDotPaint;

    private int mAxisColor = Color.parseColor("#BABABA");
    private int mDataLineColor = Color.parseColor("#43BDCF");
    private int mGridGrayColor = Color.parseColor("#F1F1F1");
    private int mGridDarkColor = Color.parseColor("#DDDDDD");
    private int mLabelColor = Color.parseColor("#9A9A9A");

    private int mFreshLevel1Color = Color.parseColor("#43D23D");
    private int mFreshLevel2Color = Color.parseColor("#D5D300");
    private int mFreshLevel3Color = Color.parseColor("#FFA013");
    private int mFreshLevel4Color = Color.parseColor("#EC4443");
    private int mFreshLevel5Color = Color.parseColor("#9517AC");
    private int mFreshLevel6Color = Color.parseColor("#8F0000");

    private int mLabelInnerPadding = dp2px(2);
    private int mLabelOutterPadding = dp2px(8);
    private int mRightPadding = dp2px(12);
    private int mTopPadding = dp2px(12);

    private int mCavasWidth;
    private int mCavasHeight = dp2px(200);

    private int mPerDataDotGapWidth = dp2px(27);
    private int mIndicatorBarWidth = dp2px(10);

    private final int TRANSLATE_CHART_X_MIN = dp2px(20);
    private Integer mTranslateChartX;

    private int mOriginY;
    private int mOriginX;
    private int mDataAreaWidth;
    private int mDataAreaHeight;

    private PollutionTendChartData mData;

    public PollutionTendChart(Context context) {
        super(context);
        init(context);
    }

    public PollutionTendChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PollutionTendChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);

        mAxisPaint = new Paint();
        mAxisPaint.setColor(mAxisColor);
        mAxisPaint.setStrokeWidth(dp2px(1));
        mAxisPaint.setAntiAlias(true);


        mLabelPaint = new Paint();
        mLabelPaint.setColor(mLabelColor);
        mLabelPaint.setTextSize(sp2px(8));
        mLabelPaint.setAntiAlias(true);

        mDataLinePaint = new Paint();
        mDataLinePaint.setColor(mDataLineColor);
        mDataLinePaint.setStrokeWidth(dp2px(2));
        mDataLinePaint.setAntiAlias(true);
        mDataLinePaint.setStyle(Paint.Style.STROKE);

        mDataDotPaint = new Paint();
        mDataDotPaint.setColor(mDataLineColor);
        mDataDotPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidthSize = 800;
        int minHeightSize = 600;

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
//        Log.e(TAG,"mCavasWidth:" + mCavasWidth);

        Rect rect = measureText(mLabelPaint, "500");
//        Log.e(TAG, "rect.width=" + rect.width() + "; rect.height=" + rect.height());

        mOriginX = rect.width() + mLabelOutterPadding + mLabelInnerPadding + mIndicatorBarWidth;
        mOriginY = mCavasHeight - rect.height() - mLabelOutterPadding - mLabelInnerPadding;
        mDataAreaWidth = mCavasWidth - mRightPadding - rect.width() - mLabelOutterPadding - mLabelInnerPadding - mIndicatorBarWidth;
        mDataAreaHeight = mCavasHeight - mTopPadding - rect.height() - mLabelOutterPadding - mLabelInnerPadding;
//        Log.e(TAG, "originY:" + mOriginY + "; originX:" + mOriginX + "; dataAreaWidth:" + mDataAreaWidth + "; dataAreaHeight:" + mDataAreaHeight);

        setMeasuredDimension(mCavasWidth, mCavasHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        drawAxis(canvas);
        drawLineWithData(canvas);
    }


    private void drawLineWithData(Canvas canvas) {
        List<PollutionDataPoint> aqiData = mData.getPointList();
        PollutionTendChartData.DataType aqiType = mData.getDataType();

        int saveCount = canvas.saveLayer(mOriginX, mOriginY - mDataAreaHeight, mOriginX + mDataAreaWidth, mCavasHeight, mAxisPaint, Canvas.ALL_SAVE_FLAG);

        if (mDelta != null) {
            mTranslateChartX = mTranslateChartX + mDelta;
//            Log.e(TAG,"drawLineWithData:" + mTranslateChartX);
            if (mTranslateChartX > TRANSLATE_CHART_X_MIN) {
                mTranslateChartX = TRANSLATE_CHART_X_MIN;
            } else if (mTranslateChartX < mDataAreaWidth - mPerDataDotGapWidth * aqiData.size()) {
                mTranslateChartX = mDataAreaWidth - mPerDataDotGapWidth * aqiData.size();
            }
        } else if (mTranslateChartX == null) {
            mTranslateChartX = mDataAreaWidth - mPerDataDotGapWidth * aqiData.size();
        }

        canvas.translate(mTranslateChartX, 0);
        mDelta = null;

        canvas.drawLine(mOriginX - TRANSLATE_CHART_X_MIN, mOriginY, mOriginX + mPerDataDotGapWidth * aqiData.size(), mOriginY, mAxisPaint);
        Path dataPath = new Path();

        boolean exitsBreakPoint = true;
        for (int i = 0; i < aqiData.size(); i++) {
            int dx = mOriginX + i * mPerDataDotGapWidth;

            Integer aqi = aqiData.get(i).getAQI();
            if (aqi == null) {
                exitsBreakPoint = true;
            } else {
                int dy = mOriginY - mDataAreaHeight * aqiData.get(i).getAQI() / 500;
                if (exitsBreakPoint) {
                    dataPath.moveTo(dx, dy);
                } else {
                    dataPath.lineTo(dx, dy);
                }
                exitsBreakPoint = false;
            }

        }
        canvas.drawPath(dataPath, mDataLinePaint);


        for (int i = 0; i < aqiData.size(); i++) {
            int dx = mOriginX + i * mPerDataDotGapWidth;

            Integer aqi = aqiData.get(i).getAQI();


            if (i % 5 == 0) {
                mAxisPaint.setColor(mGridDarkColor);
                canvas.drawLine(dx, mOriginY, dx, mOriginY - mDataAreaHeight, mAxisPaint);
                mAxisPaint.setColor(mAxisColor);

                String formatTimeExpress = aqiData.get(i).getLabel();
                Rect timeRect = measureText(mLabelPaint, formatTimeExpress);
                canvas.drawText(formatTimeExpress, dx - timeRect.width() / 2, mOriginY + mLabelInnerPadding + timeRect.height(), mLabelPaint);

            }

            if (aqi != null) {
                int dy = mOriginY - mDataAreaHeight * aqi / 500;
                canvas.drawCircle(dx, dy, 8, mDataDotPaint);
                mDataDotPaint.setColor(Color.WHITE);
                canvas.drawCircle(dx, dy, 6, mDataDotPaint);
                mDataDotPaint.setColor(mDataLineColor);
            }

        }

        canvas.restoreToCount(saveCount);
    }

    private void drawAxis(Canvas canvas) {
        mAxisPaint.setStrokeWidth(mIndicatorBarWidth);
        mAxisPaint.setColor(mFreshLevel1Color);
        canvas.save();
        canvas.translate(-mIndicatorBarWidth / 2, 0);
        canvas.drawLine(mOriginX, mOriginY, mOriginX, mOriginY - mDataAreaHeight * 50 / 500, mAxisPaint);

        mAxisPaint.setColor(mFreshLevel2Color);
        canvas.drawLine(mOriginX, mOriginY - mDataAreaHeight * 50 / 500, mOriginX, mOriginY - mDataAreaHeight * 100 / 500, mAxisPaint);

        mAxisPaint.setColor(mFreshLevel3Color);
        canvas.drawLine(mOriginX, mOriginY - mDataAreaHeight * 100 / 500, mOriginX, mOriginY - mDataAreaHeight * 150 / 500, mAxisPaint);

        mAxisPaint.setColor(mFreshLevel4Color);
        canvas.drawLine(mOriginX, mOriginY - mDataAreaHeight * 150 / 500, mOriginX, mOriginY - mDataAreaHeight * 200 / 500, mAxisPaint);

        mAxisPaint.setColor(mFreshLevel5Color);
        canvas.drawLine(mOriginX, mOriginY - mDataAreaHeight * 200 / 500, mOriginX, mOriginY - mDataAreaHeight * 300 / 500, mAxisPaint);

        mAxisPaint.setColor(mFreshLevel6Color);
        canvas.drawLine(mOriginX, mOriginY - mDataAreaHeight * 300 / 500, mOriginX, mOriginY - mDataAreaHeight, mAxisPaint);

        Rect rect50 = measureText(mLabelPaint, "50");
        canvas.drawText("50", mOriginX - rect50.width() - mLabelInnerPadding - mIndicatorBarWidth, mOriginY - mDataAreaHeight * 50 / 500 + rect50.height() / 2, mLabelPaint);
        canvas.drawText("100", mOriginX - measureText(mLabelPaint, "100").width() - mLabelInnerPadding - mIndicatorBarWidth, mOriginY - mDataAreaHeight * 100 / 500 + rect50.height() / 2, mLabelPaint);
        canvas.drawText("150", mOriginX - measureText(mLabelPaint, "150").width() - mLabelInnerPadding - mIndicatorBarWidth, mOriginY - mDataAreaHeight * 150 / 500 + rect50.height() / 2, mLabelPaint);
        canvas.drawText("200", mOriginX - measureText(mLabelPaint, "200").width() - mLabelInnerPadding - mIndicatorBarWidth, mOriginY - mDataAreaHeight * 200 / 500 + rect50.height() / 2, mLabelPaint);
        canvas.drawText("300", mOriginX - measureText(mLabelPaint, "300").width() - mLabelInnerPadding - mIndicatorBarWidth, mOriginY - mDataAreaHeight * 300 / 500 + rect50.height() / 2, mLabelPaint);
        canvas.drawText("500", mOriginX - measureText(mLabelPaint, "500").width() - mLabelInnerPadding - mIndicatorBarWidth, mOriginY - mDataAreaHeight + rect50.height() / 2, mLabelPaint);
        canvas.restore();

        mAxisPaint.setColor(mGridGrayColor);
        canvas.drawRect(mOriginX, mOriginY - mDataAreaHeight * 100 / 500, mOriginX + mDataAreaWidth, mOriginY - mDataAreaHeight * 50 / 500, mAxisPaint);
        canvas.drawRect(mOriginX, mOriginY - mDataAreaHeight * 200 / 500, mOriginX + mDataAreaWidth, mOriginY - mDataAreaHeight * 150 / 500, mAxisPaint);
        canvas.drawRect(mOriginX, mOriginY - mDataAreaHeight, mOriginX + mDataAreaWidth, mOriginY - mDataAreaHeight * 300 / 500, mAxisPaint);

        mAxisPaint.setColor(mAxisColor);
        mAxisPaint.setStrokeWidth(dp2px(1));
    }

    private Rect measureText(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }

    public void setData(PollutionTendChartData data) {
        mData = data;
        invalidate();
    }


    private Float mPreX;
    private Integer mDelta;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();

                if (x > mOriginX && x < mOriginX + mDataAreaWidth && y > mOriginY - mDataAreaHeight && y < mOriginY) {
                    mPreX = x;
                    return true;
                } else {
                    return false;
                }

            case MotionEvent.ACTION_MOVE:
                if (mPreX != null) {
                    float thisX = event.getX();
                    mDelta = (int) (thisX - mPreX);
                    mPreX = thisX;
//                    Log.e(TAG, "mDelta:" + mDelta);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPreX = null;
                break;
        }
        return true;
    }


    private int dp2px(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    private int sp2px(int spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getResources().getDisplayMetrics());
    }

}
