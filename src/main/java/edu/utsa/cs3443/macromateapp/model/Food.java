package edu.utsa.cs3443.macromateapp.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a standard food item in the MacroMate database. A {@code Food}
 * includes identifying information such as name, brand, category, and detailed
 * nutritional information based on a single serving size.
 *
 * <p>The class provides helper methods to compute calories and macronutrients
 * (protein, carbs, fat) for either a given number of servings or a given weight
 * in grams. These methods are used throughout the application to calculate totals
 * for logged meals.</p>
 */
public class Food implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Unique identifier for this food item. */
    private String id;

    /** Name or description of the food (e.g., "Chicken Breast"). */
    private String name;

    /** Brand associated with the food (e.g., "Generic", "Kroger"). */
    private String brand;

    /** Category of the food (e.g., "Protein", "Carb", "Veg"). */
    private String category;

    /** Size of one serving, typically in grams. */
    private double servingSize;

    /** Calories per serving. */
    private double calories;

    /** Protein in grams per serving. */
    private double proteinG;

    /** Carbohydrates in grams per serving. */
    private double carbsG;

    /** Fat in grams per serving. */
    private double fatG;

    /**
     * Default constructor for serialization.
     */
    public Food() {
    }

    /**
     * Constructs a new Food item with full nutritional data.
     *
     * @param id unique identifier
     * @param name food name
     * @param brand food brand
     * @param category nutritional category
     * @param servingSize serving size (in grams or defined unit)
     * @param calories calories per serving
     * @param proteinG protein (grams per serving)
     * @param carbsG carbohydrates (grams per serving)
     * @param fatG fat (grams per serving)
     */
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

    /**
     * Returns food ID.
     *
     * @return food ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns food name.
     *
     * @return food name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns brand name.
     *
     * @return brand name
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Returns food category.
     *
     * @return food category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns serving size.
     *
     * @return serving size
     */
    public double getServingSize() {
        return servingSize;
    }

    /**
     * Returns calories per serving.
     *
     * @return calories per serving
     */
    public double getCalories() {
        return calories;
    }

    /**
     * Returns protein grams per serving.
     *
     * @return protein grams per serving
     */
    public double getProteinG() {
        return proteinG;
    }

    /**
     * Returns carbohydrate grams per serving.
     *
     * @return carbohydrate grams per serving
     */
    public double getCarbsG() {
        return carbsG;
    }

    /**
     * Returns fat grams per serving.
     *
     * @return fat grams per serving
     */
    public double getFatG() {
        return fatG;
    }

    /**
     * Sets food ID.
     *
     * @param id food ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets food name.
     * @param name set food name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets brand name.
     *
     * @param brand set brand name
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Sets category label
     *
     * @param category set category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Sets serving size.
     *
     * @param servingSize set serving size
     */
    public void setServingSize(double servingSize) {
        this.servingSize = servingSize;
    }

    /**
     * Sets calories per serving.
     *
     * @param calories set calories per serving
     */
    public void setCalories(double calories) {
        this.calories = calories;
    }

    /**
     * Sets protein grams per serving.
     *
     * @param proteinG set protein per serving
     */
    public void setProteinG(double proteinG) {
        this.proteinG = proteinG;
    }

    /**
     * Sets carbohydrate grams per serving.
     *
     * @param carbsG set carbohydrates per serving
     */
    public void setCarbsG(double carbsG) {
        this.carbsG = carbsG;
    }

    /**
     * Sets fat grams per serving.
     *
     * @param fatG set fat grams per serving
     */
    public void setFatG(double fatG) {
        this.fatG = fatG;
    }

    /**
     * Computes total calories for the specified number of servings.
     *
     * @param servings number of servings consumed
     * @return calories for the given serving amount
     */
    public double getCaloriesForServings(double servings) {
        return calories * Math.max(0.0, servings);
    }

    /**
     * Computes macronutrients for the specified number of servings.
     *
     * @param servings number of servings consumed
     * @return map containing "proteinG", "carbsG", and "fatG"
     */
    public Map<String, Double> getMacrosForServings(double servings) {
        double s = Math.max(0.0, servings);
        Map<String, Double> out = new LinkedHashMap<>();
        out.put("proteinG", proteinG * s);
        out.put("carbsG", carbsG * s);
        out.put("fatG", fatG * s);
        return out;
    }

    /**
     * Computes calories for a given food weight in grams. Converts grams -> servings -> calories.
     *
     * @param grams weight in grams
     * @return calories for the given gram amount
     */
    public double getCaloriesForGrams(Double grams) {
        double g = grams == null ? 0.0 : grams;
        if (servingSize <= 0.0) return 0.0;
        double servings = g / servingSize;
        return getCaloriesForServings(servings);
    }

    /**
     * Computes macronutrients for a given gram amount. Converts grams -> servings -> macro totals.
     *
     * @param grams weight in grams
     * @return map containing "proteinG", "carbsG", and "fatG"
     */
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

    /**
     * Return string showing name and calories per serving.
     *
     * @return readable string showing the name and calories per serving
     */
    @Override
    public String toString() {
        String n = (name == null || name.isBlank()) ? "Food" : name.trim();
        return "%s (%d kcal/serving)".formatted(n, (int) Math.round(calories));
    }
}


