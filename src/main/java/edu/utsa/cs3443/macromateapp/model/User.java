package edu.utsa.cs3443.macromateapp.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;

    private String nameValue;
    private transient StringProperty name;

    private String goal;
    private int age;
    private Sex sex;
    private double heightIn;
    private double weightLb;
    private ActivityLevel activityLevel;

    public User() {
        this("", "", "", 0, Sex.OTHER, 0.0, 0.0, ActivityLevel.SEDENTARY);
    }

    public User(String id, String name, String goal, int age, Sex sex, double heightIn, double weightLb, ActivityLevel activityLevel) {
        this.id = id;
        this.nameValue = name;
        this.goal = goal;
        this.age = age;
        this.sex = sex;
        this.heightIn = heightIn;
        this.weightLb = weightLb;
        this.activityLevel = activityLevel;
    }

    public String getId() {
        return id;
    }

    public StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty(nameValue);
            name.addListener((o, a, b) -> nameValue = b);
        }
        return name;
    }

    public String getName() {
        return nameValue;
    }

    public String getGoal() {
        return goal;
    }

    public int getAge() {
        return age;
    }

    public Sex getSex() {
        return sex;
    }

    public double getHeightIn() {
        return heightIn;
    }

    public double getWeightLb() {
        return weightLb;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.nameValue = name;
        if (this.name != null) this.name.set(name);
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public void setHeightIn(double heightIn) {
        this.heightIn = heightIn;
    }

    public void setWeightLb(double weightLb) {
        this.weightLb = weightLb;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    public double calculateBMR() {
        double weightKg = weightLb * 0.45359237;
        double heightCm = heightIn * 2.54;

        double base = 10.0 * weightKg + 6.25 * heightCm - 5.0 * age;

        if (sex == Sex.MALE) return base + 5.0;
        if (sex == Sex.FEMALE) return base - 161.0;
        return base;
    }

    public double calculateTDEE() {
        double mult = (activityLevel == null) ? ActivityLevel.SEDENTARY.getMultiplier() : activityLevel.getMultiplier();
        return calculateBMR() * mult;
    }

    @Override
    public String toString() {
        return "User{id='%s', name='%s', goal='%s', age=%d, sex=%s, heightIn=%.2f, weightLb=%.2f, activityLevel=%s}"
                .formatted(id, nameValue, goal, age, sex, heightIn, weightLb, activityLevel);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

enum Sex {
    MALE,
    FEMALE,
    OTHER
}

enum ActivityLevel {
    SEDENTARY(1.2),
    LIGHT(1.375),
    MODERATE(1.55),
    VERY_ACTIVE(1.725),
    EXTRA_ACTIVE(1.9);

    private final double multiplier;

    ActivityLevel(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
