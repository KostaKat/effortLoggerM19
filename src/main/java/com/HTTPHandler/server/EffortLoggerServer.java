package com.HTTPHandler.server;

import com.Database.*;
import com.WebSocket.WebSocket;
import java.util.List;
import java.util.Arrays;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import java.net.InetSocketAddress;
import com.HTTPHandler.server.ContextHandlerFactory;
import org.glassfish.tyrus.server.Server;

public class EffortLoggerServer {
    private HttpServer httpServer;
    private Server webSocketServer;
    private int httpPort;
    private int wsPort;

    public EffortLoggerServer(int httpPort, int wsPort) {
        this.httpPort = httpPort;
        this.wsPort = wsPort;
    }

    public static void main(String args[]) throws Exception {
        EffortLoggerServer server = new EffortLoggerServer(8080, 8081);
        server.startHttpServer();
        server.startWebSocketServer();
    }

    public void startHttpServer() throws Exception {
           // Initialize variables
        ContextHandlerFactory contextFactory = new ContextHandlerFactory();
        List<String> endpoints = null;
        HttpContext context = null;
    
        // Create a list of endpoint paths
        endpoints = Arrays.asList(
                "/login",
                "/register",
                "/addLog",
                "/editLog",
                "/deleteLog",
                "/getLog");
    
        // Create the server on port 8000
        this.httpServer = HttpServer.create(new InetSocketAddress(this.httpPort), 0);
    
        // Loop through each endpoint and create a context for it with the corresponding
        // handler using the factory
        for (String endpoint : endpoints) {
    
            // Create a context for the endpoint
            context = httpServer.createContext(endpoint);
    
            // Set the handler for the context using the factory
            context.setHandler(contextFactory.create(endpoint));
        }
    
        // Start the server
        httpServer.start();
        System.out.println("Server listening on port " + httpPort);
    
    }

    public void stopHttpServer() {
        httpServer.stop(0);
    }

    public void startWebSocketServer() throws Exception {
        // Create the WebSocket server on the specified port
        this.webSocketServer = new Server("localhost", this.wsPort, "/", null, WebSocket.class);

        // Start the WebSocket server
        webSocketServer.start();
        System.out.println("WebSocket server listening on port " + wsPort);
    }

    public void stopWebSocketServer() {
        webSocketServer.stop();
    }
}
