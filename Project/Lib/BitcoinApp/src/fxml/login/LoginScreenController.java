package fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sql.MySQLServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LoginScreenController {

    @FXML
    private Stage loginWindow;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Text tUsername;

    @FXML
    private Text tPassword;

    @FXML
    private TextField inputUsername;

    @FXML
    private PasswordField inputPassword;

    @FXML
    private Label lblStatus;

    @FXML
    private Button bLogIn;

    @FXML
    private Button bRegister;

    @FXML
    private Button bQuit;

    @FXML
    private void initialize()
    {

    }

    @FXML
    void login(ActionEvent event) throws IOException {
        String username = inputUsername.getText();
        String password = inputPassword.getText();

        ArrayList checkDB = MySQLServer.select(String.format(
                "select * from authentification where username='%s' and password='%s'",
                username, password
        ));

        if (checkDB == null || checkDB.size() == 0){
            lblStatus.setText("Login Failed");
        }else {
            lblStatus.setText("Login Successful");

            //swtich stage to the live dashboard and close the login screen
            Stage liveDashboard = new Stage();
            FXMLLoader loader = new FXMLLoader();
            String fxmlDocPath = "./src/fxml/dashboardScene.fxml";
            FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);
            // Create the Pane and all Details
            BorderPane root = (BorderPane) loader.load(fxmlStream);
            // setup scene
            Scene scene = new Scene(root);
            liveDashboard.setScene(scene);
//            liveDashboard.setOnCloseRequest(e -> {liveDashboard.close(); });

            // show the stage
            bQuit.fire();
            liveDashboard.show();
            liveDashboard.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, windowEvent -> {
                liveDashboard.close();
            });
        }
    }

    @FXML
    void onEnterPW(ActionEvent event) {
        bLogIn.fire();
    }

    @FXML
    void register(ActionEvent event) throws IOException {
        //open the register window
        Stage registerWindow;

        // Create the FXMLLoader
        FXMLLoader loader = new FXMLLoader();
        // Path to the FXML File
        String fxmlDocPath = "./src/fxml/registerScreen.fxml";
        FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);

        // Create the Pane and all Details
        registerWindow = (Stage) loader.load(fxmlStream);
        registerWindow.show();
    }

    @FXML
    void quit(ActionEvent event) {
        loginWindow.close();
    }

    @FXML
    void closeLoginScreen(ActionEvent event){
        loginWindow.close();
    }

}
