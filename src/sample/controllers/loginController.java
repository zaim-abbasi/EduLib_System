package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.repositories.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class loginController {
    @FXML
    private Button cancelButton;

    @FXML
    private Label loginMessageLabel;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordPasswordField;

    private static Connection connectDB = null;

    public void loginButtonOnAction(ActionEvent e) {
        String username = usernameTextField.getText();
        String password = passwordPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginMessageLabel.setText("Please enter both username and password.");
            return;
        }

        try (Connection connectDB = new DatabaseConnection().getConnection();
                PreparedStatement preparedStatement = connectDB.prepareStatement(
                        "SELECT COUNT(1) FROM userAccount WHERE username = ? AND password = ?")) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet queryResult = preparedStatement.executeQuery();

            if (queryResult.next() && queryResult.getInt(1) == 1) {
                navigateToMainScreen();
            } else {
                loginMessageLabel.setText("Invalid login credentials. Please try again.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void navigateToMainScreen() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("../views/main-screen.fxml"));
            Scene scene = new Scene(parent);
            Stage primaryStage = new Stage();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Library Management System");
            primaryStage.getIcons().add(new Image("https://static.thenounproject.com/png/3314579-200.png"));
            primaryStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void cancelButtonOnAction(ActionEvent e) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
