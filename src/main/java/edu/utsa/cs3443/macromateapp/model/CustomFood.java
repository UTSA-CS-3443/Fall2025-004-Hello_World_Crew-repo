package edu.utsa.cs3443.macromateapp.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a user-created food item composed of multiple base {@link Food} objects.
 * A CustomFood stores a name, description, and a list of ingredients, where each ingredient is paired with its quantity in grams.
 * This class supports JavaFX properties for dynamic UI binding and also includes methods for computing total calories and macronutrients.
 *
 * <p>This class is serializable. JavaFX properties are marked transient and synchronized with their underlying string values to avoid serialization issues.</p>
 */
public class CustomFood implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Unique ID for this CustomFood entry. */
    private String id;

    /** The ID of the user who created this CustomFood. */
    private String userId;

    /** Backing value for the JavaFX-bound food name. */
    private String nameValue;

    /** JavaFX property for food name (lazy-loaded). */
    private transient StringProperty name;

    /** Backing value for the JavaFX-bound food description. */
    private String descriptionValue;

    /** JavaFX property for food description (lazy-loaded). */
    private transient StringProperty description;

    /** List of ingredients paired with their respective gram amounts. */
    private List<AbstractMap.SimpleEntry<Food, Double>> ingredients;

    /**
     * Default constructor that initializes a blank custom food entry.
     */
    public CustomFood() {
        this("", "", "", "");
    }

    /**
     * Constructs a customizable food item.
     *
     * @param id unique identifier for this CustomFood
     * @param userId ID of the user who created it
     * @param name display name of the food
     * @param description optional description of the food
     */
    public CustomFood(String id, String userId, String name, String description) {
        this.id = id;
        this.userId = userId;
        this.nameValue = name;
        this.descriptionValue = description;
        this.ingredients = new ArrayList<>();
    }

    /**
     * Returns the ID for CustomFood
     *
     * @return the unique ID for this CustomFood
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the UserID for created CustomFood
     *
     * @return the ID of the user who created this CustomFood
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Returns the JavaFX property for the name, creating it if necessary.
     * This allows UI components to bind directly to the name field.
     *
     * @return JavaFX name property
     */
    public StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty(nameValue);
            name.addListener((o, a, b) -> nameValue = b);
        }
        return name;
    }

    /**
     * Returns the JavaFX property for the description, creating it if necessary.
     *
     * @return JavaFX description property
     */
    public StringProperty descriptionProperty() {
        if (description == null) {
            description = new SimpleStringProperty(descriptionValue);
            description.addListener((o, a, b) -> descriptionValue = b);
        }
        return description;
    }

    /**
     * Returns the name of CustomFood
     *
     * @return the name of this CustomFood
     */
    public String getName() {
        return nameValue;
    }

    /**
     * Returns the description of CustomFood
     *
     * @return the description of this CustomFood
     */
    public String getDescription() {
        return descriptionValue;
    }

    /**
     * Returns list of entry information
     *
     * @return list of ingredient entries, each containing a Food and gram amount
     */
    public List<AbstractMap.SimpleEntry<Food, Double>> getIngredients() {
        return ingredients;
    }

    /**
     * Sets a new ID for this item.
     *
     * @param id new unique identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the user ID associated with this item.
     *
     * @param userId new user ID to assign
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Sets the display name for this CustomFood. Updates both the stored value and the bound JavaFX property (if already initialized).
     *
     * @param v new name value
     */
    public void setName(String v) {
        nameValue = v;
        if (name != null) name.set(v);
    }

    /**
     * Sets the description for this CustomFood.
     *
     * @param v new description value
     */
    public void setDescription(String v) {
        descriptionValue = v;
        if (description != null) description.set(v);
    }

    /**
     * Adds an ingredient to this CustomFood. If the ingredient already exists, its gram value is increased instead of creating a duplicate entry.
     *
     * @param food the Food ingredient being added
     * @param grams the amount in grams for this ingredient (must be positive)
     */
    public void addIngredient(Food food, double grams) {
        if (food == null) return;
        if (grams <= 0) return;

        for (int i = 0; i < ingredients.size(); i++) {
            var e = ingredients.get(i);
            if (e != null && e.getKey() != null && e.getKey().getId().equals(food.getId())) {
                ingredients.set(i, new AbstractMap.SimpleEntry<>(food, e.getValue() + grams));
                return;
            }
        }
        ingredients.add(new AbstractMap.SimpleEntry<>(food, grams));
    }

    /**
     * Removes the specified ingredient from this CustomFood.
     *
     * @param food the Food to remove
     */
    public void removeIngredient(Food food) {
        if (food == null) return;
        ingredients.removeIf(e -> e != null && e.getKey() != null && e.getKey().getId().equals(food.getId()));
    }

    /**
     * Computes the total caloric content of this CustomFood based on each ingredient and its gram amount.
     *
     * @return total calories for the custom entry
     */
    public double computerCalories() {
        double total = 0.0;
        for (var e : ingredients) {
            if (e == null || e.getKey() == null) continue;
            total += e.getKey().getCaloriesForGrams(e.getValue());
        }
        return total;
    }

    /**
     * Computes the summed macronutrients (protein, carbs, fat) for this CustomFood.
     * The result uses standard map keys: "proteinG", "carbsG", and "fatG".
     *
     * @return a map of macronutrient totals for the entire custom food item
     */
    public Map<String, Double> computerMacros() {
        double p = 0.0, c = 0.0, f = 0.0;
        for (var e : ingredients) {
            if (e == null || e.getKey() == null) continue;
            Map<String, Double> m = e.getKey().getMacrosForGrams(e.getValue());
            p += m.getOrDefault("proteinG", 0.0);
            c += m.getOrDefault("carbsG", 0.0);
            f += m.getOrDefault("fatG", 0.0);
        }
        Map<String, Double> out = new LinkedHashMap<>();
        out.put("proteinG", p);
        out.put("carbsG", c);
        out.put("fatG", f);
        return out;
    }
}


