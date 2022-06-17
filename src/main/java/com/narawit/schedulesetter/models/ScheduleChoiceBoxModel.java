package com.narawit.schedulesetter.models;

public class ScheduleChoiceBoxModel {
    private int scheduleTypeId;
    private String name;

    public ScheduleChoiceBoxModel() { }
    public ScheduleChoiceBoxModel(int id, String name){
        this.scheduleTypeId = id;
        this.name = name;
    }

    public int getScheduleTypeId(){ return scheduleTypeId; }
    public String getName() { return name; }
    public void setScheduleTypeId(int id){ scheduleTypeId = id; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString(){
        return this.name;
    }
}
