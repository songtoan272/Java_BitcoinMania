package fxml.dashboard.chart;

import fxml.util.MyTooltip;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import price.ListPriceBTC;
import price.PriceBTC;
import io.realtime.FetchPriceURL;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class LineChartHistorical extends LineChart<String, Number> {
    private String scale;
    private LocalDate startDate;
    private LocalDate endDate;
    private double lowerBound;
    private double upperBound;
    private XYChart.Series<String, Number> dataSeries;

    public LineChartHistorical() {
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

        this.setTitle("Historical Data");
        this.setAnimated(true);
        this.setAlternativeColumnFillVisible(true);

        upperBound = 0.0;
        lowerBound = 0.0;
        scale = "DAY";
        endDate = LocalDate.now();
        startDate = endDate.minusDays(31);
        dataSeries = new XYChart.Series<>();
        dataSeries.setName("Price in USD");
        Series<String, Number> lowerThresholdSeries = new Series<>();
        lowerThresholdSeries.setName("LowerBound");
        Series<String, Number> upperThresholdSeries = new Series<>();
        upperThresholdSeries.setName("UpperBound");
        this.getData().addAll(this.dataSeries, upperThresholdSeries, lowerThresholdSeries);
        this.feedData(FetchPriceURL.fetchHistoricalPrice());
    }

    private void feedData(List<PriceBTC> prices){
        if (prices==null) return;
        Series<String, Number> newSeries = this.getData().get(0);
        newSeries.getData().clear();
        XYChart.Data<String, Number> point;
        for (PriceBTC p : prices){
            point = new XYChart.Data<>(
                    p.getDatetime().toString(),
                    p.getPriceUSD()
            );
            newSeries.getData().add(point);
            Tooltip.install(point.getNode(), new MyTooltip(
                    "Date: "+ point.getXValue() + "\n" +
                            "Price: "+point.getYValue()
            ));
        }
    }

    private void updateData(){
        LinkedList<PriceBTC> prices = (LinkedList<PriceBTC>) ListPriceBTC.byScale(FetchPriceURL.fetchHistoricalPrice(this.startDate, this.endDate), this.scale);
//        LinkedList<PriceBTC> prices = (LinkedList<PriceBTC>) FetchPriceURL.fetchHistoricalPrice(this.startDate, this.endDate);
        this.feedData(prices);
    }

    private void updateThreshold(boolean isUpper){
        Series<String, Number> series = this.getData().get(isUpper ?1:2);
        series.getData().clear();
        int count = 0;
        for (Data<String, Number> d: dataSeries.getData()){
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

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        if (this.scale.equals(scale)) return;
        this.scale = scale;
        updateData();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        if (this.startDate.equals(startDate)) return;
        this.startDate = startDate;
        updateData();
    }

    public double getLowerBound() {
        return this.lowerBound;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (this.endDate.equals(endDate)) return;
        this.endDate = endDate;
        updateData();
    }

    public void setDate(LocalDate startDate, LocalDate endDate){
        if (this.startDate.equals(startDate) && this.endDate.equals(endDate)) return;
        this.startDate = startDate;
        this.endDate = endDate;
        this.updateData();
    }

    public double getUpperBound() {
        return this.upperBound;
    }


    public void setLowerBound(double lowerBound) {
        if (this.lowerBound == lowerBound){
            return;
        }
        this.lowerBound = lowerBound;
        this.updateThreshold(false);
    }

    public void setUpperBound(double upperBound) {
        if (this.upperBound == upperBound) return;
        this.upperBound = upperBound;
        this.updateThreshold(true);
    }
}
