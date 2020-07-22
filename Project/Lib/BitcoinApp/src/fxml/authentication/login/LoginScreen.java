package fxml.authentication.login;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;

public class LoginScreen
{
    public static Stage create() throws IOException
    {
        // Create the FXMLLoader
        FXMLLoader loader = new FXMLLoader();
        // Path to the FXML File
        String fxmlDocPath = "./src/fxml/authentication/login/loginScreen.fxml";
        FileInputStream fxmlStream = new FileInputStream(fxmlDocPath);
        Stage stage = loader.load(fxmlStream);
        // Set the Title to the Stage
        stage.setTitle("Login");
        return stage;
    }


}