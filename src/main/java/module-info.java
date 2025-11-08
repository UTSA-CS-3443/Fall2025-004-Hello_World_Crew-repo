module edu.utsa.cs3443.macromateapp {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.utsa.cs3443.macromateapp to javafx.fxml;
    exports edu.utsa.cs3443.macromateapp;
}