package edu.utsa.cs3443.macromateapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import edu.utsa.cs3443.macromateapp.MacroMateApplication;
import edu.utsa.cs3443.macromateapp.model.DataManager;
import edu.utsa.cs3443.macromateapp.model.User;

/**
 * Controller for the Sidebar navigation component of the MacroMate application.
 *
 * <p>This controller displays a welcome message for the active user, provides navigation
 * between primary screens, and visually highlights the currently active page.</p>
 */
public class SidebarController {

    private static DataManager dataManager; // Data manager used to load, save, and access application data.

    private static SidebarController instance; // Static reference to the current sidebar controller instance.

    @FXML private Label helloLabel;

    @FXML private Button dashboardBtn;
    @FXML private Button addFoodBtn;
    @FXML private Button foodLibraryBtn;
    @FXML private Button historyBtn;
    @FXML private Button settingsBtn;

    /** Inline style applied to the active/highlighted navigation button. */
    private final String ACTIVE =
            "-fx-background-color: #d1fae5; -fx-text-fill: #065f46; "
                    + "-fx-font-weight: 700; -fx-background-radius: 10; -fx-padding: 10 12;";

    /** Inline style applied to non-active navigation buttons. */
    private final String DEFAULT =
            "-fx-background-color: transparent; -fx-text-fill: #374151; "
                    + "-fx-font-weight: 700; -fx-padding: 10 12;";

    /**
     * Sets the data manager used by this controller.
     *
     * @param dm value used by this method
     */
    public static void setDataManager(DataManager dm) {
        dataManager = dm;
    }


    /**
     * Initializes the controller after its FXML has been loaded.
     *
     * <p>This method stores a static instance reference for cross-controller access and
     * populates the welcome label using the active user's name when available.</p>
     */
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

    /**
     * Highlights the active navigation page in the sidebar.
     *
     * @param page value used by this method
     */
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
    @FXML public void goDashboard() { MacroMateApplication.switchScene("dashboard.fxml", "MacroMate"); } // Navigates to the dashboard view.
    @FXML public void goAddFood() { MacroMateApplication.switchScene("add_food.fxml", "MacroMate - Add Food"); } // Navigates to the add food view.
    @FXML public void goFoodLibrary() { MacroMateApplication.switchScene("food_library.fxml", "MacroMate - Food Library"); } // Navigates to the food library view.
    @FXML public void goHistory() { MacroMateApplication.switchScene("history.fxml", "MacroMate - History"); } // Navigates to the history view.
    @FXML public void goSettings() { MacroMateApplication.switchScene("settings.fxml", "MacroMate - Settings"); } // Navigates to the settings view.

    /**
     * Logs out the active user and returns to the login screen.
     */
    @FXML
    public void handleLogout() {
        if (dataManager != null) {
            dataManager.setActiveUser(null);
            dataManager.saveAllData();
        }
        MacroMateApplication.switchScene("login.fxml", "MacroMate - Login");
    }
}
