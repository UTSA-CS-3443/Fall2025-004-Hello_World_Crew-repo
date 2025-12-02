package edu.utsa.cs3443.macromateapp.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DayLog implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private LocalDate date;
    private List<FoodLog> foodLogs;

    private String userId;

    private int totalCalories;
    private double totalProteinG;
    private double totalCarbsG;
    private double totalFatG;

    public DayLog() {
        this("", "", LocalDate.now());
    }

    public DayLog(String id, LocalDate date) {
        this(id, "", date);
    }

    public DayLog(String id, String userId, LocalDate date) {
        this.id = id;
        this.userId = (userId == null) ? "" : userId;
        this.date = date;
        this.foodLogs = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<FoodLog> getFoodLogs() {
        return foodLogs;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public double getTotalProteinG() {
        return totalProteinG;
    }

    public double getTotalCarbsG() {
        return totalCarbsG;
    }

    public double getTotalFatG() {
        return totalFatG;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = (userId == null) ? "" : userId; }

    public void addFoodLog(FoodLog log) {
        if (log == null) return;
        foodLogs.add(log);
        computeTotals();
    }

    public void removeFoodLog(FoodLog log) {
        if (log == null) return;
        foodLogs.remove(log);
        computeTotals();
    }

    public void computeTotals() {
        double cals = 0.0, p = 0.0, c = 0.0, f = 0.0;

        for (FoodLog log : foodLogs) {
            if (log == null) continue;
            cals += log.gtCalories();
            Map<String, Double> m = log.getMacros();
            p += m.getOrDefault("proteinG", 0.0);
            c += m.getOrDefault("carbsG", 0.0);
            f += m.getOrDefault("fatG", 0.0);
        }

        totalCalories = (int) Math.round(cals);
        totalProteinG = p;
        totalCarbsG = c;
        totalFatG = f;
    }

    public Map<String, Double> getTotals() {
        Map<String, Double> out = new LinkedHashMap<>();
        out.put("calories", (double) totalCalories);
        out.put("proteinG", totalProteinG);
        out.put("carbsG", totalCarbsG);
        out.put("fatG", totalFatG);
        return out;
    }
}

