package org.example.server;

import org.eclipse.jetty.websocket.server.JettyServerUpgradeRequest;
import org.eclipse.jetty.websocket.server.JettyServerUpgradeResponse;
import org.eclipse.jetty.websocket.server.JettyWebSocketCreator;

public class ExampleServerWebSocketCreator implements JettyWebSocketCreator {

    private final InputStreamDecoder decoder;

    public ExampleServerWebSocketCreator(InputStreamDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Object createWebSocket(JettyServerUpgradeRequest jettyServerUpgradeRequest,
                                  JettyServerUpgradeResponse jettyServerUpgradeResponse) {
        return new ExampleServerWebSocket(decoder);
    }

}
