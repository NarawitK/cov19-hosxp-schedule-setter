package com.narawit.schedulesetter.models;

public class TreatmentPlanModel {
    private int planTypeId;
    private int planScheduleId;
    private String name;
    private String description;
    private int day;

    public TreatmentPlanModel(){ }
    public TreatmentPlanModel(int planTypeId, int planScheduleId, String planName, String description, int day){
        this.planTypeId = planTypeId;
        this.planScheduleId = planScheduleId;
        this.name = planName;
        this.description = description;
        this.day = day;
    }

    public int getPlanTypeId(){ return planTypeId; }
    public void setPlanTypeId(int value){
        planTypeId = value;
    }
    public int getPlanScheduleId(){ return planScheduleId; }
    public void setPlanScheduleId(int value) { planScheduleId = value;}
    public String getName(){ return this.name; }
    public void setName(String name){
        this.name = name;
    }
    public String getDescription(){ return this.description; }
    public void setDescription(String description){ this.description = description; }
    public int getDay(){ return day; }
    public void setDay(int value) { day = value; }

    @Override
    public String toString(){
        return name;
    }
}