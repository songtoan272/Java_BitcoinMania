package fxml.dashboard;

import fxml.dashboard.chart.LineChartExcel;
import fxml.dashboard.chart.LineChartHistorical;
import fxml.dashboard.chart.LineChartRT;
import io.Export;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.Chart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DashboardSceneController {

    @FXML
    private Stage dashBoardWindow;

    @FXML
    private TabPane tabPane;

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
    private ChoiceBox<String> currencyOptionLive;

    @FXML
    private ChoiceBox<String> scaleOptionLive;

    @FXML
    private ChoiceBox<String> smartBoundsLive;

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
    private ChoiceBox<String> currencyOptionHis;

    @FXML
    private ChoiceBox<String> scaleOptionHis;

    @FXML
    private ChoiceBox<String> smartBoundsHis;

    @FXML
    private TextField topThresholdHis;

    @FXML
    private TextField lowThresholdHis;

    @FXML
    private DatePicker startDateHis;

    @FXML
    private DatePicker endDateHis;

    @FXML
    private AnchorPane graphPaneHistory;

    @FXML
    private Tab excelTab;

    @FXML
    private Button importButton;

    @FXML
    private ChoiceBox<String> currencyOptionExcel;

    @FXML
    private ChoiceBox<String> scaleOptionExcel;

    @FXML
    private ChoiceBox<String> smartBoundsExcel;

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

    private final StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
        final DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy");

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

    @FXML
    private void initialize(){
        //set up graphs on 3 tabs
        chartRT = new LineChartRT();
        chartFromExcel = new LineChartExcel();
        chartHistorical = new LineChartHistorical();


        setupGraph(graphPaneLive, chartRT);
        setupGraph(graphPaneHistory, chartHistorical);
        setupGraph(graphPaneExcel, chartFromExcel);

        chartRT.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {this.closeApp();});
    }

    private void setWH(Pane pane, Chart chart){
        chart.setPrefWidth(pane.getPrefWidth());
        chart.setMaxWidth(pane.getMaxWidth());
        chart.setMinWidth(pane.getMinWidth());
        chart.setPrefHeight(pane.getPrefHeight());
        chart.setMaxHeight(pane.getMaxHeight());
        chart.setMinHeight(pane.getMinHeight());
    }

    private void setupGraph(Pane pane, Chart chart){
        pane.getChildren().add(chart);
        setWH(pane, chart);
        if(chart instanceof LineChartRT){
            setupOptionsLive();
        }
        if(chart instanceof LineChartHistorical){
            setupOptionsHistorical();
        }
        if(chart instanceof LineChartExcel){
            setupOptionsExcel();
        }
    }

    private void setupOptionsLive() {
        ObservableList<String> currenciesLive = FXCollections.observableArrayList(
                "USD",
                "EUR",
                "GBP");
        currencyOptionLive.setItems(currenciesLive);
        currencyOptionLive.getSelectionModel().select(0);
        currencyOptionLive.getSelectionModel().selectedItemProperty().addListener((v, oldV, newV) -> {
            chartRT.setCurrency(newV);
        });
        currencyOptionLive.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                chartRT.setCurrency(currencyOptionLive.getValue());
            }
        });

        ObservableList<String> scales = FXCollections.observableArrayList("Scales", "MIN");
        scaleOptionLive.setItems(scales);
        scaleOptionLive.getSelectionModel().select(1);

        topThresholdLive.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}([.]\\d{0,4})?")) {
                topThresholdLive.setText(oldValue);
            }
        });
        topThresholdLive.setOnAction(new EventHandler(){
            @Override
            public void handle(Event event) {
                double newV = Double.parseDouble(topThresholdLive.getText());
                if (newV < chartRT.getLowerBound() && chartRT.getLowerBound() > 0.0){
                    topThresholdLive.setText("" + chartRT.getLowerBound());
                }
                chartRT.setUpperBound(Double.parseDouble(topThresholdLive.getText()));
                System.out.println("topThreshold="+chartRT.getUpperBound());
            }
        });

        lowThresholdLive.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}([.]\\d{0,4})?")) {
                lowThresholdLive.setText(oldValue);
            }
        });
        lowThresholdLive.setOnAction(new EventHandler(){
            @Override
            public void handle(Event event) {
                double newV = Double.parseDouble(lowThresholdLive.getText());
                if (newV > chartRT.getUpperBound() && chartRT.getUpperBound() > 0.0){
                    lowThresholdLive.setText("" + chartRT.getUpperBound());
                }
                chartRT.setLowerBound(Double.parseDouble(lowThresholdLive.getText()));
                System.out.println("lowThreshold="+chartRT.getLowerBound());
            }
        });

        startDateLive.setValue(LocalDate.now());
        endDateLive.setValue(LocalDate.now());
    }

    private void setupOptionsExcel() {
        ObservableList<String> currencies = FXCollections.observableArrayList("USD");
        currencyOptionExcel.setItems(currencies);
        currencyOptionExcel.getSelectionModel().select(0);

        ObservableList<String> scales = FXCollections.observableArrayList(
            "Scales",
                "5 MINS",
                "15 MINS",
                "HOUR",
                "12 HOURS",
                "DAY",
                "3 DAYS",
                "WEEK",
                "15 DAYS",
                "MONTH");
        scaleOptionExcel.setItems(scales);
        scaleOptionExcel.getSelectionModel().select(0);
        scaleOptionExcel.getSelectionModel().selectedItemProperty().addListener((v, oldV, newV) -> {
            if (newV.equals("Scales")){
                scaleOptionExcel.getSelectionModel().select(oldV);
            }
            System.out.println("Scale changed to " + scaleOptionExcel.getValue());
        });
        scaleOptionExcel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                chartFromExcel.setScale(scaleOptionExcel.getValue());
            }
        });

//        ObservableList<String> smartBounds = FXCollections.observableArrayList(
//                "Smart bounds",
//                "Last 24 hours",
//                "Last 3 days",
//                "Last week",
//                "Last month",
//                "Last 6 months",
//                "Last year");
//        smartBoundsExcel.setItems(smartBounds);
//        smartBoundsExcel.getSelectionModel().select(0);
//        smartBoundsExcel.getSelectionModel().selectedItemProperty().addListener((v, oldV, newV) -> {
//            if (newV.equals("Smart bounds")){
//                smartBoundsExcel.getSelectionModel().select(oldV);
//            }System.out.println("Smart bounds changed to " + smartBoundsExcel.getValue());
//        });
//
//        // set start in function of smart bounds
//        smartBoundsExcel.valueProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
//                switch (newValue) {
//                    case "Last 24 hours" -> startDateExcel.setValue(LocalDate.now().minus(1, ChronoUnit.DAYS));
//                    case "Last 3 days" -> startDateExcel.setValue(LocalDate.now().minus(3, ChronoUnit.DAYS));
//                    case "Last week" -> startDateExcel.setValue(LocalDate.now().minus(1, ChronoUnit.WEEKS));
//                    case "Last month" -> startDateExcel.setValue(LocalDate.now().minus(1, ChronoUnit.MONTHS));
//                    case "Last 6 months" -> startDateExcel.setValue(LocalDate.now().minus(6, ChronoUnit.MONTHS));
//                    case "Last year" -> startDateExcel.setValue(LocalDate.now().minus(1, ChronoUnit.YEARS));
//                    default -> System.out.print("Change bound");
//                }
//                endDateExcel.setValue(LocalDate.now());
//            }
//        });

        topThresholdExcel.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([.]\\d{0,4})?")) {
                    topThresholdExcel.setText(oldValue);
                }
            }
        });

        topThresholdExcel.setOnAction(new EventHandler(){
            @Override
            public void handle(Event event) {
                double newV = Double.parseDouble(topThresholdExcel.getText());
                if (newV < chartFromExcel.getLowerBound()){
                    topThresholdExcel.setText("" + chartFromExcel.getLowerBound());
                }
                chartFromExcel.setUpperBound(Double.parseDouble(topThresholdExcel.getText()));
                System.out.println("topThreshold="+chartFromExcel.getLowerBound());
            }
        });

        lowThresholdExcel.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([.]\\d{0,4})?")) {
                    lowThresholdExcel.setText(oldValue);
                }
            }
        });

        lowThresholdExcel.setOnAction(new EventHandler(){
            @Override
            public void handle(Event event) {
                double newV = Double.parseDouble(lowThresholdExcel.getText());
                if (newV > chartFromExcel.getUpperBound()){
                    lowThresholdExcel.setText("" + chartFromExcel.getUpperBound());
                }
                chartFromExcel.setLowerBound(Double.parseDouble(lowThresholdExcel.getText()));
                System.out.println("lowThreshold="+chartFromExcel.getLowerBound());
            }
        });

        startDateExcel.setConverter(converter);
        startDateExcel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                chartFromExcel.setFromTime(startDateExcel.getValue().atStartOfDay());
                System.out.println("startDate="+startDateExcel.getValue().toString());
            }
        });

        endDateExcel.setConverter(converter);
        endDateExcel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                chartFromExcel.setToTime(endDateExcel.getValue().atStartOfDay());
                System.out.println("endDate="+endDateExcel.getValue().toString());
            }
        });
    }

    private void setupOptionsHistorical() {
        ObservableList<String> currencies = FXCollections.observableArrayList("USD");
        currencyOptionHis.setItems(currencies);
        currencyOptionHis.getSelectionModel().select(0);

        ObservableList<String> scales = FXCollections.observableArrayList(
                "Scales",
                "DAY",
                "3 DAYS",
                "WEEK",
                "15 DAYS",
                "MONTH");
        scaleOptionHis.setItems(scales);
        scaleOptionHis.getSelectionModel().select(1);
        scaleOptionHis.getSelectionModel().selectedItemProperty().addListener((v, oldV, newV) -> {
            if (newV.equals("Scales")){
                scaleOptionHis.getSelectionModel().select(oldV);
            }
            if (!scaleOptionHis.getValue().equals(chartHistorical.getScale())){
                chartHistorical.setScale(scaleOptionHis.getValue());
            }
            System.out.println("Scale changed to " + scaleOptionHis.getValue());
        });

        ObservableList<String> smartBounds = FXCollections.observableArrayList(
                "Smart bounds",
                "Last month",
                "Last 6 months",
                "Last year");
        smartBoundsHis.setItems(smartBounds);
        smartBoundsHis.getSelectionModel().select(1);
        smartBoundsHis.getSelectionModel().selectedItemProperty().addListener((v, oldV, newV) -> {
            if (newV.equals("Smart bounds")){
                smartBoundsHis.getSelectionModel().select(oldV);
            }System.out.println("Smart bounds changed to " + smartBoundsHis.getValue());
        });

        // set start and end date in function of smart bounds
        smartBoundsHis.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            switch (newValue) {
                case "Last month" -> startDateHis.setValue(LocalDate.now().minus(1, ChronoUnit.MONTHS));
                case "Last 6 months" -> startDateHis.setValue(LocalDate.now().minus(6, ChronoUnit.MONTHS));
                case "Last year" -> startDateHis.setValue(LocalDate.now().minus(1, ChronoUnit.YEARS));
                default -> System.out.println("Change bound");
            }
            endDateHis.setValue(LocalDate.now().minusDays(1));
            chartHistorical.setDate(startDateHis.getValue(), endDateHis.getValue());
        });

        startDateHis.setConverter(converter);
        startDateHis.setValue(LocalDate.now().minusDays(31));
        startDateHis.setOnAction(actionEvent -> {
            LocalDate dNew = startDateHis.getValue();
            if (dNew.isAfter(chartHistorical.getEndDate())){
                dNew = chartHistorical.getEndDate();
                startDateHis.setValue(dNew);
            }
            if (dNew.isBefore(LocalDate.of(2010, 7, 18))){
                dNew = LocalDate.of(2010, 7, 18);
                startDateHis.setValue(dNew);
            }
            if (dNew.isAfter(LocalDate.now())){
                dNew = LocalDate.now();
                startDateHis.setValue(dNew);
            }
            chartHistorical.setStartDate(startDateHis.getValue());
            System.out.println("startDate="+startDateHis.getValue().toString());
        });

        endDateHis.setConverter(converter);
        endDateHis.setValue(LocalDate.now().minusDays(1));
        endDateHis.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LocalDate dNew = endDateHis.getValue();
                if (dNew.isBefore(chartHistorical.getStartDate())){
                    dNew = LocalDate.from(chartHistorical.getStartDate());
                    endDateHis.setValue(dNew);
                }
                if (dNew.isBefore(LocalDate.of(2010, 7, 18))){
                    dNew = LocalDate.of(2010, 7, 18);
                    endDateHis.setValue(dNew);
                }
                if (dNew.isAfter(LocalDate.now())){
                    dNew = LocalDate.now();
                    endDateHis.setValue(dNew);
                }
                chartHistorical.setEndDate(endDateHis.getValue());
                System.out.println("endDate="+endDateHis.getValue().toString());
            }
        });

        topThresholdHis.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}([.]\\d{0,4})?")) {
                topThresholdHis.setText(oldValue);
            }
        });
        topThresholdHis.setOnAction(new EventHandler(){
            @Override
            public void handle(Event event) {
                double newV = Double.parseDouble(topThresholdHis.getText());
                if (newV < chartHistorical.getLowerBound() && chartHistorical.getLowerBound() > 0.0){
                    topThresholdLive.setText("" + chartRT.getLowerBound());
                }
                chartHistorical.setUpperBound(Double.parseDouble(topThresholdHis.getText()));
                System.out.println("topThreshold="+chartHistorical.getUpperBound());
            }
        });

        lowThresholdHis.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}([.]\\d{0,4})?")) {
                lowThresholdHis.setText(oldValue);
            }
        });
        lowThresholdHis.setOnAction(new EventHandler(){
            @Override
            public void handle(Event event) {
                double newV = Double.parseDouble(lowThresholdHis.getText());
                if (newV > chartHistorical.getUpperBound() && chartHistorical.getUpperBound() > 0.0){
                    lowThresholdHis.setText("" + chartHistorical.getUpperBound());
                }
                chartHistorical.setLowerBound(Double.parseDouble(lowThresholdHis.getText()));
                System.out.println("lowThreshold="+chartHistorical.getLowerBound());
            }
        });

    }


    @FXML
    void exportCSV(ActionEvent event) {
        try {
            int tabID = tabPane.getSelectionModel().getSelectedIndex();
            Export selectedChart = switch (tabID) {
                case 0 -> chartRT;
                case 1 -> chartHistorical;
                case 2 -> chartFromExcel;
                default -> null;
            };
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose path to save CSV file");
            fileChooser.setInitialDirectory(new File(new File(".")
                    .getCanonicalFile()
                    .getParentFile()
                    .getParentFile()
                    .getAbsolutePath() + "/Dataset"));
            fileChooser.setInitialFileName("export" + LocalDate.now().toString() + ".csv");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV", "*.csv"));
            File file = fileChooser.showSaveDialog(dashBoardWindow);
            if (file != null) {
                assert selectedChart != null;
                selectedChart.exportCSVFile(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void exportPDF(ActionEvent event) {
        try {
            int tabID = tabPane.getSelectionModel().getSelectedIndex();
            Export selectedChart = switch (tabID) {
                case 0 -> chartRT;
                case 1 -> chartHistorical;
                case 2 -> chartFromExcel;
                default -> null;
            };
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose path to save CSV file");
            fileChooser.setInitialDirectory(new File(new File(".")
                    .getCanonicalFile()
                    .getParentFile()
                    .getParentFile()
                    .getAbsolutePath() + "/Dataset"));
            fileChooser.setInitialFileName("export" + LocalDate.now().toString() + ".pdf");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF", "*.pdf"));
            File file = fileChooser.showSaveDialog(dashBoardWindow);
            if (file != null) {
                assert selectedChart != null;
                selectedChart.exportPDFFile(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void exportSQL(ActionEvent event) {
        try {
            int tabID = tabPane.getSelectionModel().getSelectedIndex();
            Export selectedChart = switch (tabID) {
                case 0 -> chartRT;
                case 1 -> chartHistorical;
                case 2 -> chartFromExcel;
                default -> null;
            };
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose path to save SQL file");
            fileChooser.setInitialDirectory(new File(new File(".")
                    .getCanonicalFile()
                    .getParentFile()
                    .getParentFile()
                    .getAbsolutePath() + "/Dataset"));
            fileChooser.setInitialFileName("export" + LocalDate.now().toString() + ".sql");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("SQL", "*.sql"));
            File file = fileChooser.showSaveDialog(dashBoardWindow);
            if (file != null) {
                assert selectedChart != null;
                selectedChart.exportSQLFile(file);
                System.out.println("SQL saved to " + file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void importExcel(ActionEvent event) {
        try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Excel Data File");
            fileChooser.setInitialDirectory(new File(new File(".")
                    .getCanonicalFile()
                    .getParentFile()
                    .getParentFile()
                    .getAbsolutePath() + "/Dataset"));
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel", "*.xls", "*.xlsx"));
            File file = fileChooser.showOpenDialog(dashBoardWindow);
            if (file != null){
                chartFromExcel.changeSourceFile(file);
                if (!tabPane.getSelectionModel().getSelectedItem().equals(excelTab)){
                    tabPane.getSelectionModel().select(excelTab);
                }
                scaleOptionExcel.getSelectionModel().select(1);
                startDateExcel.setValue(chartFromExcel.getMinDate());
                endDateExcel.setValue(chartFromExcel.getMaxDate());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void closeApp() {
        chartRT.stopUpdate();
        dashBoardWindow.close();
        Platform.exit();
    }


}
