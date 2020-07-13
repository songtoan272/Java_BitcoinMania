package fxml;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;
import realtime.LineChartRT;

public class DashboardSceneController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem menuImportExcel;

    @FXML
    private Menu menuExport;

    @FXML
    private MenuItem menuExportSQL;

    @FXML
    private MenuItem menuExportCSV;

    @FXML
    private MenuItem menuExportPDF;

    @FXML
    private MenuItem menuClose;

    @FXML
    private ChoiceBox<?> scaleOption;

    @FXML
    private ChoiceBox<?> currencyOption;

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
    private ToggleButton toggleCandle;

    @FXML
    private AnchorPane graphPane;

    @FXML
    private LineChartRT chartRT;

    @FXML
    private LineChart<String, Number> chartFromExcel;


    @FXML
    private void initialize(){
        chartRT = new LineChartRT(new CategoryAxis(), new NumberAxis());

        chartFromExcel = new LineChart<String, Number>(new CategoryAxis(), new NumberAxis());
        chartFromExcel.setTitle("Bitcoin Price");
        chartFromExcel.getXAxis().setLabel("Datetime");
        chartFromExcel.getYAxis().setLabel("Value");
        chartFromExcel.setAnimated(false); // disable animations
        chartFromExcel.setLegendVisible(true);
        chartFromExcel.setVisible(false);

        graphPane.getChildren().addAll(chartRT, chartFromExcel);
        for (Node chart : graphPane.getChildren()){
            ((Chart) chart).setPrefWidth(graphPane.getPrefWidth());
            ((Chart) chart).setMaxWidth(graphPane.getMaxWidth());
            ((Chart) chart).setMinWidth(graphPane.getMinWidth());

            ((Chart) chart).setPrefHeight(graphPane.getPrefHeight());
            ((Chart) chart).setMaxHeight(graphPane.getMaxHeight());
            ((Chart) chart).setMinHeight(graphPane.getMinHeight());
        }
         chartRT.setVisible(true); chartRT.setCursor(Cursor.CROSSHAIR);
         chartRT.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
            chartRT.stopUpdate();
            windowEvent.consume();
            Platform.exit();
         });
         chartRT.updateData();

        


        //Set up choicebox currencyOption
//        ObservableList<String> curriencies = FXCollections.observableArrayList(
//                "Currency",
//                "USD",
//                "EUR",
//                "GBP");
//        currencyOption.setItems(curriencies);
//        currencyOption.getSelectionModel().select(0);
//        currencyOption.getSelectionModel().selectedItemProperty().addListener((v, oldV, newV) -> {
//            System.out.println("Currency changed to " + newV);
//        });

        //Set up

    }

    @FXML
    void exportCSV(ActionEvent event) {

    }

    @FXML
    void importEXCEL(ActionEvent event) {

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

    @FXML
    void switchGraph(ActionEvent event) {
        if (toggleCandle.isSelected()) {
            chartRT.setVisible(false);
            chartFromExcel.setVisible(true);
        }else{
            chartRT.setVisible(true);
            chartFromExcel.setVisible(false);
        }
    }

}
