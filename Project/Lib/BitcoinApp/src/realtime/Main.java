package realtime;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
    final int WINDOW_SIZE = 100;
    private ScheduledExecutorService scheduledExecutorService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Login");

        // Create the FXMLLoader
        FXMLLoader loader = new FXMLLoader();
        // Path to the FXML File
        String fxmlDocPath = "./src/fxml/loginScreen.fxml";
        FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);

        // Create the Pane and all Details
        Stage loginScreen = (Stage) loader.load(fxmlStream);

        // setup scene
//        Scene scene = new Scene(loginScreen);
//        primaryStage.setScene(scene);

        // show the stage
        loginScreen.show();
//        LineChart<String, Double> chart = (LineChart<String, Double>) loader.getNamespace().get("chart");
//        LineChart<String, Double> chart = (LineChart<String, Double>) ((AnchorPane)
//                        ((SplitPane) root.getCenter()).getItems().get(1))
//                        .getChildren();
//        updateData(chart);


//        System.out.println(( (RealtimeChart)
//                        ((AnchorPane)
//                        ((SplitPane) root.getCenter()).getItems().get(1))
//                        .getChildren())
//        );


    }

    public void updateData(LineChart<String, Double> chart){
//        defining a series to display data
        XYChart.Series<String, Double> series = new XYChart.Series<>();
        series.setName("Data Series");

        ObservableList<XYChart.Series<String, Double>> data = FXCollections.observableArrayList();
        data.add(series);
        // add series to chart
        chart.setData(data);

        // this is used to display time in HH:mm:ss format
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        // setup a scheduled executor to periodically put data into the chart
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // get a random integer between 0-10
            Double random = ThreadLocalRandom.current().nextDouble(10.0);

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

    @Override
    public void stop() throws Exception {
        super.stop();
        scheduledExecutorService.shutdownNow();
    }
}