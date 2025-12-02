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

public class SettingsController {

    private DataManager dataManager;

    @FXML private Label helloLabel;

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;

    @FXML private TextField caloriesGoalField;
    @FXML private TextField proteinGoalField;
    @FXML private TextField carbsGoalField;
    @FXML private TextField fatGoalField;

    @FXML private Label statusLabel;
    @FXML private Label errorLabel;

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @FXML
    private void initialize() {
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

    private void hideMessages() {
        if (statusLabel != null) { statusLabel.setText(""); statusLabel.setVisible(false); }
        if (errorLabel != null) { errorLabel.setText(""); errorLabel.setVisible(false); }
    }

    private void showStatus(String s) {
        if (statusLabel != null) { statusLabel.setText(s); statusLabel.setVisible(true); }
        if (errorLabel != null) { errorLabel.setVisible(false); }
    }

    private void showError(String s) {
        if (errorLabel != null) { errorLabel.setText(s); errorLabel.setVisible(true); }
        if (statusLabel != null) { statusLabel.setVisible(false); }
    }

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

    @FXML
    public void handleSignOut() {
        if (dataManager != null) {
            dataManager.setActiveUser(null);
            dataManager.saveAllData();
        }
        MacroMateApplication.switchScene("login.fxml", "MacroMate - Login");
    }

    @FXML public void goDashboard() { MacroMateApplication.switchScene("dashboard.fxml", "MacroMate"); }
    @FXML public void goAddFood() { MacroMateApplication.switchScene("add_food.fxml", "MacroMate - Add Food"); }
    @FXML public void goFoodLibrary() { MacroMateApplication.switchScene("food_library.fxml", "MacroMate - Food Library"); }
    @FXML public void goHistory() { MacroMateApplication.switchScene("history.fxml", "MacroMate - History"); }
    @FXML public void goSettings() { MacroMateApplication.switchScene("settings.fxml", "MacroMate - Settings"); }
}


