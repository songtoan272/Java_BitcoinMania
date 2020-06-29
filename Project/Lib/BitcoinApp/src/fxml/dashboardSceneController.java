package fxml;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
    private ChoiceBox<String> scaleOption;

    @FXML
    private ChoiceBox<String> currencyOption;

    @FXML
    private Slider topThreshold;

    @FXML
    private Slider lowThreshold;

    @FXML
    private Label lowThresholdLabel;

    @FXML
    private Label topThresholdLabel;

    @FXML
    private DatePicker startDate;

    @FXML
    private ChoiceBox<String> smartBoundsOption;

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

        //Set up scales options for chart

        ObservableList<String> scales = FXCollections.observableArrayList(
                "Scales",
                "DAY",
                "WEEK",
                "MONTH",
                "YEAR");
        scaleOption.setItems(scales);
        scaleOption.getSelectionModel().select(0);
        scaleOption.getSelectionModel().selectedItemProperty().addListener((v, oldV, newV) -> {
            System.out.println("Scale changed to " + newV);
        });

        //Set smartBounds

        ObservableList<String> smartBounds = FXCollections.observableArrayList(
                "Smart bounds",
                "Last 24 hours",
                "Last week",
                "Last month",
                "Last year");
        smartBoundsOption.setItems(smartBounds);
        smartBoundsOption.getSelectionModel().select(0);
        smartBoundsOption.getSelectionModel().selectedItemProperty().addListener((v, oldV, newV) -> {
            System.out.println("Currency changed to " + newV);
        });

        // set start and end date in fuunction of smart bounds

        smartBoundsOption.valueProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                switch(newValue) {
                    case "Last 24 hours":
                        startDate.setValue(LocalDate.now().minus(1, ChronoUnit.DAYS));
                        endDate.setValue(LocalDate.now());
                        break;
                    case "Last week":
                        startDate.setValue(LocalDate.now().minus(1, ChronoUnit.WEEKS));
                        endDate.setValue(LocalDate.now());
                        break;
                    case "Last month":
                        startDate.setValue(LocalDate.now().minus(1, ChronoUnit.MONTHS));
                        endDate.setValue(LocalDate.now());
                        break;
                    case "Last year":
                        startDate.setValue(LocalDate.now().minus(1, ChronoUnit.YEARS));
                        endDate.setValue(LocalDate.now());
                        break;
                    default:
                        System.out.print("Change bound");
                }
            }
        });

        //Set up thresholds value for alert + event on change to update label

        lowThresholdLabel.textProperty().setValue("Low threshold : " + lowThreshold.getValue());
        topThresholdLabel.textProperty().setValue("Top threshold : " + topThreshold.getValue());
        lowThreshold.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                lowThresholdLabel.textProperty().setValue("Low threshold : " + newValue.intValue());
            }
        });
        topThreshold.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                topThresholdLabel.textProperty().setValue("Low threshold : " + newValue.intValue());
            }
        });


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
    void updateEndDate(ActionEvent event) {

    }

    @FXML
    void updateStartDate(InputMethodEvent event) {

    }


}
