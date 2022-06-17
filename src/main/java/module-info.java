module com.narawit.schedulesetter {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.mariadb.jdbc;

    opens com.narawit.schedulesetter to javafx.fxml;
    exports com.narawit.schedulesetter;
    exports com.narawit.schedulesetter.controllers;
    opens com.narawit.schedulesetter.controllers to javafx.fxml;
    opens com.narawit.schedulesetter.viewmodels to javafx.fxml;
}