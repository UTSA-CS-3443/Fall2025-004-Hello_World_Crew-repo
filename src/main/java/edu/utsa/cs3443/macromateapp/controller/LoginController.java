package edu.utsa.cs3443.macromateapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import edu.utsa.cs3443.macromateapp.MacroMateApplication;
import edu.utsa.cs3443.macromateapp.model.DataManager;
import edu.utsa.cs3443.macromateapp.model.User;
import javafx.scene.image.ImageView;

public class LoginController {

    private DataManager dataManager;

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private ImageView logoImage;


    @FXML private TextField fullNameField;
    @FXML private TextField signUpEmailField;
    @FXML private PasswordField signUpPasswordField;
    @FXML private PasswordField confirmPasswordField;

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @FXML
    private void initialize() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setText("");
        }
    }

    @FXML
    private void handleLogin() {
        clearError();

        if (dataManager == null) {
            showError("Internal error: Data not initialized.");
            return;
        }

        String email = emailField == null ? "" : emailField.getText();
        String pass = passwordField == null ? "" : passwordField.getText();

        try {
            User u = dataManager.authenticate(email, pass);
            if (u == null) {
                showError("Invalid email or password.");
                return;
            }
            dataManager.saveAllData();
            MacroMateApplication.switchScene("dashboard.fxml", "MacroMate");
        } catch (Exception e) {
            showError("Login failed.");
        }
    }

    @FXML
    private void handleSignUp() {
        clearError();

        if (dataManager == null) {
            showError("Internal error: Data not initialized.");
            return;
        }

        String name = fullNameField == null ? "" : fullNameField.getText();
        String email = signUpEmailField == null ? "" : signUpEmailField.getText();
        String p1 = signUpPasswordField == null ? "" : signUpPasswordField.getText();
        String p2 = confirmPasswordField == null ? "" : confirmPasswordField.getText();

        if (!p1.equals(p2)) {
            showError("Passwords do not match.");
            return;
        }

        boolean ok = dataManager.registerUser(name, email, p1);
        if (!ok) {
            showError("Unable to create account (email may already exist).");
            return;
        }

        dataManager.saveAllData();
        MacroMateApplication.switchScene("dashboard.fxml", "MacroMate");
    }

    @FXML
    private void goToSignUp() {
        clearError();
        MacroMateApplication.switchScene("signup.fxml", "MacroMate - Sign Up");
    }

    @FXML
    private void goToLogin() {
        clearError();
        MacroMateApplication.switchScene("login.fxml", "MacroMate - Login");
    }

    private void clearError() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setText("");
        }
    }

    private void showError(String msg) {
        if (errorLabel != null) {
            errorLabel.setText(msg == null ? "" : msg);
            errorLabel.setVisible(true);
        }
    }
}

