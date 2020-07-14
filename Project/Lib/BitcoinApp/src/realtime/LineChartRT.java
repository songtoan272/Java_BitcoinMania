package realtime;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import price.PriceBTC;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class LineChartRT extends LineChart<String, Number> {

    final int WINDOW_SIZE = 100;
    private ScheduledExecutorService scheduledExecutorService;

    private final FetchPriceURL liveDataStream;
    private final Series<String, Number> liveDataSeries;
    private LinkedList<PriceBTC> allPrices;

    private String currency;
    private double lowerBound;
    private double upperBound;
    private double lastClose;



    public LineChartRT(CategoryAxis xAxis, NumberAxis yAxis) {
        super(xAxis, yAxis);
        CategoryAxis xAxis_ = (CategoryAxis)  this.getXAxis();
        NumberAxis yAxis_ = (NumberAxis)  this.getYAxis();

        //defining the axes
//        xAxis_.setLabel("Date");
        xAxis_.setAnimated(true);
        xAxis_.setAutoRanging(true);
        xAxis_.setVisible(false);
        xAxis_.setTickLabelsVisible(false);
        xAxis_.setTickMarkVisible(false);

        yAxis_.setAnimated(true);
        yAxis_.setAutoRanging(true);
        yAxis_.setVisible(false);
        yAxis_.setTickMarkVisible(false);
        yAxis_.setForceZeroInRange(false);
        yAxis_.setTickLabelsVisible(true);
        yAxis_.setMinorTickVisible(false);
//        yAxis_.set

        //declare attributes
        currency = "USD";
        lowerBound = -1.;
        upperBound = -1.;
        liveDataSeries = new Series<>();
        liveDataSeries.setName("Price in "+currency);
        liveDataStream = new FetchPriceURL("https://api.coindesk.com/v1/bpi/currentprice.json");
        allPrices = new LinkedList<>();
        lastClose = FetchPriceURL.fetchLastClose();

        //creating the line chart with two axis created above
        this.setTitle("Bitcoin Price LIVE");
        this.setAnimated(true); // disable animations
        this.setLegendVisible(true);
//        tis.add
    }

    public void updateData(){
        ObservableList<Series<String, Number>> data = FXCollections.observableArrayList();
        Series<String, Number> lowerThresholdSeries = new Series<>();
        lowerThresholdSeries.setName("LowerBound");
        Series<String, Number> upperThresholdSeries = new Series<>();
        upperThresholdSeries.setName("UpperBound");
        //add series to data and add data to chart
        data.addAll(liveDataSeries, upperThresholdSeries, lowerThresholdSeries);
        this.setData(data);

        lowerBound = 9200.0;
        upperBound = 9250.0;
        Tooltip.install(lowerThresholdSeries.getNode(), new MyTooltip(
                "Lower Threshold: " + lowerBound
        ));
        Tooltip.install(upperThresholdSeries.getNode(), new MyTooltip(
                "Upper Threshold: " + upperBound
        ));

        // setup a scheduled executor to periodically put data into the chart
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // Update the chart
            Platform.runLater(() -> {
                // fetch new data input from the stream and put it on graph
                PriceBTC price = liveDataStream.fetchLivePrice();
                System.out.println(price.toString());
                allPrices.add(price);
                Data<String, Number> pricePoint = switch (currency) {
                    case "USD" -> new Data<>(price.getDatetime().toString(), price.getPriceUSD());
                    case "EUR" -> new Data<>(price.getDatetime().toString(), price.getPriceEUR());
                    case "GBP" -> new Data<>(price.getDatetime().toString(), price.getPriceGBP());
                    default -> new Data<>(price.getDatetime().toString(), price.getPriceUSD());
                };
                liveDataSeries.getData().add(pricePoint);
                //set tooltip for the new data point of the graph
                Tooltip.install(pricePoint.getNode(),
                        new MyTooltip("Date: " + pricePoint.getXValue() +"\n" +
                                "Price: " + pricePoint.getYValue() + " " + currency + "\n" +
                                tendency(pricePoint.getYValue().doubleValue(),
                                        (liveDataSeries.getData().size() > 1 ?
                                                liveDataSeries.getData().get(liveDataSeries.getData().size()-2).getYValue().doubleValue() : pricePoint.getYValue().doubleValue())))
                );

                if (lowerBound >= 0.0){
                    Data<String, Number> lowerPoint = new Data<>(price.getDatetime().toString(), lowerBound);
                    lowerThresholdSeries.getData().add(lowerPoint);
                    lowerPoint.getNode().setVisible(false);
                }
                if (upperBound >= 0.0){
                    XYChart.Data<String, Number> upperPoint = new Data<>(price.getDatetime().toString(), upperBound);
                    upperThresholdSeries.getData().add(upperPoint);
                    upperPoint.getNode().setVisible(false);
                }

                //Crop graph in window size
                if (liveDataSeries.getData().size() > WINDOW_SIZE) {
                    liveDataSeries.getData().remove(0);
                    lowerThresholdSeries.getData().remove(0);
                    upperThresholdSeries.getData().remove(0);
                }
            });
        }, 0, 1, TimeUnit.MINUTES);
    }

    public void stopUpdate(){
        scheduledExecutorService.shutdownNow();
        scheduledExecutorService.shutdown();
    }

    class MyTooltip extends Tooltip {
        MyTooltip(String s){
            super(s);
            setShowDelay(Duration.seconds(0.01));
        }
    }

    private String tendency(double currentPrice, double previousPrice){
        if (currentPrice < previousPrice){
            double percentage = (previousPrice - currentPrice) / previousPrice;
            return "v " + percentage + "%";
        }else{
            double percentage = (currentPrice - previousPrice) / previousPrice;
            return "^ " + percentage + "%";
        }
    }

    public void switchCurrencyLive(){
        liveDataSeries.getData().removeAll();
        XYChart.Data<String, Number> pricePoint;
        for (PriceBTC p : allPrices){
            pricePoint = switch (currency) {
                case "USD" -> new XYChart.Data<>(p.getDatetime().toString(), p.getPriceUSD());
                case "EUR" -> new XYChart.Data<>(p.getDatetime().toString(), p.getPriceEUR());
                case "GBP" -> new XYChart.Data<>(p.getDatetime().toString(), p.getPriceGBP());
                default -> new XYChart.Data<>(p.getDatetime().toString(), p.getPriceUSD());
            };
            liveDataSeries.getData().add(pricePoint);
            liveDataSeries.getData().add(pricePoint);
            Tooltip pricePointTooltip = new Tooltip("Date: " + pricePoint.getXValue() +"\n" +
                    "Price: " + pricePoint.getYValue() + " " + currency);
            Tooltip.install(pricePoint.getNode(), pricePointTooltip);
            pricePointTooltip.setShowDelay(Duration.seconds(0.01));
        }
    }

}
