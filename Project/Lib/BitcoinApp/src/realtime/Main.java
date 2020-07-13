package realtime;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.util.concurrent.ScheduledExecutorService;

public class Main extends Application {
    final int WINDOW_SIZE = 100;
    private ScheduledExecutorService scheduledExecutorService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.close();
        // Create the FXMLLoader
        FXMLLoader loader = new FXMLLoader();
        // Path to the FXML File
        String fxmlDocPath = "../fxml/loginScreen.fxml";
        // Create the Pane and all Details
        Stage loginScreen = (Stage) FXMLLoader.load(getClass().getResource(fxmlDocPath));

        // show the log in stage
        loginScreen.show();

    }


    @Override
    public void stop() throws Exception {
        super.stop();
    }
}