package org.example.server;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class ExampleServerWebSocketCreator implements WebSocketCreator {

    private final InputStreamDecoder decoder;

    public ExampleServerWebSocketCreator(InputStreamDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest servletUpgradeRequest,
                                  ServletUpgradeResponse servletUpgradeResponse) {
        return new ExampleServerWebSocket(decoder);
    }

}
