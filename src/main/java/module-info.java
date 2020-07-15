module fsciamdev {
    requires javafx.controls;
    requires javafx.fxml;
    requires poi.ooxml;
    requires xmlbeans;
    requires java.sql;
    requires poi;

    opens fsciamdev to javafx.fxml;
    exports fsciamdev;
}