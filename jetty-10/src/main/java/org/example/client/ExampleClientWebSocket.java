package org.example.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class ExampleClientWebSocket {

    private final CompletableFuture<CloseStatus> futureCloseStatus = new CompletableFuture<>();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.printf("[Client] Connected with server %s%n", session.getRemote().getRemoteAddress());
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("[Client] Socket Closed: [" + statusCode + "] " + reason);
        futureCloseStatus.complete(new CloseStatus(statusCode, Objects.toString(reason)));
    }

    @OnWebSocketError
    public void onError(Throwable cause) {
        cause.printStackTrace();
    }

    @OnWebSocketMessage
    public void onMessage(InputStream inputStream) {
        byte[] bytes;
        try {
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String encoded = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes);
        System.out.println(encoded);
    }

    public CloseStatus awaitClosure() {
        System.out.println("[Client] Awaiting closure from remote");
        return futureCloseStatus.join();
    }

}
