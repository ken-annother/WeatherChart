package com.nicekun.weatherchartlib;

public class PollutionDataPoint {
    private Integer AQI;
    private String label;

    public PollutionDataPoint(Integer AQI, String label) {
        this.AQI = AQI;
        this.label = label;
    }

    public PollutionDataPoint() {

    }

    public Integer getAQI() {
        return AQI;
    }

    public void setAQI(Integer AQI) {
        this.AQI = AQI;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "PollutionDataPoint{" +
                "AQI=" + AQI +
                ", label='" + label + '\'' +
                '}';
    }
}
