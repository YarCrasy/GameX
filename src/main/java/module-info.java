module com.yarcrasy.gamex {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.j;
    requires javafx.base;

    opens com.yarcrasy.gamex to javafx.fxml;
    opens com.yarcrasy.gamex.Models to javafx.fxml;
    exports com.yarcrasy.gamex;
    exports com.yarcrasy.gamex.controllers;
    exports com.yarcrasy.gamex.Models;
    opens com.yarcrasy.gamex.controllers to javafx.fxml;
}