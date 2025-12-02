package edu.utsa.cs3443.macromateapp.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import edu.utsa.cs3443.macromateapp.MacroMateApplication;
import edu.utsa.cs3443.macromateapp.model.*;
import javafx.scene.control.DatePicker;
import javafx.scene.chart.CategoryAxis;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DashboardController {

    private DataManager dataManager;

    @FXML private Label helloLabel;
    @FXML private Label dateLabel;

    @FXML private Label calorieProgressLabel;
    @FXML private ProgressBar calorieProgressBar;
    @FXML private Label calorieRemainingLabel;

    @FXML private Label proteinLabel;
    @FXML private ProgressBar proteinProgressBar;

    @FXML private Label carbsLabel;
    @FXML private ProgressBar carbsProgressBar;

    @FXML private Label fatLabel;
    @FXML private ProgressBar fatProgressBar;

    @FXML private Label breakfastCaloriesLabel;
    @FXML private Label lunchCaloriesLabel;
    @FXML private Label dinnerCaloriesLabel;
    @FXML private Label snackCaloriesLabel;

    @FXML private ListView<String> breakfastList;
    @FXML private ListView<String> lunchList;
    @FXML private ListView<String> dinnerList;
    @FXML private ListView<String> snackList;

    @FXML private LineChart<String, Number> caloriesChart;
    @FXML private LineChart<String, Number> macrosChart;

    @FXML private Label avgCaloriesLabel;
    @FXML private Label avgProteinLabel;
    @FXML private Label avgCarbsLabel;
    @FXML private Label avgFatLabel;

    @FXML private DatePicker historyDatePicker;
    @FXML private Label caloriesTitleLabel;
    @FXML private Label macrosTitleLabel;
    @FXML private Label summaryTitleLabel;

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @FXML
    private void initialize() {

        SidebarController.highlight("dashboard");

        if (dataManager == null) return;

        User u = dataManager.getActiveUser();
        if (helloLabel != null) {
            String name = (u == null || u.getName() == null || u.getName().isBlank()) ? "User" : u.getName().trim();
            helloLabel.setText("Hello, " + name + "!");
        }

        if (dateLabel != null) {
            dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        }

        if (breakfastList != null || lunchList != null || dinnerList != null || snackList != null) {
            refreshDashboard();
        }
        if (historyDatePicker != null) {
            historyDatePicker.setValue(LocalDate.now());
            historyDatePicker.valueProperty().addListener((obs, oldV, newV) -> {
                if (newV == null) return;
                showDailyDetails();
            });
        }

        if (caloriesChart != null || macrosChart != null) {
            showWeeklyTrends();
        }

    }

    private void refreshDashboard() {
        DayLog today = dataManager.getDayLog(LocalDate.now());
        if (today == null) return;

        Goal goal = dataManager.getActiveGoal();
        int calGoal = goal == null ? 2000 : goal.getTargetCalories();
        double pGoal = goal == null ? 150 : goal.getTargetProteinG();
        double cGoal = goal == null ? 200 : goal.getTargetCarbsG();
        double fGoal = goal == null ? 65 : goal.getTargetFatG();

        today.computeTotals();

        int cal = today.getTotalCalories();
        double p = today.getTotalProteinG();
        double c = today.getTotalCarbsG();
        double f = today.getTotalFatG();

        if (calorieProgressLabel != null) calorieProgressLabel.setText("%d / %d".formatted(cal, calGoal));
        if (calorieProgressBar != null) calorieProgressBar.setProgress(calGoal <= 0 ? 0 : Math.min(1.0, cal / (double) calGoal));
        if (calorieRemainingLabel != null) calorieRemainingLabel.setText("%d kcal remaining".formatted(Math.max(0, calGoal - cal)));

        if (proteinLabel != null) proteinLabel.setText("%.0fg / %.0fg".formatted(p, pGoal));
        if (proteinProgressBar != null) proteinProgressBar.setProgress(pGoal <= 0 ? 0 : Math.min(1.0, p / pGoal));

        if (carbsLabel != null) carbsLabel.setText("%.0fg / %.0fg".formatted(c, cGoal));
        if (carbsProgressBar != null) carbsProgressBar.setProgress(cGoal <= 0 ? 0 : Math.min(1.0, c / cGoal));

        if (fatLabel != null) fatLabel.setText("%.0fg / %.0fg".formatted(f, fGoal));
        if (fatProgressBar != null) fatProgressBar.setProgress(fGoal <= 0 ? 0 : Math.min(1.0, f / fGoal));

        Map<FoodLog.MealType, List<String>> byMeal = new EnumMap<>(FoodLog.MealType.class);
        Map<FoodLog.MealType, Double> kcalByMeal = new EnumMap<>(FoodLog.MealType.class);
        for (FoodLog.MealType mt : FoodLog.MealType.values()) {
            byMeal.put(mt, new ArrayList<>());
            kcalByMeal.put(mt, 0.0);
        }

        for (FoodLog log : today.getFoodLogs()) {
            if (log == null) continue;
            FoodLog.MealType mt = log.getMealType() == null ? FoodLog.MealType.SNACK : log.getMealType();
            String name = dataManager.resolveFoodNameById(log.getCustomFoodId());
            if (name == null || name.isBlank()) name = "Item";

            String line = "%s - %.0f kcal (%s)".formatted(name, log.gtCalories(), log.getFormattedTime());
            byMeal.get(mt).add(line);
            kcalByMeal.put(mt, kcalByMeal.get(mt) + log.gtCalories());
        }

        setMealList(breakfastList, byMeal.get(FoodLog.MealType.BREAKFAST));
        setMealList(lunchList, byMeal.get(FoodLog.MealType.LUNCH));
        setMealList(dinnerList, byMeal.get(FoodLog.MealType.DINNER));
        setMealList(snackList, byMeal.get(FoodLog.MealType.SNACK));

        if (breakfastCaloriesLabel != null) breakfastCaloriesLabel.setText("%.0f kcal".formatted(kcalByMeal.get(FoodLog.MealType.BREAKFAST)));
        if (lunchCaloriesLabel != null) lunchCaloriesLabel.setText("%.0f kcal".formatted(kcalByMeal.get(FoodLog.MealType.LUNCH)));
        if (dinnerCaloriesLabel != null) dinnerCaloriesLabel.setText("%.0f kcal".formatted(kcalByMeal.get(FoodLog.MealType.DINNER)));
        if (snackCaloriesLabel != null) snackCaloriesLabel.setText("%.0f kcal".formatted(kcalByMeal.get(FoodLog.MealType.SNACK)));
    }

    private void setMealList(ListView<String> view, List<String> items) {
        if (view == null) return;
        if (items == null) items = List.of();
        view.setItems(FXCollections.observableArrayList(items));
        if (items.isEmpty()) view.setItems(FXCollections.observableArrayList("No items added yet"));
    }

    @FXML public void goDashboard() { MacroMateApplication.switchScene("dashboard.fxml", "MacroMate"); }
    @FXML public void goAddFood() { MacroMateApplication.switchScene("add_food.fxml", "MacroMate - Add Food"); }
    @FXML public void goFoodLibrary() { MacroMateApplication.switchScene("food_library.fxml", "MacroMate - Food Library"); }
    @FXML public void goHistory() { MacroMateApplication.switchScene("history.fxml", "MacroMate - History"); }
    @FXML public void goSettings() { MacroMateApplication.switchScene("settings.fxml", "MacroMate - Settings"); }

    @FXML public void openAddBreakfast() { goAddFood(); }
    @FXML public void openAddLunch() { goAddFood(); }
    @FXML public void openAddDinner() { goAddFood(); }
    @FXML public void openAddSnack() { goAddFood(); }
    // Called by the Delete buttons in dashboard.fxml
    @FXML
    public void handleDeleteBreakfastItem() {
        deleteSelectedFrom(breakfastList, FoodLog.MealType.BREAKFAST);
    }

    @FXML
    public void handleDeleteLunchItem() {
        deleteSelectedFrom(lunchList, FoodLog.MealType.LUNCH);
    }

    @FXML
    public void handleDeleteDinnerItem() {
        deleteSelectedFrom(dinnerList, FoodLog.MealType.DINNER);
    }

    @FXML
    public void handleDeleteSnackItem() {
        deleteSelectedFrom(snackList, FoodLog.MealType.SNACK);
    }

    // Helper method to delete the selected item from a meal list
    private void deleteSelectedFrom(ListView<String> view, FoodLog.MealType mealType) {
        if (view == null || dataManager == null) return;

        // What the user clicked in the ListView
        String selected = view.getSelectionModel().getSelectedItem();
        if (selected == null || selected.isBlank() || "No items added yet".equals(selected)) {
            // Nothing useful selected
            return;
        }

        // Get today's DayLog (same one used to build the dashboard lists)
        DayLog today = dataManager.getDayLog(LocalDate.now());
        if (today == null) return;

        FoodLog toRemove = null;

        // Rebuild the *display text* exactly like refreshDashboard() does and match it
        for (FoodLog log : today.getFoodLogs()) {
            if (log == null) continue;

            FoodLog.MealType mt = (log.getMealType() == null)
                    ? FoodLog.MealType.SNACK
                    : log.getMealType();
            if (mt != mealType) continue;

            String name = dataManager.resolveFoodNameById(log.getCustomFoodId());
            if (name == null || name.isBlank()) name = "Item";

            String line = "%s - %.0f kcal (%s)".formatted(
                    name,
                    log.gtCalories(),
                    log.getFormattedTime()
            );

            if (line.equals(selected)) {
                toRemove = log;
                break;
            }
        }

        if (toRemove != null) {
            today.removeFoodLog(toRemove);  // updates totals in DayLog
            // Rebuild the lists, labels, and progress bars
            refreshDashboard();
        }
    }
    @FXML
    public void showWeeklyTrends() {
        if (dataManager == null) return;

        if (caloriesTitleLabel != null) caloriesTitleLabel.setText("Calorie Intake (Last 7 Days)");
        if (macrosTitleLabel != null) macrosTitleLabel.setText("Macronutrients (Last 7 Days)");
        if (summaryTitleLabel != null) summaryTitleLabel.setText("Weekly Summary");


        List<LocalDate> days = new ArrayList<>();
        for (int i = 6; i >= 0; i--) days.add(LocalDate.now().minusDays(i));

        if (caloriesChart != null) {
            caloriesChart.getData().clear();
            XYChart.Series<String, Number> s = new XYChart.Series<>();
            for (LocalDate d : days) {
                DayLog dl = dataManager.getDayLog(d);
                dl.computeTotals();
                s.getData().add(new XYChart.Data<>(d.format(DateTimeFormatter.ofPattern("MMM d")), dl.getTotalCalories()));
            }
            caloriesChart.getData().add(s);
        }

        if (macrosChart != null) {
            macrosChart.getData().clear();

            XYChart.Series<String, Number> pS = new XYChart.Series<>();
            pS.setName("Protein");
            XYChart.Series<String, Number> cS = new XYChart.Series<>();
            cS.setName("Carbs");
            XYChart.Series<String, Number> fS = new XYChart.Series<>();
            fS.setName("Fat");

            double sumCal = 0, sumP = 0, sumC = 0, sumF = 0;

            for (LocalDate d : days) {
                DayLog dl = dataManager.getDayLog(d);
                dl.computeTotals();

                String x = d.format(DateTimeFormatter.ofPattern("MMM d"));
                pS.getData().add(new XYChart.Data<>(x, dl.getTotalProteinG()));
                cS.getData().add(new XYChart.Data<>(x, dl.getTotalCarbsG()));
                fS.getData().add(new XYChart.Data<>(x, dl.getTotalFatG()));

                sumCal += dl.getTotalCalories();
                sumP += dl.getTotalProteinG();
                sumC += dl.getTotalCarbsG();
                sumF += dl.getTotalFatG();
            }

            macrosChart.getData().addAll(pS, cS, fS);

            if (avgCaloriesLabel != null) avgCaloriesLabel.setText("%d".formatted((int) Math.round(sumCal / 7.0)));
            if (avgProteinLabel != null) avgProteinLabel.setText("%dg".formatted((int) Math.round(sumP / 7.0)));
            if (avgCarbsLabel != null) avgCarbsLabel.setText("%dg".formatted((int) Math.round(sumC / 7.0)));
            if (avgFatLabel != null) avgFatLabel.setText("%dg".formatted((int) Math.round(sumF / 7.0)));
        }
    }

    @FXML
    public void showDailyDetails() {
        if (dataManager == null) return;

        LocalDate date = LocalDate.now();
        if (historyDatePicker != null && historyDatePicker.getValue() != null) {
            date = historyDatePicker.getValue();
        }
        if (caloriesTitleLabel != null) caloriesTitleLabel.setText("Calorie Intake (Today)");
        if (macrosTitleLabel != null) macrosTitleLabel.setText("Macronutrients (Today)");
        if (summaryTitleLabel != null) summaryTitleLabel.setText("Daily Summary");

        DayLog day = dataManager.getDayLog(date);
        if (day == null) return;

        Map<FoodLog.MealType, Double> calByMeal = new EnumMap<>(FoodLog.MealType.class);
        Map<FoodLog.MealType, Double> pByMeal = new EnumMap<>(FoodLog.MealType.class);
        Map<FoodLog.MealType, Double> cByMeal = new EnumMap<>(FoodLog.MealType.class);
        Map<FoodLog.MealType, Double> fByMeal = new EnumMap<>(FoodLog.MealType.class);

        for (FoodLog.MealType mt : FoodLog.MealType.values()) {
            calByMeal.put(mt, 0.0);
            pByMeal.put(mt, 0.0);
            cByMeal.put(mt, 0.0);
            fByMeal.put(mt, 0.0);
        }

        day.computeTotals();

        for (FoodLog log : day.getFoodLogs()) {
            if (log == null) continue;
            FoodLog.MealType mt = log.getMealType() == null ? FoodLog.MealType.SNACK : log.getMealType();

            calByMeal.put(mt, calByMeal.get(mt) + log.gtCalories());

            Map<String, Double> m = log.getMacros();
            pByMeal.put(mt, pByMeal.get(mt) + m.getOrDefault("proteinG", 0.0));
            cByMeal.put(mt, cByMeal.get(mt) + m.getOrDefault("carbsG", 0.0));
            fByMeal.put(mt, fByMeal.get(mt) + m.getOrDefault("fatG", 0.0));
        }

        if (caloriesChart != null) {
            caloriesChart.getData().clear();

            CategoryAxis xAxis = (CategoryAxis) caloriesChart.getXAxis();
            if (xAxis != null) xAxis.setLabel("Meal");

            XYChart.Series<String, Number> s = new XYChart.Series<>();
            s.setName(date.format(DateTimeFormatter.ofPattern("MMM d")));

            for (FoodLog.MealType mt : FoodLog.MealType.values()) {
                s.getData().add(new XYChart.Data<>(prettyMeal(mt), calByMeal.get(mt)));
            }

            caloriesChart.getData().add(s);
        }

        if (macrosChart != null) {
            macrosChart.getData().clear();

            CategoryAxis xAxis = (CategoryAxis) macrosChart.getXAxis();
            if (xAxis != null) xAxis.setLabel("Meal");

            XYChart.Series<String, Number> pS = new XYChart.Series<>();
            pS.setName("Protein");
            XYChart.Series<String, Number> cS = new XYChart.Series<>();
            cS.setName("Carbs");
            XYChart.Series<String, Number> fS = new XYChart.Series<>();
            fS.setName("Fat");

            for (FoodLog.MealType mt : FoodLog.MealType.values()) {
                String x = prettyMeal(mt);
                pS.getData().add(new XYChart.Data<>(x, pByMeal.get(mt)));
                cS.getData().add(new XYChart.Data<>(x, cByMeal.get(mt)));
                fS.getData().add(new XYChart.Data<>(x, fByMeal.get(mt)));
            }

            macrosChart.getData().addAll(pS, cS, fS);

            if (avgCaloriesLabel != null) avgCaloriesLabel.setText("%d".formatted(day.getTotalCalories()));
            if (avgProteinLabel != null) avgProteinLabel.setText("%dg".formatted((int) Math.round(day.getTotalProteinG())));
            if (avgCarbsLabel != null) avgCarbsLabel.setText("%dg".formatted((int) Math.round(day.getTotalCarbsG())));
            if (avgFatLabel != null) avgFatLabel.setText("%dg".formatted((int) Math.round(day.getTotalFatG())));
        }
    }

    private String prettyMeal(FoodLog.MealType mt) {
        return switch (mt) {
            case BREAKFAST -> "Breakfast";
            case LUNCH -> "Lunch";
            case DINNER -> "Dinner";
            case SNACK -> "Snack";
        };
    }

    @FXML
    public void handleLogout() {
        // Clear active user if needed
        if (dataManager != null) {
            dataManager.setActiveUser(null);
            dataManager.saveAllData();
        }

        MacroMateApplication.switchScene("login.fxml", "MacroMate - Login");
    }

}




