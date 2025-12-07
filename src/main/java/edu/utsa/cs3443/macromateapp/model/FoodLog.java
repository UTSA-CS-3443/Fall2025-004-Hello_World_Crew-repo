package edu.utsa.cs3443.macromateapp.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a single logged food entry within a {@link DayLog}.
 * A {@code FoodLog} records what the user ate, when they ate it, how many servings
 * were consumed, the meal type (breakfast, lunch, etc.), optional notes, and
 * computed nutritional totals (calories, protein, carbs, fat).
 *
 * <p>Calorie and macronutrient totals are calculated externally by
 * {@link DataManager} when creating logs from {@link Food} or {@link CustomFood},
 * and stored using {@link #setComputedTotals(double, double, double, double)}.</p>
 */
public class FoodLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Unique identifier for the log entry. */
    private String id;

    /** ID of the associated food item (standard Food or CustomFood). */
    private String CustomFoodId;

    /** Type of meal this entry belongs to (breakfast, lunch, dinner, snack). */
    private MealType mealType;

    /** Number of servings consumed. */
    private double servings;

    /** Timestamp of when the food was consumed. */
    private LocalDateTime timestamp;

    /** Optional user-entered notes about the meal. */
    private String notes;

    /** Total calories for this log entry. */
    private double calories;

    /** Total protein in grams. */
    private double proteinG;

    /** Total carbohydrates in grams. */
    private double carbsG;

    /** Total fat in grams. */
    private double fatG;

    /** Default constructor for serialization. */
    public FoodLog() {
    }

    /**
     * Constructs a FoodLog describing a food consumed at a specific time.
     *
     * @param id unique log ID
     * @param foodId ID of the food item (standard or custom)
     * @param mealType meal category
     * @param servings number of servings consumed
     * @param timestamp timestamp of consumption
     * @param notes optional notes
     */
    public FoodLog(String id, String foodId, MealType mealType, double servings, LocalDateTime timestamp, String notes) {
        this.id = id;
        this.CustomFoodId = foodId;
        this.mealType = mealType;
        this.servings = servings;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    /**
     * Returns ID for food log.
     *
     * @return unique ID for this food log
     */
    public String getId() {
        return id;
    }

    /**
     * Returns ID of the food.
     *
     * @return ID of the associated food or custom food
     */
    public String getCustomFoodId() {
        return CustomFoodId;
    }

    /**
     * Returns meal type.
     *
     * @return meal type for this entry
     */
    public MealType getMealType() {
        return mealType;
    }

    /**
     * Returns number of servings.
     *
     * @return number of servings
     */
    public double getServings() {
        return servings;
    }

    /**
     * Returns timestamp of the entry.
     *
     * @return timestamp of the consumed entry
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns user notes.
     *
     * @return user notes associated with the entry
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets log ID.
     *
     * @param id set log ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets associated food ID.
     *
     * @param foodId set food ID
     */
    public void setCustomFoodId(String foodId) {
        this.CustomFoodId = foodId;
    }

    /**
     * Sets meal type.
     *
     * @param mealType set meal type
     */
    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    /**
     * Sets serving count.
     *
     * @param servings set serving count
     */
    public void setServings(double servings) {
        this.servings = servings;
    }

    /**
     * Sets timestamp.
     *
     * @param timestamp set timestamp.
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Sets user notes.
     *
     * @param notes set user notes.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Stores precomputed nutrient totals for this entry.
     * Called by {@link DataManager#createFoodLogFromFood} or {@link DataManager#createFoodLogFromCustomFood}.
     *
     * @param calories total calories
     * @param proteinG total protein (g)
     * @param carbsG total carbs (g)
     * @param fatG total fat (g)
     */
    void setComputedTotals(double calories, double proteinG, double carbsG, double fatG) {
        this.calories = calories;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.fatG = fatG;
    }

    /**
     * Returns calories logged for entry.
     *
     * @return total calories logged for this entry
     */
    public double gtCalories() {
        return calories;
    }

    /**
     * Returns macronutrient values in a map format for UI or processing.
     *
     * Keys:
     * <ul>
     *     <li>"proteinG"</li>
     *     <li>"carbsG"</li>
     *     <li>"fatG"</li>
     * </ul>
     *
     * @return map of macros for this food log
     */
    public Map<String, Double> getMacros() {
        Map<String, Double> out = new LinkedHashMap<>();
        out.put("proteinG", proteinG);
        out.put("carbsG", carbsG);
        out.put("fatG", fatG);
        return out;
    }

    /**
     * Returns a formatted string version of the timestamp, e.g. "3:42 PM".
     *
     * @return formatted time or empty string if timestamp is null
     */
    public String getFormattedTime() {
        if (timestamp == null) return "";
        return timestamp.format(DateTimeFormatter.ofPattern("h:mm a"));
    }

    /**
     * String summary of the food log entry, showing meal type, time, and calories.
     *
     * @return formatted string summary.
     */
    @Override
    public String toString() {
        String time = getFormattedTime();
        String meal = mealType == null ? "" : mealType.name();
        return "%s  %s  %.0f kcal".formatted(meal, time, calories);
    }

    /**
     * Enumerates meal categories that a logged food entry can belong to.
     */
    public enum MealType {
        BREAKFAST,
        LUNCH,
        DINNER,
        SNACK
    }
}