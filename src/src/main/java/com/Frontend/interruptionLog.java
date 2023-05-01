package com.Frontend;

public class interruptionLog {
    private String startTime;
    private String stopTime;
    private String duration;
    private String interruptionType;

    public interruptionLog(String startTime, String stopTime, String duration, String interruptionType) {
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.duration = duration;
        this.interruptionType = interruptionType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInterruptionTime() {
        return interruptionType;
    }

    public void setInterruptionTime(String interruptionTime) {
        this.interruptionType = interruptionTime;
    }

}
