package fxml;

import excel.LineChartExcel;
import historical.LineChartHistorical;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.Chart;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import realtime.LineChartRT;

public class DashboardSceneController {

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
    private Tab liveTab;

    @FXML
    private ChoiceBox<?> currencyOptionLive;

    @FXML
    private ChoiceBox<?> scaleOptionLive;

    @FXML
    private ChoiceBox<?> smartBoundsLive;

    @FXML
    private TextField topThresholdLive;

    @FXML
    private TextField lowThresholdLive;

    @FXML
    private DatePicker startDateLive;

    @FXML
    private DatePicker endDateLive;

    @FXML
    private AnchorPane graphPaneLive;

    @FXML
    private Tab historyTab;

    @FXML
    private ChoiceBox<?> currencyOptionHis;

    @FXML
    private ChoiceBox<?> scaleOptionHis;

    @FXML
    private ChoiceBox<?> smartBoundsHis;

    @FXML
    private DatePicker startDateHis;

    @FXML
    private DatePicker endDateHis;

    @FXML
    private AnchorPane graphPaneHistory;

    @FXML
    private Tab excelTab;

    @FXML
    private ChoiceBox<?> currencyOptionExcel;

    @FXML
    private ChoiceBox<?> scaleOptionExcel;

    @FXML
    private ChoiceBox<?> smartBoundsExcel;

    @FXML
    private TextField topThresholdExcel;

    @FXML
    private TextField lowThresholdExcel;

    @FXML
    private DatePicker startDateExcel;

    @FXML
    private DatePicker endDateExcel;

    @FXML
    private AnchorPane graphPaneExcel;

    @FXML
    private LineChartRT chartRT;

    @FXML
    private LineChartExcel chartFromExcel;

    @FXML
    private LineChartHistorical chartHistorical;

    @FXML
    private void initialize(){
        chartRT = new LineChartRT();
        chartFromExcel = new LineChartExcel();
        chartHistorical = new LineChartHistorical();

        graphPaneLive.getChildren().add(chartRT);
        graphPaneHistory.getChildren().add(chartHistorical);
        graphPaneExcel.getChildren().add(chartFromExcel);

        setWH(graphPaneLive, chartRT);
        setWH(graphPaneHistory, chartHistorical);
        setWH(graphPaneExcel, chartFromExcel);
//        chartRT.setVisible(true); chartRT.setCursor(Cursor.CROSSHAIR);
//        chartRT.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
//            chartRT.stopUpdate();
//            windowEvent.consume();
//            Platform.exit();
//        });
//        chartRT.updateData();
//        chartRT.on
//        LineChartRT l = (LineChartRT) graphPane.getChildren().get(0);




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

    private void setWH(Pane pane, Chart chart){
        chart.setPrefWidth(pane.getPrefWidth());
        chart.setMaxWidth(pane.getMaxWidth());
        chart.setMinWidth(pane.getMinWidth());
        chart.setPrefHeight(pane.getPrefHeight());
        chart.setMaxHeight(pane.getMaxHeight());
        chart.setMinHeight(pane.getMinHeight());
    }

    @FXML
    void exportCSV(ActionEvent event) {

    }

    @FXML
    void exportPDF(ActionEvent event) {

    }

    @FXML
    void exportSQL(ActionEvent event) {

    }

    @FXML
    void importEXCEL(ActionEvent event) {

    }

    @FXML
    void updateCurrency(InputMethodEvent event) {

    }

    @FXML
    void updateEndDate(ActionEvent event) {

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


}
