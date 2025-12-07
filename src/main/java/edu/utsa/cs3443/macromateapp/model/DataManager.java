package edu.utsa.cs3443.macromateapp.model;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Central data manager responsible for loading, saving, and accessing all
 * persistent data used by the MacroMate application. This includes:
 *
 * <ul>
 *     <li>User accounts and authentication</li>
 *     <li>Food library items</li>
 *     <li>Custom foods created by users</li>
 *     <li>Daily logs of meals and nutrient totals</li>
 *     <li>Active goals and progress tracking</li>
 * </ul>
 *
 * <p>All data is serialized to a single file inside a user-specific data directory.
 * The manager also provides helper methods for computing hashed passwords, seeding
 * defaults, updating logs, and associating entries with the active user.</p>
 */
public class DataManager implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Currently authenticated user (or null if no user is logged in). */
    private User activeUser;

    /** List of stored user goals. */
    private List<Goal> goals;

    /** Global list of standard food items. */
    private List<Food> foods;

    /** List of custom foods created by all users. */
    private List<CustomFood> customFoods;

    /** List of all recorded DayLogs for all users. */
    private List<DayLog> dayLogs;

    /** Fast lookup table for DayLog entries: (userId | date) -> DayLog. */
    private transient Map<String, DayLog> dayLogIndex;

    /** Path to the directory containing serialized app data. */
    private Path dataDirectory;

    /** Map of email -> User object. */
    private Map<String, User> usersByEmail;

    /** Map of email -> password salt. */
    private Map<String, String> passwordSaltByEmail;

    /** Map of email -> hashed password. */
    private Map<String, String> passwordHashByEmail;

    /**
     * Constructs a new DataManager using the provided data directory.
     * All collections are initialized empty and populated later through
     * {@link #loadAllData()}.
     *
     * @param dataDirectory directory where all persistent app data will be stored
     */
    public DataManager(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.goals = new ArrayList<>();
        this.foods = new ArrayList<>();
        this.customFoods = new ArrayList<>();
        this.dayLogs = new ArrayList<>();
        this.usersByEmail = new HashMap<>();
        this.passwordSaltByEmail = new HashMap<>();
        this.passwordHashByEmail = new HashMap<>();
        this.dayLogIndex = new HashMap<>();
    }

    /**
     * Returns the active user.
     *
     * @return the currently active user, or null if none is logged in
     */
    public User getActiveUser() {
        return activeUser;
    }

    /**
     * Sets the currently active user.
     *
     * @param activeUser the user to authenticate as
     */
    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    /**
     * Returns list of goals.
     *
     * @return list of all goals
     */
    public List<Goal> getGoals() {
        return goals;
    }

    /**
     * Returns list of food items.
     *
     * @return list of all standard food items
     */
    public List<Food> getFoods() {
        return foods;
    }

    /**
     * Returns all food items visible to the currently active user.
     * This includes:
     * <ul>
     *     <li>Global foods</li>
     *     <li>User's own generated Food entries based on CustomFood</li>
     * </ul>
     *
     * @return visible food list for the active user
     */
    public List<Food> getFoodsForActiveUser() {
        if (foods == null) return List.of();

        if (activeUser == null || activeUser.getId() == null) {
            return new ArrayList<>(foods);
        }

        String uid = activeUser.getId();

        Set<String> allowedCustomFoodItemIds = new HashSet<>();
        if (customFoods != null) {
            for (CustomFood cf : customFoods) {
                if (cf == null) continue;
                if (uid.equalsIgnoreCase(cf.getUserId())) {
                    allowedCustomFoodItemIds.add("cf_item_" + cf.getId());
                }
            }
        }

        List<Food> out = new ArrayList<>();
        for (Food f : foods) {
            if (f == null) continue;
            String id = f.getId();

            if (id != null && id.startsWith("cf_item_")) {
                if (allowedCustomFoodItemIds.contains(id)) out.add(f);
            } else {
                out.add(f);
            }
        }

        return out;
    }

    /**
     * Returns list of custom foods.
     *
     * @return list of all custom foods
     */
    public List<CustomFood> getCustomFoods() {
        return customFoods;
    }

    /**
     * Returns list of day logs.
     *
     * @return list of all day logs
     */
    public List<DayLog> getDayLogs() {
        return dayLogs;
    }

    /**
     * Builds a unique key for (userId, date) lookup in {@link #dayLogIndex}.
     *
     * @param userId user identifier
     * @param date date of the log
     * @return string key formatted as "userId|yyyy-mm-dd"
     */
    private static String dayKey(String userId, LocalDate date) {
        String u = (userId == null) ? "" : userId;
        String d = (date == null) ? "" : date.toString();
        return u + "|" + d;
    }

    /**
     * Loads all serialized data from disk into memory. If no data file exists, default foods are seeded.
     * If loading fails, the system resets to empty collections and seeds default foods.
     */
    public void loadAllData() {
        try {
            Files.createDirectories(dataDirectory);
            Path file = dataDirectory.resolve("macromate.dat");
            if (!Files.exists(file)) {
                seedDefaultsIfNeeded();
                return;
            }

            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
                activeUser = (User) in.readObject();
                goals = castList(in.readObject());
                foods = castList(in.readObject());
                customFoods = castList(in.readObject());
                dayLogs = castList(in.readObject());
                usersByEmail = castMap(in.readObject());
                passwordSaltByEmail = castMap(in.readObject());
                passwordHashByEmail = castMap(in.readObject());
            }

            if (goals == null) goals = new ArrayList<>();
            if (foods == null) foods = new ArrayList<>();
            if (customFoods == null) customFoods = new ArrayList<>();
            if (dayLogs == null) dayLogs = new ArrayList<>();
            if (usersByEmail == null) usersByEmail = new HashMap<>();
            if (passwordSaltByEmail == null) passwordSaltByEmail = new HashMap<>();
            if (passwordHashByEmail == null) passwordHashByEmail = new HashMap<>();
            rebuildDayLogIndex();

            seedDefaultsIfNeeded();
        } catch (Exception e) {
            activeUser = null;
            goals = new ArrayList<>();
            foods = new ArrayList<>();
            customFoods = new ArrayList<>();
            dayLogs = new ArrayList<>();
            usersByEmail = new HashMap<>();
            passwordSaltByEmail = new HashMap<>();
            passwordHashByEmail = new HashMap<>();
            seedDefaultsIfNeeded();
            rebuildDayLogIndex();
        }
    }

    /**
     * Saves all persistent data to disk. Serialization suppresses errors to avoid interrupting application flow.
     */
    public void saveAllData() {
        try {
            Files.createDirectories(dataDirectory);
            Path file = dataDirectory.resolve("macromate.dat");
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
                out.writeObject(activeUser);
                out.writeObject(goals);
                out.writeObject(foods);
                out.writeObject(customFoods);
                out.writeObject(dayLogs);
                out.writeObject(usersByEmail);
                out.writeObject(passwordSaltByEmail);
                out.writeObject(passwordHashByEmail);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Registers a new user by email, hashing and salting the password before saving.
     * Automatically logs the new user in.
     *
     * @param fullName the user's full name
     * @param email    user email (used as ID)
     * @param password plaintext password to hash
     * @return true if registration succeeded, false if email exists or invalid input
     */
    public boolean registerUser(String fullName, String email, String password) {
        String e = normalizeEmail(email);
        if (e.isEmpty()) return false;
        if (password == null || password.isBlank()) return false;
        if (usersByEmail.containsKey(e)) return false;

        User u = new User();
        u.setId(e);
        u.setName(fullName == null ? "" : fullName.trim());
        usersByEmail.put(e, u);

        String salt = randomSaltHex(16);
        String hash = sha256Hex((salt + password).getBytes(StandardCharsets.UTF_8));
        passwordSaltByEmail.put(e, salt);
        passwordHashByEmail.put(e, hash);

        activeUser = u;
        saveAllData();
        return true;
    }

    /**
     * Authenticates a user by comparing salted+hashed passwords.
     *
     * @param email    user email
     * @param password plaintext password to verify
     * @return the authenticated {@link User}, or null on failure
     */
    public User authenticate(String email, String password) {
        String e = normalizeEmail(email);
        if (!usersByEmail.containsKey(e)) return null;
        String salt = passwordSaltByEmail.getOrDefault(e, "");
        String expected = passwordHashByEmail.getOrDefault(e, "");
        String actual = sha256Hex((salt + (password == null ? "" : password)).getBytes(StandardCharsets.UTF_8));
        if (!expected.equals(actual)) return null;

        activeUser = usersByEmail.get(e);
        return activeUser;
    }

    /**
     * Updates a user's account email, migrating all relevant data and credentials.
     *
     * @param oldEmail current email
     * @param newEmail desired new email
     * @return true if update succeeds, false if invalid or duplicate email
     */
    public boolean updateAccountEmail(String oldEmail, String newEmail) {
        String oldE = normalizeEmail(oldEmail);
        String newE = normalizeEmail(newEmail);
        if (oldE.isEmpty() || newE.isEmpty()) return false;
        if (!usersByEmail.containsKey(oldE)) return false;
        if (usersByEmail.containsKey(newE)) return false;

        User u = usersByEmail.remove(oldE);
        String salt = passwordSaltByEmail.remove(oldE);
        String hash = passwordHashByEmail.remove(oldE);

        u.setId(newE);

        usersByEmail.put(newE, u);
        passwordSaltByEmail.put(newE, salt);
        passwordHashByEmail.put(newE, hash);

        if (activeUser != null && oldE.equalsIgnoreCase(activeUser.getId())) activeUser = u;

        for (CustomFood cf : customFoods) {
            if (cf != null && oldE.equalsIgnoreCase(cf.getUserId())) cf.setUserId(newE);
        }

        saveAllData();
        return true;
    }

    /**
     * Rebuilds the day log index for fast (userId, date) lookups.
     */
    private void rebuildDayLogIndex() {
        if (dayLogIndex == null) dayLogIndex = new HashMap<>();
        dayLogIndex.clear();

        if (dayLogs == null) dayLogs = new ArrayList<>();

        for (DayLog dl : dayLogs) {
            if (dl == null) continue;
            if (dl.getDate() == null) continue;
            String key = dayKey(dl.getUserId(), dl.getDate());
            dayLogIndex.putIfAbsent(key, dl);
        }
    }

    /**
     * Retrieves the user's log for the given date, creating one if it does not exist.
     *
     * @param date date of interest
     * @return existing or newly created DayLog
     */
    public DayLog getDayLog(LocalDate date) {
        LocalDate d = (date == null) ? LocalDate.now() : date;

        if (activeUser == null) return null;
        String userId = activeUser.getId();

        if (dayLogIndex == null) rebuildDayLogIndex();

        String key = dayKey(userId, d);
        DayLog existing = dayLogIndex.get(key);
        if (existing != null) return existing;

        if (dayLogs == null) dayLogs = new ArrayList<>();

        DayLog created = new DayLog(UUID.randomUUID().toString(), userId, d);
        dayLogs.add(created);
        dayLogIndex.put(key, created);
        return created;
    }

    /**
     * Adds a food log entry to the specified date.
     *
     * @param date date to attach the log to
     * @param log  food log entry
     */
    public void addFoodLog(LocalDate date, FoodLog log) {
        if (log == null) return;
        DayLog day = getDayLog(date);
        day.addFoodLog(log);
    }

    /**
     * Returns the goal that is active today.
     *
     * @return currently active goal, or null if none are active
     */
    public Goal getActiveGoal() {
        LocalDate today = LocalDate.now();
        for (Goal g : goals) {
            if (g != null && g.isActive(today)) return g;
        }
        return null;
    }

    /**
     * Resolves the display name of a food or custom food by ID.
     *
     * @param foodOrCustomFoodId ID of the food entry
     * @return matching name, or empty string if not found
     */
    public String resolveFoodNameById(String foodOrCustomFoodId) {
        if (foodOrCustomFoodId == null) return "";
        for (Food f : foods) {
            if (f != null && foodOrCustomFoodId.equals(f.getId())) return f.getName();
        }
        for (CustomFood c : customFoods) {
            if (c != null && foodOrCustomFoodId.equals(c.getId())) return c.getName();
        }
        return "";
    }

    /**
     * Creates a new FoodLog entry using a standard Food item, computing totals
     * based on the given number of servings.
     *
     * @param id        log ID
     * @param food      food item used
     * @param mealType  meal category
     * @param servings  number of servings
     * @param timestamp timestamp of consumption
     * @param notes     optional notes
     * @return constructed FoodLog instance
     */
    public FoodLog createFoodLogFromFood(String id, Food food, FoodLog.MealType mealType, double servings, LocalDateTime timestamp, String notes) {
        FoodLog log = new FoodLog(id, food == null ? "" : food.getId(), mealType, servings, timestamp, notes);
        if (food != null) {
            double calories = food.getCaloriesForServings(servings);
            Map<String, Double> m = food.getMacrosForServings(servings);
            log.setComputedTotals(calories, m.getOrDefault("proteinG", 0.0), m.getOrDefault("carbsG", 0.0), m.getOrDefault("fatG", 0.0));
        }
        return log;
    }

    /**
     * Creates a new FoodLog entry using a CustomFood item. Totals are computed
     * by scaling per-serving values.
     *
     * @param id          log ID
     * @param customFood  custom food used
     * @param mealType    meal category
     * @param servings    servings multiplier
     * @param timestamp   timestamp of consumption
     * @param notes       optional notes
     * @return constructed FoodLog instance
     */
    public FoodLog createFoodLogFromCustomFood(String id, CustomFood customFood, FoodLog.MealType mealType, double servings, LocalDateTime timestamp, String notes) {
        FoodLog log = new FoodLog(id, customFood == null ? "" : customFood.getId(), mealType, servings, timestamp, notes);
        if (customFood != null) {
            double calories = customFood.computerCalories() * Math.max(0.0, servings);
            Map<String, Double> m = customFood.computerMacros();
            double s = Math.max(0.0, servings);
            log.setComputedTotals(
                    calories,
                    m.getOrDefault("proteinG", 0.0) * s,
                    m.getOrDefault("carbsG", 0.0) * s,
                    m.getOrDefault("fatG", 0.0) * s
            );
        }
        return log;
    }

    /**
     * Deletes a CustomFood by ID and removes all associated generated Food items
     * and FoodLog references.
     *
     * @param customFoodId ID of the custom food to delete
     * @return true if any data was removed
     */
    public boolean deleteCustomFoodById(String customFoodId) {
        if (customFoodId == null || customFoodId.isBlank()) return false;

        String generatedFoodId = "cf_item_" + customFoodId;

        boolean removedCustom = customFoods.removeIf(cf -> cf != null && customFoodId.equals(cf.getId()));
        boolean removedGeneratedFood = foods.removeIf(f -> f != null && generatedFoodId.equals(f.getId()));

        for (DayLog dl : dayLogs) {
            if (dl == null) continue;
            dl.getFoodLogs().removeIf(fl -> fl != null && (customFoodId.equals(fl.getCustomFoodId()) || generatedFoodId.equals(fl.getCustomFoodId())));
        }

        saveAllData();
        return removedCustom || removedGeneratedFood;
    }

    /** Safely casts an object to a List. */
    @SuppressWarnings("unchecked")
    private static <T> List<T> castList(Object o) {
        if (o instanceof List<?> l) return (List<T>) l;
        return new ArrayList<>();
    }

    /** Safely casts an object to a Map. */
    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> castMap(Object o) {
        if (o instanceof Map<?, ?> m) return (Map<K, V>) m;
        return new HashMap<>();
    }

    /**
     * Seeds initial default foods if the food list is empty. Ensures that first-time users have data to interact with.
     */
    private void seedDefaultsIfNeeded() {
        if (foods == null) foods = new ArrayList<>();
        if (!foods.isEmpty()) return;

        foods.add(new Food("f1", "Chicken Breast", "Generic", "Protein", 1.0, 165, 31, 0, 3.6));
        foods.add(new Food("f2", "Brown Rice", "Generic", "Carb", 1.0, 218, 4.5, 45.8, 1.6));
        foods.add(new Food("f3", "Broccoli", "Generic", "Veg", 1.0, 55, 3.7, 11.2, 0.6));
    }

    /** Normalizes an email to lowercase and trims whitespace. */
    private static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    /** Generates a random salt of the specified byte length encoded as hex. */
    private static String randomSaltHex(int bytes) {
        byte[] b = new byte[bytes];
        new SecureRandom().nextBytes(b);
        return toHex(b);
    }

    /** Computes a SHA-256 digest and returns hex representation. */
    private static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return toHex(md.digest(bytes));
        } catch (Exception e) {
            return "";
        }
    }

    /** Converts a byte array to a hex string. */
    private static String toHex(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }
}



