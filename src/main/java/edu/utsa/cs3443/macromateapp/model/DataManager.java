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

public class DataManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private User activeUser;

    private List<Goal> goals;
    private List<Food> foods;
    private List<CustomFood> customFoods;
    private List<DayLog> dayLogs;
    private transient Map<String, DayLog> dayLogIndex;

    private Path dataDirectory;

    private Map<String, User> usersByEmail;
    private Map<String, String> passwordSaltByEmail;
    private Map<String, String> passwordHashByEmail;

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

    public User getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public List<Food> getFoods() {
        return foods;
    }

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

    public List<CustomFood> getCustomFoods() {
        return customFoods;
    }

    public List<DayLog> getDayLogs() {
        return dayLogs;
    }

    private static String dayKey(String userId, LocalDate date) {
        String u = (userId == null) ? "" : userId;
        String d = (date == null) ? "" : date.toString();
        return u + "|" + d;
    }

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

    public void addFoodLog(LocalDate date, FoodLog log) {
        if (log == null) return;
        DayLog day = getDayLog(date);
        day.addFoodLog(log);
    }


    public Goal getActiveGoal() {
        LocalDate today = LocalDate.now();
        for (Goal g : goals) {
            if (g != null && g.isActive(today)) return g;
        }
        return null;
    }

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

    public FoodLog createFoodLogFromFood(String id, Food food, FoodLog.MealType mealType, double servings, LocalDateTime timestamp, String notes) {
        FoodLog log = new FoodLog(id, food == null ? "" : food.getId(), mealType, servings, timestamp, notes);
        if (food != null) {
            double calories = food.getCaloriesForServings(servings);
            Map<String, Double> m = food.getMacrosForServings(servings);
            log.setComputedTotals(calories, m.getOrDefault("proteinG", 0.0), m.getOrDefault("carbsG", 0.0), m.getOrDefault("fatG", 0.0));
        }
        return log;
    }

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

    @SuppressWarnings("unchecked")
    private static <T> List<T> castList(Object o) {
        if (o instanceof List<?> l) return (List<T>) l;
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> castMap(Object o) {
        if (o instanceof Map<?, ?> m) return (Map<K, V>) m;
        return new HashMap<>();
    }

    private void seedDefaultsIfNeeded() {
        if (foods == null) foods = new ArrayList<>();
        if (!foods.isEmpty()) return;

        foods.add(new Food("f1", "Chicken Breast", "Generic", "Protein", 1.0, 165, 31, 0, 3.6));
        foods.add(new Food("f2", "Brown Rice", "Generic", "Carb", 1.0, 218, 4.5, 45.8, 1.6));
        foods.add(new Food("f3", "Broccoli", "Generic", "Veg", 1.0, 55, 3.7, 11.2, 0.6));
    }

    private static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private static String randomSaltHex(int bytes) {
        byte[] b = new byte[bytes];
        new SecureRandom().nextBytes(b);
        return toHex(b);
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return toHex(md.digest(bytes));
        } catch (Exception e) {
            return "";
        }
    }

    private static String toHex(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }
}



