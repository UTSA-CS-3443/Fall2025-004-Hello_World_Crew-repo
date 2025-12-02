package edu.utsa.cs3443.macromateapp.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Goal implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private int targetCalories;
    private double targetProteinG;
    private double targetCarbsG;
    private double targetFatG;
    private LocalDate startDate;
    private LocalDate endDate;

    public Goal() {
    }

    public Goal(String id, int targetCalories, double targetProteinG, double targetCarbsG, double targetFatG, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.targetCalories = targetCalories;
        this.targetProteinG = targetProteinG;
        this.targetCarbsG = targetCarbsG;
        this.targetFatG = targetFatG;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public int getTargetCalories() {
        return targetCalories;
    }

    public double getTargetProteinG() {
        return targetProteinG;
    }

    public double getTargetCarbsG() {
        return targetCarbsG;
    }

    public double getTargetFatG() {
        return targetFatG;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTargetCalories(int targetCalories) {
        this.targetCalories = targetCalories;
    }

    public void setTargetProteinG(double targetProteinG) {
        this.targetProteinG = targetProteinG;
    }

    public void setTargetCarbsG(double targetCarbsG) {
        this.targetCarbsG = targetCarbsG;
    }

    public void setTargetFatG(double targetFatG) {
        this.targetFatG = targetFatG;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive(LocalDate date) {
        if (date == null || startDate == null || endDate == null) return false;
        return (!date.isBefore(startDate)) && (!date.isAfter(endDate));
    }

    public double progressCalories(double consumed) {
        if (targetCalories <= 0) return 0.0;
        return consumed / (double) targetCalories;
    }

    public String summary() {
        return "Goal %s: %d kcal, P=%.0fg C=%.0fg F=%.0fg (%s to %s)"
                .formatted(id, targetCalories, targetProteinG, targetCarbsG, targetFatG, startDate, endDate);
    }

    @Override
    public String toString() {
        return summary();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Goal other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

