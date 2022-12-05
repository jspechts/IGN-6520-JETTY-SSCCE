package org.example.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class ExampleServerWebSocket {

    private final AtomicReference<Session> sessionRef = new AtomicReference<>();

    private final InputStreamDecoder decoder;

    public ExampleServerWebSocket(InputStreamDecoder decoder) {
        this.decoder = decoder;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.printf("[Server] Connected with client %s%n", session.getRemote().getRemoteAddress());
        this.sessionRef.set(session);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("[Server] Socket Closed: [" + statusCode + "] " + reason);
        Session session = this.sessionRef.getAndSet(null);
        if (session != null) {
            session.close();
        }
    }

    @OnWebSocketError
    public void onError(Throwable cause) {
        cause.printStackTrace();
    }

    @OnWebSocketMessage
    public void onMessage(InputStream inputStream) {
        try {
            decoder.decode(inputStream);
            Optional.of(sessionRef)
                .map(AtomicReference::get)
                .ifPresent(Session::close);
        } catch (Exception e) {
            e.printStackTrace();
            Optional.of(sessionRef)
                .map(AtomicReference::get)
                .ifPresent(session -> session.close(1008, e.toString()));
        }
    }

}
