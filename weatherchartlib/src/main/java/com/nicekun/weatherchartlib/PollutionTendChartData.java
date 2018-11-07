package com.nicekun.weatherchartlib;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PollutionTendChartData {
    private List<PollutionDataPoint> mPointList;
    private DataType mDataType;

    private PollutionTendChartData(PollutionTendChartDataBuilder builder) {
        mDataType = builder.mDataType;
        mPointList = builder.mPointList;
    }

    public List<PollutionDataPoint> getPointList() {
        return mPointList;
    }

    public void setPointList(List<PollutionDataPoint> pointList) {
        mPointList = pointList;
    }

    public DataType getDataType() {
        return mDataType;
    }

    public void setDataType(DataType dataType) {
        mDataType = dataType;
    }

    public enum DataType {
        HOUR,

        DAY
    }


    public static class PollutionTendChartDataBuilder {
        List<PollutionDataPoint> mPointList;
        DataType mDataType;
        Date mDate;

        private boolean mIsForcast = false;
        private Integer[] mAqiData;

        /**
         * 构造函数
         * @param aqiData 数据集,如果数据点不存在, 传入null
         * @param type 数据的类型 <br/>{@link DataType#DAY DataType.DAY} :每一天的采集数据;   <br/>{@link DataType#HOUR DataType.HOUR} :每一个小时采集的数据
         */
        public PollutionTendChartDataBuilder(Integer[] aqiData, DataType type) {
            this.mAqiData = aqiData;
            this.mDataType = type;
        }

        public PollutionTendChartData build() {
            if (mDataType == null) {
                throw new RuntimeException("datatype should not be null");
            }

            if (mAqiData == null || mAqiData.length == 0) {
                throw new RuntimeException("aqiData should not be null");
            }

            if (mDataType == DataType.HOUR) {
                mPointList = transferHourData();
            } else if (mDataType == DataType.DAY) {
                mPointList = transferDayData();
            }

            return new PollutionTendChartData(this);
        }

        private List<PollutionDataPoint> transferDayData() {
            ArrayList<PollutionDataPoint> pollutionDataPoints = new ArrayList<>();
            DateFormat monthDay = new SimpleDateFormat("MM-dd", Locale.getDefault());

            if (mIsForcast) {
                Calendar calendar = Calendar.getInstance();
                if (mDate != null) {
                    calendar.setTime(mDate);
                }

                for (Integer eAqiData : mAqiData) {
                    String label = monthDay.format(calendar.getTime());
                    pollutionDataPoints.add(new PollutionDataPoint(eAqiData, label));
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
            } else {

                Calendar calendar = Calendar.getInstance();
                if (mDate != null) {
                    calendar.setTime(mDate);
                } else {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                }

                for (int i = mAqiData.length - 1; i >= 0; i--) {
                    Integer eAqiData = mAqiData[i];
                    String label = monthDay.format(calendar.getTime());
                    pollutionDataPoints.add(0, new PollutionDataPoint(eAqiData, label));
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                }
            }

            return pollutionDataPoints;
        }

        private List<PollutionDataPoint> transferHourData() {
            ArrayList<PollutionDataPoint> pollutionDataPoints = new ArrayList<>();

            if (mIsForcast) {
                Calendar calendar = Calendar.getInstance();
                int dayCount = 0;
                if (mDate != null) {
                    calendar.setTime(mDate);
                }

                Integer preHour = null;
                for (Integer eAqiData : mAqiData) {
                    int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
                    if (preHour != null && nowHour < preHour) {
                        dayCount++;
                    }
                    preHour = nowHour;
                    String label = String.valueOf(nowHour) + (dayCount == 0 ? "" : "(+" + dayCount + ")");
                    pollutionDataPoints.add(new PollutionDataPoint(eAqiData, label));
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                }

            } else {

                Calendar calendar = Calendar.getInstance();
                int dayCount = 0;

                if (mDate != null) {
                    calendar.setTime(mDate);
                } else {
                    calendar.add(Calendar.HOUR_OF_DAY, -1);
                }

                Integer preHour = null;
                for (int i = mAqiData.length - 1; i >= 0; i--) {
                    Integer eAqiData = mAqiData[i];
                    int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
                    if (preHour != null && nowHour > preHour) {
                        dayCount++;
                    }

                    preHour = nowHour;

                    String label = String.valueOf(nowHour) + (dayCount == 0 ? "" : "(-" + dayCount + ")");
                    pollutionDataPoints.add(0, new PollutionDataPoint(eAqiData, label));
                    calendar.add(Calendar.HOUR_OF_DAY, -1);
                }
            }

            return pollutionDataPoints;
        }

        /**
         * 如果是预报AQI, 请给出数据集的第一个点的date
         * @param time
         * @return
         */
        public PollutionTendChartDataBuilder setForcastFirstPointDate(Date time) {
            this.mIsForcast = true;
            mDate = time;
            return this;
        }


        /**
         * 如果是历史的AQI记录, 给出最后一个点的date
         * @param time
         * @return
         */
        public PollutionTendChartDataBuilder setHistoryLastPointDate(Date time) {
            this.mIsForcast = false;
            mDate = time;
            return this;
        }
    }
}
