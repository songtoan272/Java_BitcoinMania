package fxml.dashboard.chart;

import fxml.util.MyTooltip;
import io.Export;
import io.excel.ExcelReader;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Tooltip;
import price.ListPriceBTC;
import price.PriceBTC;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class LineChartExcel extends LineChart<String, Number> implements Export {
    double lowerBound;
    double upperBound;
    String scale;
    LocalDateTime fromTime;
    LocalDateTime toTime;
    List<PriceBTC> allPrices;


    public LineChartExcel() {
        super(new CategoryAxis(), new NumberAxis());
        CategoryAxis xAxis = (CategoryAxis)  this.getXAxis();
        NumberAxis yAxis = (NumberAxis)  this.getYAxis();

        //defining the axes
//        xAxis.setLabel("Date");
        xAxis.setAnimated(true);
        xAxis.setAutoRanging(true);
        xAxis.setVisible(false);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);

        yAxis.setAnimated(true);
        yAxis.setAutoRanging(true);
        yAxis.setVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setForceZeroInRange(false);
        yAxis.setTickLabelsVisible(true);
        yAxis.setMinorTickVisible(false);

        this.setTitle("Excel Data");
        this.setAnimated(true);
        this.setAlternativeColumnFillVisible(false);

        this.upperBound = 0.0;
        this.lowerBound = 0.0;

        ObservableList<Series<String, Number>> data = this.getData();
        Series<String, Number> priceSeries = new Series<>();
        priceSeries.setName("Price in USD");
        Series<String, Number> lowerThresholdSeries = new Series<>();
        lowerThresholdSeries.setName("LowerBound");
        Series<String, Number> upperThresholdSeries = new Series<>();
        upperThresholdSeries.setName("UpperBound");
        //add series to data and add data to fxml.dashboard.chart
        data.addAll(priceSeries, upperThresholdSeries, lowerThresholdSeries);
//        this.changeSourceFile(new File("/home/songtoan272/Documents/Cours/Semestre_5/Java_Bases/BitcoinMania/Project/Dataset/Fichier-Excel.xlsx"));
    }

    private void feedData(List<PriceBTC> prices) {
        if (prices == null) return;
        Series<String, Number> newDataSeries = this.getData().get(0);
        newDataSeries.getData().clear();
        for (PriceBTC p : prices) {
            Data<String, Number> point = new Data<>(
                    p.getDatetime().toString(),
                    p.getPriceUSD()
            );
            newDataSeries.getData().add(point);
            Tooltip.install(point.getNode(), new MyTooltip(
                    "Date: " + point.getXValue() + "\n" +
                            "Price: " + point.getYValue()
            ));
        }
    }

    private void updateData(){
        List<PriceBTC> prices = ListPriceBTC.byDates(this.allPrices, this.fromTime, this.toTime);
        prices = ListPriceBTC.byScale(prices, this.scale);
        this.feedData(prices);
    }

    public void changeSourceFile(File file){
        System.out.println("Source Excel changed to " + file.getAbsolutePath());
        this.allPrices = ExcelReader.read(file);
        if (this.allPrices == null) return;
        if (this.allPrices.size()==0) return;
        this.scale = "5 MINS";
        this.fromTime = this.allPrices.get(0).getDatetime();
        this.toTime = this.allPrices.get(allPrices.size()-1).getDatetime();
        this.feedData(this.allPrices);
    }

    private void updateThreshold(boolean isUpper){
        Series<String, Number> series = this.getData().get(isUpper ?1:2);
        series.getData().clear();
        int count = 0;
        for (Data<String, Number> d: this.getData().get(0).getData()){
            Data<String, Number> point = new Data<>(d.getXValue(),
                    isUpper ? this.upperBound:this.lowerBound);
            series.getData().add(point);
            point.getNode().setVisible(false);
            if (isUpper && (double) d.getYValue() >= this.upperBound) count++;
            if (!isUpper && (double) d.getYValue() <= this.lowerBound) count++;
        }
        Tooltip.install(series.getNode(), new MyTooltip(
                isUpper? "Cross Upper Threshold: " + count + " times.":
                        "Cross Lower Threshold: " + count + " times."
        ));
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        if (this.lowerBound == lowerBound) return;
        this.lowerBound = lowerBound;
        this.updateThreshold(false);
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upperBound) {
        if (this.upperBound == upperBound) return;
        this.upperBound = upperBound;
        this.updateThreshold(true);
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        if (this.scale == null) return;
        if (this.scale.equals(scale)) return;
        this.scale = scale;
        this.updateData();
    }

    public LocalDateTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalDateTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalDateTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalDateTime toTime) {
        this.toTime = toTime;
    }

    public LocalDate getMinDate(){
        if (this.allPrices != null){
            return this.allPrices.get(0).getDatetime().toLocalDate();
        }else return null;
    }

    public LocalDate getMaxDate(){
        if (this.allPrices != null){
            return this.allPrices.get(this.allPrices.size()-1).getDatetime().toLocalDate();
        }else return null;
    }

    @Override
    public void exportCSVFile(File file) {

    }

    @Override
    public void exportSQLFile(File file) {

    }
}
