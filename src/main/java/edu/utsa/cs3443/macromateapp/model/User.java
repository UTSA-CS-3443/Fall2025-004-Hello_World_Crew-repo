package edu.utsa.cs3443.macromateapp.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a user account within the MacroMate application.
 * A {@code User} stores personal profile information such as name, age, sex,
 * height, weight, daily activity level, and nutritional goals. This data is
 * used to compute BMR (Basal Metabolic Rate) and TDEE (Total Daily Energy Expenditure)
 * for personalized calorie recommendations.
 *
 * <p>The user's name supports JavaFX binding via {@link StringProperty}, while
 * other attributes are stored normally for serialization.</p>
 */
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Unique identifier for the user (typically an email). */
    private String id;

    /** Backing value for the JavaFX name property. */
    private String nameValue;

    /** JavaFX-bound display name (lazy initialized). */
    private transient StringProperty name;

    /** User's stated health or fitness goal (e.g., "Lose Weight"). */
    private String goal;

    /** User's age in years. */
    private int age;

    /** Biological sex used for BMR calculation. */
    private Sex sex;

    /** User's height in inches. */
    private double heightIn;

    /** User's weight in pounds. */
    private double weightLb;

    /** Daily activity level used to compute TDEE. */
    private ActivityLevel activityLevel;

    /**
     * Default constructor initializing a blank user profile.
     */
    public User() {
        this("", "", "", 0, Sex.OTHER, 0.0, 0.0, ActivityLevel.SEDENTARY);
    }

    /**
     * Creates a new User with all profile fields supplied.
     *
     * @param id unique user ID
     * @param name display name
     * @param goal fitness or nutrition goal
     * @param age age in years
     * @param sex biological sex
     * @param heightIn height in inches
     * @param weightLb weight in pounds
     * @param activityLevel daily activity level multiplier
     */
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

    /**
     * Returns the user ID.
     *
     * @return user ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the JavaFX property for the user's display name, creating it if needed.
     *
     * @return JavaFX-bound name property
     */
    public StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty(nameValue);
            name.addListener((o, a, b) -> nameValue = b);
        }
        return name;
    }

    /**
     * Returns the user's display name.
     *
     * @return user's display name
     */
    public String getName() {
        return nameValue;
    }

    /**
     * Returns the user's goal.
     *
     * @return user's stated goal
     */
    public String getGoal() {
        return goal;
    }

    /**
     * Returns the user's age.
     *
     * @return user's age in years
     */
    public int getAge() {
        return age;
    }

    /**
     * Returns the user's biological sex.
     *
     * @return user's biological sex
     */
    public Sex getSex() {
        return sex;
    }

    /**
     * Returns user's height in inches.
     *
     * @return height in inches
     */
    public double getHeightIn() {
        return heightIn;
    }

    /**
     * Returns the user's weight in pounds.
     *
     * @return weight in pounds
     */
    public double getWeightLb() {
        return weightLb;
    }

    /**
     * Return's the user's activity level.
     *
     * @return user's daily activity level
     */
    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    /**
     * Sets user ID.
     *
     * @param id set user ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets user name and updates the JavaFX property if it exists.
     *
     * @param name set user name
     */
    public void setName(String name) {
        this.nameValue = name;
        if (this.name != null) this.name.set(name);
    }

    /**
     * Sets user's stated goal.
     *
     * @param goal set user's goal
     */
    public void setGoal(String goal) {
        this.goal = goal;
    }

    /**
     * Sets user's age.
     *
     * @param age set user's age
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Sets user's biological sex.
     *
     * @param sex set user's biological sex
     */
    public void setSex(Sex sex) {
        this.sex = sex;
    }

    /**
     * Sets height in inches.
     *
     * @param heightIn set user's height in inches
     */
    public void setHeightIn(double heightIn) {
        this.heightIn = heightIn;
    }

    /**
     * Sets weight in pounds.
     *
     * @param weightLb set user's weight in pounds
     */
    public void setWeightLb(double weightLb) {
        this.weightLb = weightLb;
    }

    /**
     * Sets daily activity level.
     *
     * @param activityLevel set user's activity level
     */
    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    /**
     * Calculates the user's Basal Metabolic Rate (BMR) using the Mifflin-St Jeor equation.
     *
     * @return estimated BMR in calories/day
     */
    public double calculateBMR() {
        double weightKg = weightLb * 0.45359237;
        double heightCm = heightIn * 2.54;

        double base = 10.0 * weightKg + 6.25 * heightCm - 5.0 * age;

        if (sex == Sex.MALE) return base + 5.0;
        if (sex == Sex.FEMALE) return base - 161.0;
        return base;
    }

    /**
     * Calculates the user's Total Daily Energy Expenditure (TDEE), which multiplies BMR by the user's activity level.
     *
     * @return estimated TDEE in calories/day
     */
    public double calculateTDEE() {
        double mult = (activityLevel == null) ? ActivityLevel.SEDENTARY.getMultiplier() : activityLevel.getMultiplier();
        return calculateBMR() * mult;
    }

    /**
     * Standard to String method.
     *
     * @return formatted string representation of the user
     */
    @Override
    public String toString() {
        return "User{id='%s', name='%s', goal='%s', age=%d, sex=%s, heightIn=%.2f, weightLb=%.2f, activityLevel=%s}"
                .formatted(id, nameValue, goal, age, sex, heightIn, weightLb, activityLevel);
    }

    /**
     * Users are equal if they share the same ID.
     *
     * @param o   the reference object with which to compare.
     * @return ID
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User other)) return false;
        return Objects.equals(id, other.id);
    }

    /**
     * Returns hash code.
     *
     * @return hash code based on user ID
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

/**
 * Enum representing biological sex options used for BMR calculation.
 */
enum Sex {
    MALE,
    FEMALE,
    OTHER
}

/**
 * Represents the user's physical activity level. Each level maps to a numeric multiplier used in TDEE calculation.
 */
enum ActivityLevel {
    /** Minimal physical activity. */
    SEDENTARY(1.2),

    /** Light exercise 1–3 days/week. */
    LIGHT(1.375),

    /** Moderate exercise 3–5 days/week. */
    MODERATE(1.55),

    /** Hard exercise 6–7 days/week. */
    VERY_ACTIVE(1.725),

    /** Very intense physical labor or training. */
    EXTRA_ACTIVE(1.9);

    /** Factor for multiplying BMR to estimate TDEE. */
    private final double multiplier;

    ActivityLevel(double multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * Returns multiplier for calculation.
     *
     * @return numeric multiplier for TDEE calculation
     */
    public double getMultiplier() {
        return multiplier;
    }
}
