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

public class CustomFood implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;

    private String nameValue;
    private transient StringProperty name;

    private String descriptionValue;
    private transient StringProperty description;

    private List<AbstractMap.SimpleEntry<Food, Double>> ingredients;

    public CustomFood() {
        this("", "", "", "");
    }

    public CustomFood(String id, String userId, String name, String description) {
        this.id = id;
        this.userId = userId;
        this.nameValue = name;
        this.descriptionValue = description;
        this.ingredients = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty(nameValue);
            name.addListener((o, a, b) -> nameValue = b);
        }
        return name;
    }

    public StringProperty descriptionProperty() {
        if (description == null) {
            description = new SimpleStringProperty(descriptionValue);
            description.addListener((o, a, b) -> descriptionValue = b);
        }
        return description;
    }

    public String getName() {
        return nameValue;
    }

    public String getDescription() {
        return descriptionValue;
    }

    public List<AbstractMap.SimpleEntry<Food, Double>> getIngredients() {
        return ingredients;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String v) {
        nameValue = v;
        if (name != null) name.set(v);
    }

    public void setDescription(String v) {
        descriptionValue = v;
        if (description != null) description.set(v);
    }

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

    public void removeIngredient(Food food) {
        if (food == null) return;
        ingredients.removeIf(e -> e != null && e.getKey() != null && e.getKey().getId().equals(food.getId()));
    }

    public double computerCalories() {
        double total = 0.0;
        for (var e : ingredients) {
            if (e == null || e.getKey() == null) continue;
            total += e.getKey().getCaloriesForGrams(e.getValue());
        }
        return total;
    }

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


