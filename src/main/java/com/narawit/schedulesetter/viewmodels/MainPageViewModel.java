package com.narawit.schedulesetter.viewmodels;

import com.narawit.schedulesetter.helper.DateCalculator;
import com.narawit.schedulesetter.models.PlanChoiceBoxModel;
import com.narawit.schedulesetter.models.ResultModel;
import com.narawit.schedulesetter.models.ScheduleChoiceBoxModel;
import com.narawit.schedulesetter.models.TreatmentPlanModel;
import com.narawit.schedulesetter.services.DataQuery;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

import java.sql.SQLException;

public class MainPageViewModel {
    public static final int DateDiffStartRange = 21;
    private ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<LocalDate>(LocalDate.now());
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<LocalDate>(LocalDate.now().plusDays(DateDiffStartRange));
    private DoubleProperty progress = new SimpleDoubleProperty(0);
    private StringProperty dateDiff = new SimpleStringProperty();
    private StringProperty statusMessage = new SimpleStringProperty("Ready");
    private List<TreatmentPlanModel> dataList;
    private ObservableList<PlanChoiceBoxModel> mainCbList;
    private ObservableList<ScheduleChoiceBoxModel> subCbList;
    private ResultModel result = new ResultModel();

    public int getPlanDayByPlanId(int planId){
        if(!this.dataList.isEmpty()){
            for (TreatmentPlanModel elem: dataList)
            {
                if(elem.getPlanScheduleId() == planId){
                    return elem.getDay();
                }
            }
        }
        return DateDiffStartRange;
    }
    public ObservableList<PlanChoiceBoxModel> getMainCbList(){ return mainCbList; }
    public ObservableList<ScheduleChoiceBoxModel> getSubCbList(){ return subCbList; }
    public ObjectProperty<LocalDate> startDateProperty(){ return startDate; }
    public ObjectProperty<LocalDate> endDateProperty(){ return endDate; }
    public DoubleProperty progressProperty(){ return progress; }
    public StringProperty dateDiffProperty(){ return dateDiff; }
    public StringProperty statusMessageProperty(){ return statusMessage; }

    public final String getDateDiff(){
        return dateDiff.get();
    }
    public final void setDateDiff(String value) {
        if(value != "" &&  Long.parseLong(value) > 0){
            dateDiff.set(value);
            setEndDate(getStartDate().plusDays(Long.parseLong(value)));
        }
        else{
            setEndDate(getStartDate().plusDays(DateDiffStartRange));
            dateDiff.set(String.valueOf(DateDiffStartRange));
        }
    }
    public final LocalDate getStartDate(){
        return startDate.get();
    }
    public final void setStartDate(LocalDate date){
        startDate.set(date);
    }
    public final LocalDate getEndDate(){
        return endDate.get();
    }
    public final void setEndDate(LocalDate date){ endDate.set(date); }
    public final double getProgress(){
        return progress.get();
    }
    public final void setProgress(double value) { progress.set(value); }
    public final String getStatusMessage(){
        return statusMessage.get();
    }
    public final void setStatusMessage(String message){ statusMessage.set(message); }

    public MainPageViewModel(){
        calcAndSetDateDiff();
    }

    public void PopulateMainChoiceBox() throws SQLException{
        this.dataList = DataQuery.getPlanData();
        Set<PlanChoiceBoxModel> cbl = new LinkedHashSet<>();
        for (TreatmentPlanModel elem: dataList)
        {
            cbl.add(new PlanChoiceBoxModel(elem.getPlanTypeId(), elem.getName()));
        }
        this.mainCbList = FXCollections.observableList(cbl.stream().toList());
    }

    public void onFirstBoxSelected(int selectedPlanId){
        result.setPlanTypeId(selectedPlanId);
        PopulateSecondChoiceBox(this.dataList, selectedPlanId);
    }
    
    public void onSecondChoiceBoxSelected(int selectedScheduleTypeId) {
    	result.setPlanScheduleId(selectedScheduleTypeId);
    }
    
    public boolean onUpdateButtonClicked() throws SQLException {
    	return this.persistScheduleChanged(result);
    }

    private void PopulateSecondChoiceBox(List<TreatmentPlanModel> dataList, int selectedPlanTypeId){
        List<TreatmentPlanModel> scheduleFilteredList = dataList.stream().filter(e -> e.getPlanTypeId() == selectedPlanTypeId)
                .collect(Collectors.toList());
        List<ScheduleChoiceBoxModel> scbdl = new ArrayList<>();
        for (TreatmentPlanModel elem: scheduleFilteredList)
        {
            scbdl.add(new ScheduleChoiceBoxModel(elem.getPlanScheduleId(), elem.getDescription()));
        }
        this.subCbList = FXCollections.observableList(scbdl);
    }
    
    public boolean persistScheduleChanged(ResultModel r) throws SQLException{
        return DataQuery.updatePlanDate(r);
    }
    
    public void calcAndSetDateDiff(){
        ValidateEndDate();
        Integer day = DateCalculator.CalculateDateDiff(getStartDate(), getEndDate());
        setDateDiff(day.toString());
        setDayCountToResult(day);
    }

    private void setDayCountToResult(int day){
        result.setDay(day);
    }
    
    public void ValidateEndDate(){
        if(getEndDate().isBefore(getStartDate()) || getEndDate().isEqual(getStartDate())){
            setEndDate(getStartDate().plusDays(DateDiffStartRange));
            setDateDiff(String.valueOf(DateDiffStartRange));
        }
    }
}
