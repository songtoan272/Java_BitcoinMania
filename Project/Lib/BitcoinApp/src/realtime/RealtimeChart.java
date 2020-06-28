package realtime;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


public class RealtimeChart<X,Y> extends LineChart {

    final int WINDOW_SIZE = 100;
    private ScheduledExecutorService scheduledExecutorService;
    Series<String, Number> series;


    public RealtimeChart(CategoryAxis xAxis, NumberAxis yAxis) {
        super(xAxis, yAxis);

        //defining the axes
        this.getXAxis().setLabel("Time/s");
        this.getXAxis().setAnimated(false); // axis animations are removed
        this.getYAxis().setLabel("Value");
        this.getYAxis().setAnimated(false); // axis animations are removed

        series = new Series<>();
        series.setName("Data Series");

        //creating the line chart with two axis created above
        this.setTitle("Realtime Bitcoin Price");
        this.setAnimated(false); // disable animations
        this.setLegendVisible(true);

    }

    public void updateData(){
        //defining a series to display data
//        Series<String, Number> series = new Series<>();
//        series.setName("Data Series");

        ObservableList<Series<String, Number>> data = FXCollections.observableArrayList();
        data.add(series);
        // add series to chart
        this.setData(data);

        // this is used to display time in HH:mm:ss format
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        // setup a scheduled executor to periodically put data into the chart
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // get a random integer between 0-10
            Integer random = ThreadLocalRandom.current().nextInt(10);

            // Update the chart
            Platform.runLater(() -> {
                // get current time
                Date now = new Date();
                // put random number with current time
                series.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), random));

                if (series.getData().size() > WINDOW_SIZE)
                    series.getData().remove(0);
            });
        }, 0, 1, TimeUnit.SECONDS);
    }

}
