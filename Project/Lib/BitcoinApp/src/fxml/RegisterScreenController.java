package fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    private PasswordField intputPassword;

    @FXML
    private PasswordField inputCfPassword;

    @FXML
    void closerRegisterWindow(ActionEvent event) {
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

    }

}