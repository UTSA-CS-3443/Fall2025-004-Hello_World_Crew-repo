package edu.utsa.cs3443.macromateapp.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import edu.utsa.cs3443.macromateapp.MacroMateApplication;
import edu.utsa.cs3443.macromateapp.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FoodLibraryController {

    private DataManager dataManager;
    private Food selectedFood;

    @FXML private Label helloLabel;

    @FXML private TextField searchField;

    @FXML private ListView<Food> foodsList;
    @FXML private Label selectedFoodLabel;
    @FXML private ChoiceBox<FoodLog.MealType> mealTypeChoice;
    @FXML private TextField servingsField;
    @FXML private TextArea notesArea;
    @FXML private Label statusLabel;
    @FXML private Label errorLabel;

    @FXML private ListView<String> libraryList;

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @FXML
    private void initialize() {

        SidebarController.highlight("addfood");

        if (dataManager == null) return;

        User u = dataManager.getActiveUser();
        if (helloLabel != null) {
            String name = (u == null || u.getName() == null || u.getName().isBlank()) ? "User" : u.getName().trim();
            helloLabel.setText("Hello, " + name + "!");
        }

        if (mealTypeChoice != null) {
            mealTypeChoice.setItems(FXCollections.observableArrayList(FoodLog.MealType.values()));
            mealTypeChoice.setValue(FoodLog.MealType.LUNCH);
        }

        if (foodsList != null) {
            foodsList.setItems(FXCollections.observableArrayList(dataManager.getFoods()));
        }

        if (libraryList != null) {
            refreshLibrary();
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
    public void handleSearch() {
        if (dataManager == null) return;
        hideMessages();

        String q = searchField == null ? "" : searchField.getText();
        String query = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);

        if (foodsList != null) {
            List<Food> filtered = dataManager.getFoods().stream()
                    .filter(Objects::nonNull)
                    .filter(f -> query.isEmpty()
                            || safe(f.getName()).contains(query)
                            || safe(f.getBrand()).contains(query)
                            || safe(f.getCategory()).contains(query))
                    .collect(Collectors.toList());
            foodsList.setItems(FXCollections.observableArrayList(filtered));
        }

        if (libraryList != null) {
            List<String> items = buildCustomFoodItems().stream()
                    .filter(s -> query.isEmpty() || s.toLowerCase(Locale.ROOT).contains(query))
                    .collect(Collectors.toList());
            libraryList.setItems(FXCollections.observableArrayList(items));
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }

    @FXML
    public void handleSelectFood() {
        if (foodsList == null) return;
        selectedFood = foodsList.getSelectionModel().getSelectedItem();

        if (selectedFoodLabel != null) {
            if (selectedFood == null) {
                selectedFoodLabel.setText("Select a food item to continue");
            } else {
                selectedFoodLabel.setText(selectedFood.getName() + " (" + Math.round(selectedFood.getCalories()) + " kcal per serving)");
            }
        }
    }

    @FXML
    public void handleAddToDiary() {
        hideMessages();

        if (dataManager == null) { showError("Data not initialized."); return; }
        if (dataManager.getActiveUser() == null) { showError("No active user."); return; }
        if (selectedFood == null) { showError("Select a food first."); return; }

        FoodLog.MealType mt = mealTypeChoice == null ? FoodLog.MealType.LUNCH : mealTypeChoice.getValue();
        if (mt == null) mt = FoodLog.MealType.LUNCH;

        double servings;
        try {
            String raw = servingsField == null ? "1" : servingsField.getText();
            servings = Double.parseDouble(raw.trim());
        } catch (Exception e) {
            showError("Servings must be a number.");
            return;
        }
        if (servings <= 0) { showError("Servings must be > 0."); return; }

        String notes = notesArea == null ? "" : notesArea.getText();

        FoodLog log = dataManager.createFoodLogFromFood(
                UUID.randomUUID().toString(),
                selectedFood,
                mt,
                servings,
                LocalDateTime.now(),
                notes
        );

        dataManager.addFoodLog(LocalDate.now(), log);
        dataManager.saveAllData();
        showStatus("Added to diary.");
    }

    @FXML
    public void openCreateCustomFood() {
        hideMessages();

        if (dataManager == null) { showError("Data not initialized."); return; }
        if (dataManager.getActiveUser() == null) { showError("No active user."); return; }

        TextInputDialog n = new TextInputDialog();
        n.setHeaderText("Create Custom Food");
        n.setContentText("Name:");
        Optional<String> nameOpt = n.showAndWait();
        if (nameOpt.isEmpty()) return;
        String name = nameOpt.get().trim();
        if (name.isEmpty()) { showError("Name required."); return; }

        double calories = askDouble("Calories (kcal) per serving:", 100);
        if (calories < 0) return;
        double protein = askDouble("Protein (g) per serving:", 0);
        if (protein < 0) return;
        double carbs = askDouble("Carbs (g) per serving:", 0);
        if (carbs < 0) return;
        double fats = askDouble("Fat (g) per serving:", 0);
        if (fats < 0) return;

        String cfId = UUID.randomUUID().toString();

        CustomFood cf = new CustomFood(cfId, dataManager.getActiveUser().getId(), name, "");

        Food perServing = new Food(
                "cf_item_" + cfId,
                name,
                "Custom",
                "Custom",
                1.0,
                calories,
                protein,
                carbs,
                fats
        );

        dataManager.getFoods().add(perServing);
        cf.addIngredient(perServing, 1.0);
        dataManager.getCustomFoods().add(cf);

        dataManager.saveAllData();

        if (foodsList != null) foodsList.setItems(FXCollections.observableArrayList(dataManager.getFoods()));
        refreshLibrary();
        showStatus("Custom food created.");
    }

    @FXML
    public void handleDeleteSelectedCustomFood() {
        hideMessages();

        if (dataManager == null) { showError("Data not initialized."); return; }
        if (dataManager.getActiveUser() == null) { showError("No active user."); return; }
        if (libraryList == null) { showError("Custom list not available."); return; }

        int idx = libraryList.getSelectionModel().getSelectedIndex();
        if (idx < 0) { showError("Select a custom food first."); return; }

        List<CustomFood> mine = dataManager.getCustomFoods().stream()
                .filter(Objects::nonNull)
                .filter(cf -> dataManager.getActiveUser().getId().equalsIgnoreCase(cf.getUserId()))
                .collect(Collectors.toList());

        if (idx >= mine.size()) { showError("Select a custom food first."); return; }

        CustomFood target = mine.get(idx);

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Delete Custom Food");
        confirm.setContentText("Delete \"" + safeTitle(target.getName()) + "\"? This will also remove logs that used it.");
        Optional<ButtonType> r = confirm.showAndWait();
        if (r.isEmpty() || r.get() != ButtonType.OK) return;

        boolean ok = dataManager.deleteCustomFoodById(target.getId());
        if (!ok) { showError("Could not delete."); return; }

        if (foodsList != null) foodsList.setItems(FXCollections.observableArrayList(dataManager.getFoods()));
        refreshLibrary();
        showStatus("Custom food deleted.");
    }

    private double askDouble(String prompt, double defaultValue) {
        TextInputDialog d = new TextInputDialog(String.valueOf(defaultValue));
        d.setHeaderText("Create Custom Food");
        d.setContentText(prompt);
        Optional<String> r = d.showAndWait();
        if (r.isEmpty()) return -1;
        try {
            return Double.parseDouble(r.get().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private void refreshLibrary() {
        if (libraryList == null) return;
        libraryList.setItems(FXCollections.observableArrayList(buildCustomFoodItems()));
    }

    private List<String> buildCustomFoodItems() {
        if (dataManager == null || dataManager.getActiveUser() == null) return List.of();

        List<String> out = new ArrayList<>();
        for (CustomFood cf : dataManager.getCustomFoods()) {
            if (cf == null) continue;
            if (!dataManager.getActiveUser().getId().equalsIgnoreCase(cf.getUserId())) continue;

            Map<String, Double> m = cf.computerMacros();

            double calories = cf.computerCalories();

            out.add("%s — %.0f kcal/serv (P: %.1fg  C: %.1fg  F: %.1fg)".formatted(
                    safeTitle(cf.getName()),
                    calories,
                    m.getOrDefault("proteinG", 0.0),
                    m.getOrDefault("carbsG", 0.0),
                    m.getOrDefault("fatG", 0.0)
            ));
        }
        return out;
    }

    private String safeTitle(String s) {
        return (s == null || s.isBlank()) ? "Food" : s.trim();
    }

    @FXML public void goDashboard() { MacroMateApplication.switchScene("dashboard.fxml", "MacroMate"); }
    @FXML public void goAddFood() { MacroMateApplication.switchScene("add_food.fxml", "MacroMate - Add Food"); }
    @FXML public void goFoodLibrary() { MacroMateApplication.switchScene("food_library.fxml", "MacroMate - Food Library"); }
    @FXML public void goHistory() { MacroMateApplication.switchScene("history.fxml", "MacroMate - History"); }
    @FXML public void goSettings() { MacroMateApplication.switchScene("settings.fxml", "MacroMate - Settings"); }
}

