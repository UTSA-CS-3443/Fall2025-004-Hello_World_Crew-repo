package edu.utsa.cs3443.macromateapp.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Food implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String brand;
    private String category;

    private double servingSize;
    private double calories;
    private double proteinG;
    private double carbsG;
    private double fatG;

    public Food() {
    }

    public Food(String id, String name, String brand, String category, double servingSize,
                double calories, double proteinG, double carbsG, double fatG) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.servingSize = servingSize;
        this.calories = calories;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.fatG = fatG;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
    }

    public double getServingSize() {
        return servingSize;
    }

    public double getCalories() {
        return calories;
    }

    public double getProteinG() {
        return proteinG;
    }

    public double getCarbsG() {
        return carbsG;
    }

    public double getFatG() {
        return fatG;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setServingSize(double servingSize) {
        this.servingSize = servingSize;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setProteinG(double proteinG) {
        this.proteinG = proteinG;
    }

    public void setCarbsG(double carbsG) {
        this.carbsG = carbsG;
    }

    public void setFatG(double fatG) {
        this.fatG = fatG;
    }

    public double getCaloriesForServings(double servings) {
        return calories * Math.max(0.0, servings);
    }

    public Map<String, Double> getMacrosForServings(double servings) {
        double s = Math.max(0.0, servings);
        Map<String, Double> out = new LinkedHashMap<>();
        out.put("proteinG", proteinG * s);
        out.put("carbsG", carbsG * s);
        out.put("fatG", fatG * s);
        return out;
    }

    public double getCaloriesForGrams(Double grams) {
        double g = grams == null ? 0.0 : grams;
        if (servingSize <= 0.0) return 0.0;
        double servings = g / servingSize;
        return getCaloriesForServings(servings);
    }

    public Map<String, Double> getMacrosForGrams(Double grams) {
        double g = grams == null ? 0.0 : grams;
        if (servingSize <= 0.0) {
            Map<String, Double> out = new LinkedHashMap<>();
            out.put("proteinG", 0.0);
            out.put("carbsG", 0.0);
            out.put("fatG", 0.0);
            return out;
        }
        double servings = g / servingSize;
        return getMacrosForServings(servings);
    }

    @Override
    public String toString() {
        String n = (name == null || name.isBlank()) ? "Food" : name.trim();
        return "%s (%d kcal/serving)".formatted(n, (int) Math.round(calories));
    }
}


