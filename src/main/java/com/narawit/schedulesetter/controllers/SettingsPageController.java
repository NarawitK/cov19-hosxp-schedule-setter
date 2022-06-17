package com.narawit.schedulesetter.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

import com.narawit.schedulesetter.helper.DbProperties;

public class SettingsPageController implements Initializable {
    @FXML
    public TextField addressField;
    @FXML
    public TextField portField;
    @FXML
    public TextField dbField;
    @FXML
    public TextField userNameField;
    @FXML
    public PasswordField pwdField;
    @FXML
    public CheckBox useInternetCb;
    @FXML
    public Button saveBtn;
    @FXML
    public Button closeBtn;

    public SettingsPageController(){ }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        setTextProperties();
    }

    private void setTextProperties(){
        try{
            Map<String,String> dbPropsMap = getDbPropertiesAsMap();
            addressField.setText(dbPropsMap.get("address"));
            portField.setText(dbPropsMap.get("port"));
            dbField.setText(dbPropsMap.get("database"));
            userNameField.setText(dbPropsMap.get("username"));
            pwdField.setText(dbPropsMap.get("password"));
            useInternetCb.setSelected(Boolean.parseBoolean(dbPropsMap.get("useInternet")));
        }
        catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }
    private Map<String, String> getDbPropertiesAsMap() throws IOException{
        Properties props = new Properties();
        Map<String, String> map = new HashMap<>();
        FileInputStream fStream = DbProperties.GetDbPropertiesAsInputStream();
        props.load(fStream);
        map.put("address", props.getProperty("INTERNAL_DB_URL"));
        map.put("port", props.getProperty("DB_PORT"));
        map.put("database", props.getProperty("DB_NAME"));
        map.put("username", props.getProperty("DB_USERNAME"));
        map.put("password", props.getProperty("DB_PASSWORD"));
        map.put("useInternet", props.getProperty("USE_EXTERNAL_DATASOURCE"));
        fStream.close();
        return map;
    }

    @FXML
    private void closeScene(){
        closeBtn.getScene().getWindow().hide();
    }
    
    @FXML
    private void saveProperties(){
        Properties props = new Properties();
        try{
            props.setProperty("INTERNAL_DB_URL", addressField.getText());
            props.setProperty("EXTERNAL_DB_URL", "Your Address Here");
            props.setProperty("USE_EXTERNAL_DATASOURCE", Boolean.toString(useInternetCb.isSelected()));
            props.setProperty("DB_PORT", portField.getText());
            props.setProperty("DB_NAME", dbField.getText());
            props.setProperty("DB_USERNAME", userNameField.getText());
            props.setProperty("DB_PASSWORD", pwdField.getText());
            // Static Default DB Properties
            props.setProperty("SESSION_VARS", "?sessionVariables=character_set_client=utf8,character_set_results=utf8,character_set_connection=utf8,collation_connection=utf8_general_ci");
            props.store(DbProperties.GetDbPropertiesAsOutputStream(), null);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Your Settings have been saved. \n Restart Application for settings to take effect");
            alert.showAndWait();
            closeScene();
        }
        catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to save your settings \n Reason: " + e.getMessage());
            alert.showAndWait();
        }
    }
}