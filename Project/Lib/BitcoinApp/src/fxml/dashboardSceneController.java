package fxml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;

public class dashboardSceneController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem menuClose;

    @FXML
    private MenuItem menuExportSQL;

    @FXML
    private MenuItem menuExportCSV;

    @FXML
    private MenuItem menuExportPDF;

    @FXML
    private MenuItem menuImportExcel;

    @FXML
    private ChoiceBox<?> scaleOption;

    @FXML
    private ChoiceBox<String> currencyOption;

    @FXML
    private Slider topThreshold;

    @FXML
    private Slider lowThreshold;

    @FXML
    private DatePicker startDate;

    @FXML
    private ChoiceBox<?> smartBoundsOption;

    @FXML
    private DatePicker endDate;

    @FXML
    private LineChart<?, ?> chart;

    @FXML
    private void initialize(){
        chart = new LineChart<String, Number>(new CategoryAxis(), new NumberAxis());

        //Set up choicebox currencyOption
        ObservableList<String> curriencies = FXCollections.observableArrayList(
                "Currency",
                "USD",
                "EUR",
                "GBP");
        currencyOption.setItems(curriencies);
        currencyOption.getSelectionModel().select(0);
        currencyOption.getSelectionModel().selectedItemProperty().addListener((v, oldV, newV) -> {
            System.out.println("Currency changed to " + newV);
        });

        //Set up

    }

    @FXML
    void exportCSV(ActionEvent event) {

    }

    @FXML
    void exportEXCEL(ActionEvent event) {

    }

    @FXML
    void exportPDF(ActionEvent event) {

    }

    @FXML
    void exportSQL(ActionEvent event) {

    }

    @FXML
    void updateCurrency(InputMethodEvent event) {
        System.out.println("currency changed");
    }

    @FXML
    void updateEndDate(ActionEvent event) {

    }

    @FXML
    void updateLowThreshold(InputMethodEvent event) {

    }

    @FXML
    void updateScale(InputMethodEvent event) {

    }

    @FXML
    void updateSmartBound(InputMethodEvent event) {

    }

    @FXML
    void updateStartDate(InputMethodEvent event) {

    }

    @FXML
    void updateTopThreshold(InputMethodEvent event) {

    }

}
