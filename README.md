# MacroMate — Calorie & Macro Tracking Application

## 📌 Application Description
**MacroMate** is a desktop nutrition-tracking application built using **JavaFX** and following the **Model–View–Controller (MVC)** design pattern.  
Its purpose is to help users track daily calorie intake, macronutrients (protein, carbs, fats), and food history through a clean, modern, and intuitive interface.

MacroMate allows users to:

- Create an account and securely log in  
- Record foods into **Breakfast**, **Lunch**, **Dinner**, or **Snacks**  
- Add both *predefined foods* and *custom foods created by the user*  
- View daily progress toward nutrition goals  
- Track historical calorie/macro intake  
- Update profile information and personalized daily goals  
- Delete food logs or custom foods directly from the UI  
- Enjoy a consistent and responsive layout shared across Dashboard, Add Food, Food Library, History, and Settings scenes  

The application uses **local file I/O** (stored under the user’s home directory) to save:
- User accounts  
- Nutrition goals  
- Food logs  
- Custom foods  

All user data persists between sessions.

---

## 👥 Contributors
This project was built collaboratively by the Hello_World_Crew team:

- **Mathew M Davis**  
- **Jacqueline L Flores**  
- **Isabella N Griffith**  
- **Shreeshkumar Lillyprabhu**

---

## 🚀 Running the Application

### **Requirements**
To run MacroMate, you need:

- **JDK 17 or higher** (we recommend JDK 21 since JavaFX 21 is used in the project)  
- **JavaFX SDK 21+** installed and configured  
- An IDE such as IntelliJ IDEA, Eclipse, or VS Code **with JavaFX support**  
- **Internet access is NOT required** — the app runs fully offline  

---

## How to Run


### 1. Clone this repository
```bash
git clone https://github.com/UTSA-CS-3443/Fall2025-004-Hello_World_Crew-repo.git
cd Fall2025-004-Hello_World_Crew-repo
```

### 2. Open the project in your IDE
* IntelliJ IDEA:
-   File → Open → Select project folder
* Eclipse:
-   File → Import → Existing Maven/Gradle Project
* VS Code:
-   Open the folder with the Java Extension Pack installed

### 3. Configure JavaFX (if needed)
* Ensure the JavaFX SDK path is set in your IDE
* Add VM options to run JavaFX applications (example for IntelliJ):
```bash
--module-path /path/to/javafx-sdk-24.0.2/lib --add-modules javafx.controls,javafx.fxml
```

### 4. Build and Run
* Run Main.java (or the class containing the main() method)
* The app will open in a window and run fully offline
