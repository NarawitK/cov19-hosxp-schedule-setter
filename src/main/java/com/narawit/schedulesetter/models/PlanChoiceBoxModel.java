package com.narawit.schedulesetter.models;

public class PlanChoiceBoxModel {
    private int planTypeId;
    private String name;

    public int getPlanTypeId(){return planTypeId;}
    public String getName(){return name;}
    public void setPlanTypeId(int value){planTypeId = value;}
    public void setName(String name){this.name = name;}

    public PlanChoiceBoxModel(int id, String name){
        this.planTypeId = id;
        this.name = name;
    }

    @Override
    public String toString(){
        return this.name;
    }
}