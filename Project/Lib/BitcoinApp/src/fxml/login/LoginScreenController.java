package fxml.login;

import io.sql.MySQLServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
            FXMLLoader loader = new FXMLLoader();
            String fxmlDocPath = "./src/fxml/dashboard/dashboardScene.fxml";
            FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);
            Stage dashBoardWindow = loader.load(fxmlStream);

            // show the stage
            bQuit.fire();
            dashBoardWindow.show();
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
        String fxmlDocPath = "./src/fxml/register/registerScreen.fxml";
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

