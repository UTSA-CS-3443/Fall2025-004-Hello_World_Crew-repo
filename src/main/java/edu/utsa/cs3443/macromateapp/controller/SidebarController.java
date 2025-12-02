package edu.utsa.cs3443.macromateapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import edu.utsa.cs3443.macromateapp.MacroMateApplication;
import edu.utsa.cs3443.macromateapp.model.DataManager;
import edu.utsa.cs3443.macromateapp.model.User;

public class SidebarController {

    private static DataManager dataManager;

    // static instance of THIS controller
    private static SidebarController instance;

    @FXML private Label helloLabel;

    @FXML private Button dashboardBtn;
    @FXML private Button addFoodBtn;
    @FXML private Button foodLibraryBtn;
    @FXML private Button historyBtn;
    @FXML private Button settingsBtn;

    private final String ACTIVE =
            "-fx-background-color: #d1fae5; -fx-text-fill: #065f46; "
                    + "-fx-font-weight: 700; -fx-background-radius: 10; -fx-padding: 10 12;";

    private final String DEFAULT =
            "-fx-background-color: transparent; -fx-text-fill: #374151; "
                    + "-fx-font-weight: 700; -fx-padding: 10 12;";

    public static void setDataManager(DataManager dm) {
        dataManager = dm;
    }

    @FXML
    private void initialize() {
        // store instance so other controllers can talk to the sidebar
        instance = this;

        // set welcome name
        if (helloLabel != null && dataManager != null && dataManager.getActiveUser() != null) {
            User u = dataManager.getActiveUser();
            String name = (u.getName() == null || u.getName().isBlank()) ? "User" : u.getName();
            helloLabel.setText("Hello, " + name + "!");
        }
    }

    // This is what other screens call
    public static void highlight(String page) {
        if (instance == null) return; // sidebar not loaded yet

        // reset all
        instance.dashboardBtn.setStyle(instance.DEFAULT);
        instance.addFoodBtn.setStyle(instance.DEFAULT);
        instance.foodLibraryBtn.setStyle(instance.DEFAULT);
        instance.historyBtn.setStyle(instance.DEFAULT);
        instance.settingsBtn.setStyle(instance.DEFAULT);

        // apply highlight
        switch (page.toLowerCase()) {
            case "dashboard" -> instance.dashboardBtn.setStyle(instance.ACTIVE);
            case "addfood" -> instance.addFoodBtn.setStyle(instance.ACTIVE);
            case "library" -> instance.foodLibraryBtn.setStyle(instance.ACTIVE);
            case "history" -> instance.historyBtn.setStyle(instance.ACTIVE);
            case "settings" -> instance.settingsBtn.setStyle(instance.ACTIVE);
        }
    }

    // Navigation
    @FXML public void goDashboard() { MacroMateApplication.switchScene("dashboard.fxml", "MacroMate"); }
    @FXML public void goAddFood() { MacroMateApplication.switchScene("add_food.fxml", "MacroMate - Add Food"); }
    @FXML public void goFoodLibrary() { MacroMateApplication.switchScene("food_library.fxml", "MacroMate - Food Library"); }
    @FXML public void goHistory() { MacroMateApplication.switchScene("history.fxml", "MacroMate - History"); }
    @FXML public void goSettings() { MacroMateApplication.switchScene("settings.fxml", "MacroMate - Settings"); }

    @FXML
    public void handleLogout() {
        if (dataManager != null) {
            dataManager.setActiveUser(null);
            dataManager.saveAllData();
        }
        MacroMateApplication.switchScene("login.fxml", "MacroMate - Login");
    }
}
