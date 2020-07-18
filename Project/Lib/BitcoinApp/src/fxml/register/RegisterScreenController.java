package fxml.register;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import io.sql.MySQLServer;

import java.util.ArrayList;

public class RegisterScreenController {

    @FXML
    private Stage registerWindow;

    @FXML
    private TextField inputUsername;

    @FXML
    private Button bRegister;

    @FXML
    private Button bQuit;

    @FXML
    private Label lblStatus;

    @FXML
    private PasswordField inputPassword;

    @FXML
    private PasswordField inputCfPassword;

    @FXML
    void closeRegisterWindow(ActionEvent event) {
        registerWindow.close();
    }

    @FXML
    void enterCfPassword(ActionEvent event) {
        bRegister.fire();
    }

    @FXML
    void quit(ActionEvent event) {
        registerWindow.close();
    }

    @FXML
    void register(ActionEvent event) {
        String username = inputUsername.getText();
        String password = inputPassword.getText();
        String confirmPassword = inputCfPassword.getText();

        if (!password.equals(confirmPassword)) {
            lblStatus.setText("Password and confirm password do not match.");
            return;
        }

        ArrayList checkDB = MySQLServer.select(String.format(
                "select * from authentification where username='%s'",
                username
        ));

        if (checkDB == null || checkDB.size() == 0){
            MySQLServer.insertOne(String.format(
                    "insert into authentification values ('%s', '%s')",
                    username, password
            ));
            lblStatus.setText("Registered successfully! Use your ID and password to log into application.");
        }else{
            lblStatus.setText("Username already existed.");
        }
    }

}