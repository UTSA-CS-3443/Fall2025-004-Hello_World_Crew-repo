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

public class MacroMateApplication extends Application {

    private static final double APP_W = 1280;
    private static final double APP_H = 720;

    private static Stage primaryStage;
    private static Scene mainScene;

    private static DataManager dataManager;

    public static DataManager getDataManager() {
        return dataManager;
    }

    public static void switchScene(String fxmlResource, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MacroMateApplication.class.getResource(
                    "/edu/utsa/cs3443/macromateapp/layout/" + fxmlResource
            ));

            // Inject DataManager into any controller that has setDataManager()
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

            // Set up the scene or update its root
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

    public static void main(String[] args) {
        launch(args);
    }
}
