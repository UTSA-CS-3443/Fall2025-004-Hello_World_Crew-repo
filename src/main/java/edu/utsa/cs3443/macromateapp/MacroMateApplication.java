package edu.utsa.cs3443.macromateapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
            //FXMLLoader loader = new FXMLLoader(MacroMateApplication.class.getResource("/myself/macromate1/view/" + fxmlResource));
            FXMLLoader loader = new FXMLLoader(MacroMateApplication.class.getResource("/edu/utsa/cs3443/macromateapp/layout/" + fxmlResource)
            );
            loader.setControllerFactory(clazz -> {
                try {
                    Object controller = clazz.getDeclaredConstructor().newInstance();
                    try {
                        Method m = clazz.getMethod("setDataManager", DataManager.class);
                        m.invoke(controller, dataManager);
                    } catch (NoSuchMethodException ignored) {
                    }
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

        } catch (Exception e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlResource, e);
        }
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

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
