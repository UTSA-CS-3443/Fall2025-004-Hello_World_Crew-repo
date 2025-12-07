package edu.utsa.cs3443.macromateapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import edu.utsa.cs3443.macromateapp.MacroMateApplication;
import edu.utsa.cs3443.macromateapp.model.DataManager;
import edu.utsa.cs3443.macromateapp.model.User;
import javafx.scene.image.ImageView;

/**
 * Controller for the Login and Sign Up views of the MacroMate application.
 *
 * <p>This controller handles user authentication, account creation, and navigation between
 * the login and sign up screens.</p>
 */
public class LoginController {

    private DataManager dataManager; // Data manager used to load, save, and access application data.

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private ImageView logoImage;

    @FXML private TextField fullNameField;
    @FXML private TextField signUpEmailField;
    @FXML private PasswordField signUpPasswordField;
    @FXML private PasswordField confirmPasswordField;


    /**
     * Sets the data manager used by this controller.
     *
     * @param dataManager value used by this method
     */
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * Initializes the controller after its FXML has been loaded.
     *
     * <p>This method clears and hides the error label on initial load.</p>
     */
    @FXML
    private void initialize() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setText("");
        }
    }

    /**
     * Attempts to authenticate the user and navigate to the dashboard on success.
     */
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

    /**
     * Attempts to create a new user account and navigate to the dashboard on success.
     */
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

    /**
     * Navigates to the sign up view.
     */
    @FXML
    private void goToSignUp() {
        clearError();
        MacroMateApplication.switchScene("signup.fxml", "MacroMate - Sign Up");
    }

    /**
     * Navigates to the login view.
     */
    @FXML
    private void goToLogin() {
        clearError();
        MacroMateApplication.switchScene("login.fxml", "MacroMate - Login");
    }

    /**
     * Clears and hides the error label.
     */
    private void clearError() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setText("");
        }
    }

    /**
     * Displays an error message in the view.
     *
     * @param msg value used by this method
     */
    private void showError(String msg) {
        if (errorLabel != null) {
            errorLabel.setText(msg == null ? "" : msg);
            errorLabel.setVisible(true);
        }
    }
}

