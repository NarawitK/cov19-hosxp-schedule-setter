package com.narawit.schedulesetter.controllers;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import com.narawit.schedulesetter.Main;
import com.narawit.schedulesetter.helper.DbProperties;
import com.narawit.schedulesetter.models.PlanChoiceBoxModel;
import com.narawit.schedulesetter.models.ScheduleChoiceBoxModel;
import com.narawit.schedulesetter.viewmodels.MainPageViewModel;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.util.converter.IntegerStringConverter;


public class MainPageController implements Initializable {

    private final MainPageViewModel viewModel;
    private final String datePattern = "dd MMMM yyyy";

    @FXML
    public ChoiceBox<PlanChoiceBoxModel> MainPlanCb;
    @FXML
    public ChoiceBox<ScheduleChoiceBoxModel> SubPlanCb;
    @FXML
    public DatePicker StartDateDp;
    @FXML
    public DatePicker EndDateDp;
    @FXML
    public Button StartBtn;
    @FXML
    public Label ProgLbl;
    @FXML
    public Label ProgText;
    @FXML
    public ProgressBar ProgBar;
    @FXML
    public TextField DateDiff;
    
    public MainPageViewModel getViewModel() { return viewModel; }
    
    public MainPageController(){
        viewModel = new MainPageViewModel();
    }
    
	public void initialize(URL location, ResourceBundle resources) {
        SetIntegerTextFieldFormatter(DateDiff);
		InitBiDirectionalDataBinding();
        InitEndDatePicker();
        setDatePickerDateFormat(StartDateDp, datePattern);
        setDatePickerDateFormat(EndDateDp, datePattern);
        AddPlanChoiceBoxChangeListener(MainPlanCb);
        AddSubChoiceBoxChangeListener(SubPlanCb);
        try {
        	DbProperties.WriteDefaultDbProperties();
            setUpMainChoiceBox(true);
        }
        catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Network Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        catch(IOException ie) {
        	Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Properties Error");
            alert.setContentText(ie.getMessage());
            alert.showAndWait();
        }
	}

    private void setUpMainChoiceBox(boolean shouldSelectFirst) throws SQLException {
        getViewModel().PopulateMainChoiceBox();
        MainPlanCb.setItems(getViewModel().getMainCbList());
        if(shouldSelectFirst){
            MainPlanCb.getSelectionModel().selectFirst();
        }
    }
    private void setUpSubChoiceBox(boolean shouldSelectFirst){
        SubPlanCb.setItems(getViewModel().getSubCbList());
        if(shouldSelectFirst){
            SubPlanCb.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void switchToSecondary() throws IOException {
        Main.setRoot("secondary");
    }

    @FXML
    protected void onUpdateScheduleBtnClicked(){
        setProgressPartVisibility(true);
        ProgBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        StringBuilder sb = new StringBuilder();
        try {
            if(getViewModel().onUpdateButtonClicked()){
                ProgBar.setProgress(1);
                ProgBar.setStyle("-fx-accent: green");
                sb.append("Update Schedule Date successfully.\nPlanType: ");
                sb.append(MainPlanCb.getSelectionModel().getSelectedItem().getName());
                sb.append("\nDose: ");
                sb.append(SubPlanCb.getSelectionModel().getSelectedItem().getName());
                sb.append("\nAppointment Date: ");
                sb.append(EndDateDp.getValue());
                sb.append("\nDay(s) Count: ");
                sb.append(DateDiff.getText());
                setUpMainChoiceBox(false);
                setUpSubChoiceBox(false);
                GenerateDialog(AlertType.INFORMATION, "Update Complete", sb.toString());
            }
            else{
                ProgBar.setProgress(1);
                ProgBar.setStyle("-fx-accent: yellow");
                GenerateDialog(AlertType.WARNING, "Update Complete", "Nothing has been changed.");
            }
        }
        catch(SQLException e) {
            ProgBar.setProgress(1);
            ProgBar.setStyle("-fx-accent: red");
        	GenerateDialog(AlertType.ERROR, "SQL Error", e.getMessage());
        }
        finally {
            setProgressPartVisibility(false);
            ProgBar.setProgress(0);
        }
    }

    private void setProgressPartVisibility(boolean isVisible){
        if(isVisible && !ProgLbl.isVisible() && !ProgText.isVisible() && !ProgBar.isVisible()){
            ProgLbl.setVisible(true);
            ProgText.setVisible(true);
            ProgBar.setVisible(true);
        }
        else{
            ProgLbl.setVisible(isVisible);
            ProgText.setVisible(isVisible);
            ProgBar.setVisible(isVisible);
        }
    }
    @FXML
    protected void onDateSelected(ActionEvent e){
        viewModel.calcAndSetDateDiff();
    }
    @FXML
    protected void onDayCountTextReturnPressed(){
        boolean isEmpty = DateDiff.getText().isEmpty();
        getViewModel().setDateDiff(DateDiff.getText());
        getViewModel().getEndDate();
        if(isEmpty)
            DateDiff.positionCaret(DateDiff.getText().length());
    }

    protected void AddPlanChoiceBoxChangeListener(ChoiceBox<PlanChoiceBoxModel> cb){
        cb.getSelectionModel().selectedItemProperty().addListener(
                (elem, old, newValue) -> {
                	if(newValue != null) {
                        int planId = newValue.getPlanTypeId();
                		getViewModel().onFirstBoxSelected(planId);
                		setUpSubChoiceBox(true);
                	}
                }
        );
    }
    protected void AddSubChoiceBoxChangeListener(ChoiceBox<ScheduleChoiceBoxModel> cb){
        cb.getSelectionModel().selectedItemProperty().addListener(
                (elem, old, newValue) -> {
                	if(newValue != null) {
                        int scheduleTypeId = newValue.getScheduleTypeId();
                        int planDay = getViewModel().getPlanDayByPlanId(scheduleTypeId);
                		getViewModel().onSecondChoiceBoxSelected(scheduleTypeId);
                        getViewModel().setDateDiff(String.valueOf(planDay));
                        getViewModel().getEndDate();
                	}
                }
        );
    }

    private void GenerateDialog(AlertType t, String titleMessage, String contentMessage) {
    	Alert a = new Alert(t);
    	a.setTitle(titleMessage);
    	a.setContentText(contentMessage);
    	a.showAndWait();
    }

    private void SetIntegerTextFieldFormatter(TextField tf){
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([1-9][0-9]*)?")) {
                return change;
            }
            return null;
        };
        tf.setTextFormatter(
                new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
    }
    private void setDatePickerDateFormat(DatePicker datePicker, String pattern) {
    	StringConverter<LocalDate> converter = new StringConverter<>() {
            DateTimeFormatter dateFormatter = 
                DateTimeFormatter.ofPattern(pattern);
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        datePicker.setConverter(converter);
        datePicker.setPromptText(pattern.toLowerCase());
    	
    }

    private void InitEndDatePicker(){
        final Callback<DatePicker, DateCell> endDateCellFactory =
                new Callback<>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item.isBefore(viewModel.getStartDate().plusDays(getViewModel().DateDiffStartRange))) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #c0d6e4;");
                                }
                            }
                        };
                    }
                };
        EndDateDp.setDayCellFactory(endDateCellFactory);
    }
    
    private void InitBiDirectionalDataBinding(){
        Bindings.bindBidirectional(StartDateDp.valueProperty(), viewModel.startDateProperty());
        Bindings.bindBidirectional(EndDateDp.valueProperty(), viewModel.endDateProperty());
        Bindings.bindBidirectional(DateDiff.textProperty(), viewModel.dateDiffProperty());
    }
    
    @FXML
    protected void onPrefMenuItemPressed() {
    	try {
    		String stagePath = "settingspage/Settings";
    		Stage stage = Main.createStage(stagePath, "App Settings", 400, 350);
    		stage.show();
    	}
    	catch (IOException ioe) {
			this.GenerateDialog(AlertType.ERROR, "Application Error", ioe.getMessage());
		}
    }
    
    @FXML
    protected void onQuitMenuItemPressed() {
    	Main.terminate();
    }
}