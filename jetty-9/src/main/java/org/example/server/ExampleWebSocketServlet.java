package org.example.server;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class ExampleWebSocketServlet extends WebSocketServlet {

    private final InputStreamDecoder decoder;

    public ExampleWebSocketServlet(InputStreamDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public void configure(WebSocketServletFactory webSocketServletFactory) {
        ExampleServerWebSocketCreator creator = new ExampleServerWebSocketCreator(decoder);
        webSocketServletFactory.setCreator(creator);
    }

}
