package edu.utsa.cs3443.macromateapp.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class FoodLog implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String CustomFoodId;
    private MealType mealType;
    private double servings;
    private LocalDateTime timestamp;
    private String notes;

    private double calories;
    private double proteinG;
    private double carbsG;
    private double fatG;

    public FoodLog() {
    }

    public FoodLog(String id, String foodId, MealType mealType, double servings, LocalDateTime timestamp, String notes) {
        this.id = id;
        this.CustomFoodId = foodId;
        this.mealType = mealType;
        this.servings = servings;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public String getCustomFoodId() {
        return CustomFoodId;
    }

    public MealType getMealType() {
        return mealType;
    }

    public double getServings() {
        return servings;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getNotes() {
        return notes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCustomFoodId(String foodId) {
        this.CustomFoodId = foodId;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public void setServings(double servings) {
        this.servings = servings;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    void setComputedTotals(double calories, double proteinG, double carbsG, double fatG) {
        this.calories = calories;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.fatG = fatG;
    }

    public double gtCalories() {
        return calories;
    }

    public Map<String, Double> getMacros() {
        Map<String, Double> out = new LinkedHashMap<>();
        out.put("proteinG", proteinG);
        out.put("carbsG", carbsG);
        out.put("fatG", fatG);
        return out;
    }

    public String getFormattedTime() {
        if (timestamp == null) return "";
        return timestamp.format(DateTimeFormatter.ofPattern("h:mm a"));
    }

    @Override
    public String toString() {
        String time = getFormattedTime();
        String meal = mealType == null ? "" : mealType.name();
        return "%s  %s  %.0f kcal".formatted(meal, time, calories);
    }

    public enum MealType {
        BREAKFAST,
        LUNCH,
        DINNER,
        SNACK
    }
}