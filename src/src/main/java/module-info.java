module org.openjfx.hellofx {
	requires javafx.controls;
	requires javafx.fxml;
	requires org.json;

	requires com.fasterxml.jackson.core;
	requires java.sql;
	requires jdk.httpserver;
	requires com.fasterxml.jackson.databind;
	requires com.auth0.jwt;
	requires jakarta.websocket;
	requires jakarta.websocket.client;
	requires org.glassfish.tyrus.server;
	requires Java.WebSocket;
	requires com.google.gson;
	requires java.json;

	exports com.WebSocket to org.glassfish.tyrus.core;

	opens com.Frontend to javafx.fxml;

	exports com.Frontend;

	opens com.Frontend.Controllers to javafx.fxml;

	exports com.Frontend.Controllers;

}
