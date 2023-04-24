package com.WebSocket;
public class WebSocketClientTest

 {
    public static void main(String[] args) {
        WebSocketClient client = new WebSocketClient();
        try {
            client.connect("ws://localhost:8081/getLogs");
            client.sendMessage("Hello, server!");
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
