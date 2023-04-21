/*
Author : Yihui Wu
 */
package com.Frontend;

public class Log {
    private String firstName;
    private String lastName;
    private String project;
    private String startTime;
    private String endTime;
    private String lifeCycleStep;
    private String effortCategory;
    private String effortDetail;
    private String logDescription;
    private int employee;

    public Log(String firstName, String lastName, String project, String startTime, String endTime, String lifeCycleStep, String effortCategory, String effortDetail, String logDescription, int employee) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.project = project;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lifeCycleStep = lifeCycleStep;
        this.effortCategory = effortCategory;
        this.effortDetail = effortDetail;
        this.logDescription = logDescription;
        this.employee = employee;
    }

    public Log() {

    }

    // Getters and setters for all fields

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public int getEmployee() {
        return employee;
    }

    public void setEmployee(int employee) {
        this.employee = employee;
    }

    @Override
    public String toString() {
        return String.format("Log [\nemployee: %d \nfirstName: %s, \nlastName: %s, \nproject: %s, \nstartTime: %s, \nendTime: %s, \nlifeCycleStep: %s, \neffortCategory: %s, \neffortDetail: %s, \nlogDescription: %s \n]",
                employee, firstName, lastName, project, startTime, endTime, lifeCycleStep, effortCategory, effortDetail, logDescription);
    }
}

