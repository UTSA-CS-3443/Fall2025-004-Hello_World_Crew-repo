package edu.utsa.cs3443.macromateapp.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a daily nutrition log for a specific user and date.
 * A {@code DayLog} stores all {@link FoodLog} entries recorded on that date
 * and computes aggregate totals such as calories, protein, carbs, and fat.
 *
 * <p>This class is stored persistently through {@link DataManager} and is
 * automatically created whenever a user logs food for a new date.</p>
 */
public class DayLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Unique identifier for this DayLog. */
    private String id;

    /** The date associated with this log. */
    private LocalDate date;

    /** List of all food entries consumed on this date. */
    private List<FoodLog> foodLogs;

    /** ID of the user this log belongs to. */
    private String userId;

    /** Computed total calories for the day. */
    private int totalCalories;

    /** Computed total protein (grams) for the day. */
    private double totalProteinG;

    /** Computed total carbohydrates (grams) for the day. */
    private double totalCarbsG;

    /** Computed total fats (grams) for the day. */
    private double totalFatG;

    /**
     * Default constructor used for serialization. Creates a log for the current date with no user assigned.
     */
    public DayLog() {
        this("", "", LocalDate.now());
    }

    /**
     * Creates a DayLog for the given ID and date with no associated user.
     *
     * @param id unique identifier
     * @param date date of the log
     */
    public DayLog(String id, LocalDate date) {
        this(id, "", date);
    }

    /**
     * Creates a DayLog for a specific user and date.
     *
     * @param id unique identifier
     * @param userId user the log belongs to
     * @param date date represented by this log
     */
    public DayLog(String id, String userId, LocalDate date) {
        this.id = id;
        this.userId = (userId == null) ? "" : userId;
        this.date = date;
        this.foodLogs = new ArrayList<>();
    }

    /**
     * Returns ID for DayLog.
     *
     * @return unique ID for this DayLog
     */
    public String getId() {
        return id;
    }

    /**
     * Returns date of DayLog.
     *
     * @return date of the log
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns list of food entries by date.
     *
     * @return list of all food entries for this date
     */
    public List<FoodLog> getFoodLogs() {
        return foodLogs;
    }

    /**
     * Returns total calories by date.
     *
     * @return total calories consumed on this date
     */
    public int getTotalCalories() {
        return totalCalories;
    }

    /**
     * Returns total protein.
     *
     * @return total protein (g) consumed
     */
    public double getTotalProteinG() {
        return totalProteinG;
    }

    /**
     * Returns total carbohydrates.
     *
     * @return total carbohydrates (g) consumed
     */
    public double getTotalCarbsG() {
        return totalCarbsG;
    }

    /**
     * Returns total fat.
     *
     * @return total fats (g) consumed
     */
    public double getTotalFatG() {
        return totalFatG;
    }

    /**
     * Sets the log ID.
     *
     * @param id Log ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the log date.
     *
     * @param date set log date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Returns userID of log.
     *
     * @return ID of the user this log belongs to
     */
    public String getUserId() { return userId; }

    /**
     * Sets the log's associated user.
     *
     * @param userId set UserID
     */
    public void setUserId(String userId) { this.userId = (userId == null) ? "" : userId; }

    /**
     * Adds a food entry to this day and recomputes aggregate totals.
     *
     * @param log the food entry to add
     */
    public void addFoodLog(FoodLog log) {
        if (log == null) return;
        foodLogs.add(log);
        computeTotals();
    }

    /**
     * Removes a food entry from this day and recomputes totals.
     *
     * @param log food entry to remove
     */
    public void removeFoodLog(FoodLog log) {
        if (log == null) return;
        foodLogs.remove(log);
        computeTotals();
    }

    /**
     * Recalculates daily totals (calories, protein, carbs, fat) based on all tracked {@link FoodLog} entries.
     */
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

    /**
     * Returns a map containing total calories, protein, carbs, and fats
     * for UI display or serialization.
     *
     * Map keys:
     * <ul>
     *     <li>"calories"</li>
     *     <li>"proteinG"</li>
     *     <li>"carbsG"</li>
     *     <li>"fatG"</li>
     * </ul>
     *
     * @return map of daily nutrient totals
     */
    public Map<String, Double> getTotals() {
        Map<String, Double> out = new LinkedHashMap<>();
        out.put("calories", (double) totalCalories);
        out.put("proteinG", totalProteinG);
        out.put("carbsG", totalCarbsG);
        out.put("fatG", totalFatG);
        return out;
    }
}

