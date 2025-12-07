package edu.utsa.cs3443.macromateapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import edu.utsa.cs3443.macromateapp.controller.SidebarController;
import edu.utsa.cs3443.macromateapp.model.DataManager;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *  Main entry point for the MacroMate JavaFX application. This class initializes the data directory, loads all persisted user data through {@link DataManager},
 *  and determines whether to display the login screen or the dashboard at startup.
 *
 *  <p>The class also manages global scene switching so that all FXML screens use a single persistent {@link Scene} instance.
 *  When an FXML file is loaded, any controller with a {@code setDataManager(DataManager)} method automatically
 *  receives the shared {@link DataManager} instance via reflection.</p>
 *
 *  <p>Additionally, this class updates the visual highlight of the sidebar navigation depending on the active screen.</p>
 */
public class MacroMateApplication extends Application {

    /** Default application width. */
    private static final double APP_W = 1280;

    /** Default application height. */
    private static final double APP_H = 720;

    /** Primary stage of the JavaFX application. */
    private static Stage primaryStage;

    /** Main scene shared across the entire application. */
    private static Scene mainScene;

    /** Centralized manager for loading, saving, and accessing user data. */
    private static DataManager dataManager;

    /**
     * Returns the shared {@link DataManager} instance.
     *
     * @return the data manager used throughout the application
     */
    public static DataManager getDataManager() {
        return dataManager;
    }

    /**
     * Loads an FXML layout file, injects the shared {@link DataManager} into its controller
     * (if supported), updates the main application scene to display the new layout, and
     * refreshes the sidebar highlight.
     *
     * @param fxmlResource the FXML file name located under {@code /edu/utsa/cs3443/macromateapp/layout/}
     * @param title title to display on the application window
     *
     * @throws RuntimeException if the FXML file cannot be loaded or initialized
     */
    public static void switchScene(String fxmlResource, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MacroMateApplication.class.getResource(
                    "/edu/utsa/cs3443/macromateapp/layout/" + fxmlResource
            ));

            loader.setControllerFactory(clazz -> {
                try {
                    Object controller = clazz.getDeclaredConstructor().newInstance();

                    try {
                        Method m = clazz.getMethod("setDataManager", DataManager.class);
                        m.invoke(controller, dataManager);
                    } catch (NoSuchMethodException ignored) { }

                    return controller;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Parent root = loader.load();

            if (mainScene == null) {
                mainScene = new Scene(root, APP_W, APP_H);
                primaryStage.setScene(mainScene);
                primaryStage.setWidth(APP_W);
                primaryStage.setHeight(APP_H);
                primaryStage.setMinWidth(APP_W);
                primaryStage.setMinHeight(APP_H);
                primaryStage.setResizable(false);
            } else {
                mainScene.setRoot(root);
            }

            primaryStage.setTitle(title);
            primaryStage.centerOnScreen();
            primaryStage.show();

            updateSidebarHighlight(fxmlResource);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlResource, e);
        }
    }

    /**
     * Updates the sidebar highlight based on the basename of the loaded FXML file.
     *
     * @param fxmlName name of the FXML file that was loaded
     */
    private static void updateSidebarHighlight(String fxmlName) {
        fxmlName = fxmlName.toLowerCase();

        if (fxmlName.contains("dashboard")) {
            SidebarController.highlight("dashboard");
        } else if (fxmlName.contains("add_food")) {
            SidebarController.highlight("addfood");
        } else if (fxmlName.contains("food_library")) {
            SidebarController.highlight("library");
        } else if (fxmlName.contains("history")) {
            SidebarController.highlight("history");
        } else if (fxmlName.contains("settings")) {
            SidebarController.highlight("settings");
        }
    }

    /**
     * Initializes application state, loads persistent user data, and determines the
     * first screen to display (dashboard if a user is already logged in, otherwise login).
     *
     * @param stage the primary stage provided by the JavaFX runtime
     */
    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        // Load data folder in user's home directory
        Path dir = Paths.get(System.getProperty("user.home"), ".macromate1");
        dataManager = new DataManager(dir);
        dataManager.loadAllData();

        if (dataManager.getActiveUser() != null) {
            switchScene("dashboard.fxml", "MacroMate");
        } else {
            switchScene("login.fxml", "MacroMate - Login");
        }
    }

    /**
     * Standard JavaFX application entry point.
     *
     * @param args program arguments passed from the command line
     */
    public static void main(String[] args) {
        launch(args);
    }
}
