module org.openjfx.hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    
    requires com.fasterxml.jackson.core;
	requires java.sql;
	requires jdk.httpserver;
	requires com.fasterxml.jackson.databind;
	requires com.auth0.jwt;

    opens com.Frontend to javafx.fxml;
    exports com.Frontend;
    exports com.Frontend.Controllers;
    opens com.Frontend.Controllers to javafx.fxml;
}
