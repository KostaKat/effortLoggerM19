package com.WebSocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

@ClientEndpoint
public class WebSocketClient {

    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened: " + session.getId());
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("WebSocket message received: " + message);
    }

    @OnClose
    public void onClose(CloseReason reason) {
        System.out.println("WebSocket connection closed with reason: " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    public void connect(String uri) throws DeploymentException, InterruptedException, IOException, URISyntaxException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, new URI(uri));
    }

    public void sendMessage(String message) throws Exception {
        session.getBasicRemote().sendText(message);
    }
}
