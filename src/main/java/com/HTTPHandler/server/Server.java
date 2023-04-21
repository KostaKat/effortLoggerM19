package com.HTTPHandler.server;

import java.util.List;
import java.util.Arrays;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;
import java.net.InetSocketAddress;
import com.Database.*;
public class Server {
    private HttpServer server;
    private int port;

    public Server(int port) {
        this.port = port;
    }
    public static void main(String args[]) throws Exception {
    	Server server = new Server(8086);
    	server.startServer();
    	
    }
    public void startServer() throws Exception {
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
        this.server = HttpServer.create(new InetSocketAddress(this.port), 0);

        // Loop through each endpoint and create a context for it with the corresponding
        // handler using the factory
        for (String endpoint : endpoints) {

            // Create a context for the endpoint
            context = server.createContext(endpoint);

            // Set the handler for the context using the factory
            context.setHandler(contextFactory.create(endpoint));
        }
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.createDatabase();
        // Start the server
        server.start();
        
        System.out.println("Server listening on port " + port);
    }

    public void stopServer() {
        server.stop(0);
    }

    
}
