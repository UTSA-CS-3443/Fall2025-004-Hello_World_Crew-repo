package edu.utsa.cs3443.macromateapp.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a user's nutrition goal over a specified date range.
 * A {@code Goal} defines daily targets for calories, protein, carbohydrates,
 * and fat. Goals are used to guide daily intake and provide progress
 * comparison within the MacroMate application.
 *
 * <p>A goal is considered "active" only on dates between its start and end
 * (inclusive), as determined by {@link #isActive(LocalDate)}.</p>
 */
public class Goal implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Unique identifier for the goal. */
    private String id;

    /** Target daily calories. */
    private int targetCalories;

    /** Target daily protein (grams). */
    private double targetProteinG;

    /** Target daily carbohydrates (grams). */
    private double targetCarbsG;

    /** Target daily fats (grams). */
    private double targetFatG;

    /** Date on which the goal becomes active. */
    private LocalDate startDate;

    /** Date on which the goal expires. */
    private LocalDate endDate;

    /** Default constructor for serialization. */
    public Goal() {
    }

    /**
     * Constructs a fully specified nutrition goal.
     *
     * @param id unique goal ID
     * @param targetCalories calorie target per day
     * @param targetProteinG protein target (g)
     * @param targetCarbsG carbohydrate target (g)
     * @param targetFatG fat target (g)
     * @param startDate date the goal starts
     * @param endDate date the goal ends
     */
    public Goal(String id, int targetCalories, double targetProteinG, double targetCarbsG, double targetFatG, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.targetCalories = targetCalories;
        this.targetProteinG = targetProteinG;
        this.targetCarbsG = targetCarbsG;
        this.targetFatG = targetFatG;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns goal ID.
     *
     * @return goal ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns target calories per day.
     *
     * @return target calories per day
     */
    public int getTargetCalories() {
        return targetCalories;
    }

    /**
     * Returns target protein per day.
     *
     * @return target protein per day (grams)
     */
    public double getTargetProteinG() {
        return targetProteinG;
    }

    /**
     * Returns target carbs per day.
     *
     * @return target carbs per day (grams)
     */
    public double getTargetCarbsG() {
        return targetCarbsG;
    }

    /**
     * Returns  target fats per day.
     *
     * @return target fats per day (grams)
     */
    public double getTargetFatG() {
        return targetFatG;
    }

    /**
     * Returns goal start date.
     *
     * @return goal start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns goal end date.
     *
     * @return goal end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Sets goal ID.
     *
     * @param id set goal ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets calorie target.
     *
     * @param targetCalories set calorie target
     */
    public void setTargetCalories(int targetCalories) {
        this.targetCalories = targetCalories;
    }

    /**
     * Sets protein target.
     *
     * @param targetProteinG set protein target
     */
    public void setTargetProteinG(double targetProteinG) {
        this.targetProteinG = targetProteinG;
    }

    /**
     * Sets carbohydrate target.
     *
     * @param targetCarbsG set carbohydrate target
     */
    public void setTargetCarbsG(double targetCarbsG) {
        this.targetCarbsG = targetCarbsG;
    }

    /**
     * Sets fat target.
     *
     * @param targetFatG set fat target
     */
    public void setTargetFatG(double targetFatG) {
        this.targetFatG = targetFatG;
    }

    /**
     * Sets the starting date of this goal.
     *
     * @param startDate set goal start date
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Sets the ending date of this goal.
     *
     * @param endDate set goal end date
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Determines whether this goal applies to the given date.
     *
     * @param date date to check
     * @return true if date is within the goal range (inclusive), false otherwise
     */
    public boolean isActive(LocalDate date) {
        if (date == null || startDate == null || endDate == null) return false;
        return (!date.isBefore(startDate)) && (!date.isAfter(endDate));
    }

    /**
     * Calculates progress toward the calorie goal.
     * Returns a ratio between 0 and 1+ indicating how much of the goal has been met.
     *
     * @param consumed calories consumed today
     * @return fraction of the calorie goal achieved
     */
    public double progressCalories(double consumed) {
        if (targetCalories <= 0) return 0.0;
        return consumed / (double) targetCalories;
    }

    /**
     * Returns a human-readable summary of the goal including nutrient targets
     * and date range.
     *
     * @return summary string describing the full goal
     */
    public String summary() {
        return "Goal %s: %d kcal, P=%.0fg C=%.0fg F=%.0fg (%s to %s)"
                .formatted(id, targetCalories, targetProteinG, targetCarbsG, targetFatG, startDate, endDate);
    }

    /**
     * Returns formatted string of the goal.
     *
     * @return string form of the goal (delegates to {@link #summary()})
     */
    @Override
    public String toString() {
        return summary();
    }

    /**
     * Goals are equal if their IDs match.
     *
     * @param o   the reference object with which to compare.
     * @return equal ID
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Goal other)) return false;
        return Objects.equals(id, other.id);
    }

    /**
     * Returns has code of goal ID.
     *
     * @return hash code based on goal ID
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

