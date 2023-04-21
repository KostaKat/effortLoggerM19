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
<<<<<<< HEAD
    opens com.Frontend.Controllers to javafx.fxml;
    exports com.Frontend.Controllers;

=======
    exports com.Frontend.Controllers;
    opens com.Frontend.Controllers to javafx.fxml;
>>>>>>> 0c54bde8583cb045dacc306bc0e56bc4721da948
}
