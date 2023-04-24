/*
Author : Yihui Wu
 */
package com.Frontend;

public class Log {
    private String project;
    private String date;
    private String startTime;
    private String endTime;
    private String lifeCycleStep;
    private String effortCategory;
    private String effortDetail;
    private String logDescription;

    public Log(String project, String date, String startTime, String endTime, String lifeCycleStep, String effortCategory, String effortDetail, String logDescription) {
        this.date = date;
        this.project = project;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lifeCycleStep = lifeCycleStep;
        this.effortCategory = effortCategory;
        this.effortDetail = effortDetail;
        this.logDescription = logDescription;
    }

    public Log() {

    }

    // Getters and setters for all fields


    public void setDate(String date) {
        this.date = date;
    }

    public String getDate(){
        return date;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLifeCycleStep() {
        return lifeCycleStep;
    }

    public void setLifeCycleStep(String lifeCycleStep) {
        this.lifeCycleStep = lifeCycleStep;
    }

    public String getEffortCategory() {
        return effortCategory;
    }

    public void setEffortCategory(String effortCategory) {
        this.effortCategory = effortCategory;
    }

    public String getEffortDetail() {
        return effortDetail;
    }

    public void setEffortDetail(String effortDetail) {
        this.effortDetail = effortDetail;
    }

    public String getLogDescription() {
        return logDescription;
    }

    public void setLogDescription(String logDescription) {
        this.logDescription = logDescription;
    }


    @Override
    public String toString() {
        return String.format("PROJECT: %s,   startTime: %s,   endTime: %s,   lifeCycleStep: %s,   effortCategory: %s,   effortDetail: %s",
                project, startTime, endTime, lifeCycleStep, effortCategory, effortDetail);
    }
}

