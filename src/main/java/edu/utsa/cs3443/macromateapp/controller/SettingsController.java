package edu.utsa.cs3443.macromateapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import edu.utsa.cs3443.macromateapp.MacroMateApplication;
import edu.utsa.cs3443.macromateapp.model.DataManager;
import edu.utsa.cs3443.macromateapp.model.Goal;
import edu.utsa.cs3443.macromateapp.model.User;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Controller for the Settings view of the MacroMate application.
 *
 * <p>This controller allows the active user to update their profile information and manage
 * nutrition goals. It loads the current user and goal values into the UI, validates input,
 * persists updates through the {@link edu.utsa.cs3443.macromateapp.model.DataManager}, and
 * supports navigation to other screens.</p>
 */
public class SettingsController {

    private DataManager dataManager; // Data manager used to load, save, and access application data.

    @FXML private Label helloLabel;

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;

    @FXML private TextField caloriesGoalField;
    @FXML private TextField proteinGoalField;
    @FXML private TextField carbsGoalField;
    @FXML private TextField fatGoalField;

    @FXML private Label statusLabel;
    @FXML private Label errorLabel;

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
     * <p>This method highlights the sidebar entry, loads the active user's profile information,
     * loads the active goal values into the form, and hides status/error messages.</p>
     */
    @FXML
    private void initialize() {
        SidebarController.highlight("settings");

        if (dataManager == null) return;

        User u = dataManager.getActiveUser();
        if (helloLabel != null) {
            String name = (u == null || u.getName() == null || u.getName().isBlank()) ? "User" : u.getName().trim();
            helloLabel.setText("Hello, " + name + "!");
        }

        if (u != null) {
            if (fullNameField != null) fullNameField.setText(u.getName() == null ? "" : u.getName());
            if (emailField != null) emailField.setText(u.getId() == null ? "" : u.getId());
        }

        Goal g = dataManager.getActiveGoal();
        if (g == null && !dataManager.getGoals().isEmpty()) g = dataManager.getGoals().get(0);

        if (g != null) {
            if (caloriesGoalField != null) caloriesGoalField.setText(String.valueOf(g.getTargetCalories()));
            if (proteinGoalField != null) proteinGoalField.setText(String.valueOf((int)Math.round(g.getTargetProteinG())));
            if (carbsGoalField != null) carbsGoalField.setText(String.valueOf((int)Math.round(g.getTargetCarbsG())));
            if (fatGoalField != null) fatGoalField.setText(String.valueOf((int)Math.round(g.getTargetFatG())));
        }

        hideMessages();
    }

    /**
     * Hides the status and error messages in the view.
     */
    private void hideMessages() {
        if (statusLabel != null) { statusLabel.setText(""); statusLabel.setVisible(false); }
        if (errorLabel != null) { errorLabel.setText(""); errorLabel.setVisible(false); }
    }

    /**
     * Displays a status message in the view.
     *
     * @param s value used by this method
     */
    private void showStatus(String s) {
        if (statusLabel != null) { statusLabel.setText(s); statusLabel.setVisible(true); }
        if (errorLabel != null) { errorLabel.setVisible(false); }
    }

    /**
     * Displays an error message in the view.
     *
     * @param s value used by this method
     */
    private void showError(String s) {
        if (errorLabel != null) { errorLabel.setText(s); errorLabel.setVisible(true); }
        if (statusLabel != null) { statusLabel.setVisible(false); }
    }


    /**
     * Saves profile changes for the active user.
     *
     * <p>This method updates the user's name if provided, and attempts to update the account email
     * through the data manager when the email field value differs from the current user id.</p>
     */
    @FXML
    public void handleSaveProfile() {
        hideMessages();

        if (dataManager == null) { showError("Data not initialized."); return; }
        User u = dataManager.getActiveUser();
        if (u == null) { showError("No active user."); return; }

        String newName = fullNameField == null ? "" : fullNameField.getText().trim();
        String newEmail = emailField == null ? "" : emailField.getText().trim();

        if (!newName.isBlank()) u.setName(newName);

        if (!newEmail.isBlank() && !newEmail.equalsIgnoreCase(u.getId())) {
            boolean ok = dataManager.updateAccountEmail(u.getId(), newEmail);
            if (!ok) { showError("Email unavailable."); return; }
        }

        dataManager.saveAllData();
        showStatus("Profile saved.");
    }

    /**
     * Saves nutrition goals for the active user.
     *
     * <p>This method validates numeric goal fields, creates a new {@link edu.utsa.cs3443.macromateapp.model.Goal}
     * with a 30-day window starting today, replaces existing goals in the data manager, and persists changes.</p>
     */
    @FXML
    public void handleSaveGoals() {
        hideMessages();

        if (dataManager == null) { showError("Data not initialized."); return; }

        int cal;
        double p, c, f;

        try {
            cal = Integer.parseInt(caloriesGoalField.getText().trim());
            p = Double.parseDouble(proteinGoalField.getText().trim());
            c = Double.parseDouble(carbsGoalField.getText().trim());
            f = Double.parseDouble(fatGoalField.getText().trim());
        } catch (Exception e) {
            showError("Goals must be numeric.");
            return;
        }

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(30);

        Goal g = new Goal(UUID.randomUUID().toString(), cal, p, c, f, start, end);

        dataManager.getGoals().clear();
        dataManager.getGoals().add(g);
        dataManager.saveAllData();

        showStatus("Goals saved.");
    }

    /**
     * Resets goal fields and stored goals to default values.
     *
     * <p>This method creates a new default goal, replaces existing goals in the data manager,
     * persists changes, and updates the UI fields to match the default values.</p>
     */
    @FXML
    public void handleResetGoals() {
        hideMessages();

        if (dataManager == null) { showError("Data not initialized."); return; }

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(30);

        Goal g = new Goal(UUID.randomUUID().toString(), 2000, 150, 200, 65, start, end);

        dataManager.getGoals().clear();
        dataManager.getGoals().add(g);
        dataManager.saveAllData();

        if (caloriesGoalField != null) caloriesGoalField.setText("2000");
        if (proteinGoalField != null) proteinGoalField.setText("150");
        if (carbsGoalField != null) carbsGoalField.setText("200");
        if (fatGoalField != null) fatGoalField.setText("65");

        showStatus("Goals reset.");
    }

    /**
     * Signs out the active user and returns to the login screen.
     */
    @FXML
    public void handleSignOut() {
        if (dataManager != null) {
            dataManager.setActiveUser(null);
            dataManager.saveAllData();
        }
        MacroMateApplication.switchScene("login.fxml", "MacroMate - Login");
    }

    @FXML public void goDashboard() { MacroMateApplication.switchScene("dashboard.fxml", "MacroMate"); } // Navigates to the dashboard view.
    @FXML public void goAddFood() { MacroMateApplication.switchScene("add_food.fxml", "MacroMate - Add Food"); } // Navigates to the add food view
    @FXML public void goFoodLibrary() { MacroMateApplication.switchScene("food_library.fxml", "MacroMate - Food Library"); } // Navigates to the food library view.
    @FXML public void goHistory() { MacroMateApplication.switchScene("history.fxml", "MacroMate - History"); } // Navigates to the history view.
    @FXML public void goSettings() { MacroMateApplication.switchScene("settings.fxml", "MacroMate - Settings"); } // Navigates to the settings view.
}


