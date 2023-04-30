package com.Frontend;

public class Defect {
    private String defectID;
    private String name;
    private String fixStatus;
    private String stepWhenInjected;
    private String stepWhenRemoved;
    private String defectCategory;
    private String description;

    public Defect(String defectID, String name, String fixStatus, String stepWhenInjected, String stepWhenRemoved, String defectCategory, String description) {
        this.defectID = defectID;
        this.name = name;
        this.fixStatus = fixStatus;
        this.stepWhenInjected = stepWhenInjected;
        this.stepWhenRemoved = stepWhenRemoved;
        this.defectCategory = defectCategory;
        this.description = description;
    }

    public String getDefectID() {
        return defectID;
    }

    public void setDefectID(String defectID) {
        this.defectID = defectID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFixStatus() {
        return fixStatus;
    }

    public void setFixStatus(String fixStatus) {
        this.fixStatus = fixStatus;
    }

    public String getStepWhenInjected() {
        return stepWhenInjected;
    }

    public void setStepWhenInjected(String stepWhenInjected) {
        this.stepWhenInjected = stepWhenInjected;
    }

    public String getStepWhenRemoved() {
        return stepWhenRemoved;
    }

    public void setStepWhenRemoved(String stepWhenRemoved) {
        this.stepWhenRemoved = stepWhenRemoved;
    }

    public String getDefectCategory() {
        return defectCategory;
    }

    public void setDefectCategory(String defectCategory) {
        this.defectCategory = defectCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}