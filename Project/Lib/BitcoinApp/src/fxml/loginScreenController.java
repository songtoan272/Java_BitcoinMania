package fxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class loginScreenController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private Text tUsername;

    @FXML
    private Text tPassword;

    @FXML
    private TextField inputUsername;

    @FXML
    private TextField inputPassword;

    @FXML
    private Button bLogIn;

    @FXML
    private Button bQuit;

    @FXML
    private void initialize()
    {
    }

    @FXML
    void pressLogin(ActionEvent event) {
    }


    @FXML
    void login(MouseEvent event) {
        System.out.println(inputUsername.getText());
    }

    @FXML
    void onEnter(ActionEvent event) {

    }

    @FXML
    void quit(ActionEvent event) {

    }

}

