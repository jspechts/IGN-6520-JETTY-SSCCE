package org.example.server;

import org.eclipse.jetty.websocket.server.JettyWebSocketServlet;
import org.eclipse.jetty.websocket.server.JettyWebSocketServletFactory;

public class ExampleWebSocketServlet extends JettyWebSocketServlet {

    private final InputStreamDecoder decoder;

    public ExampleWebSocketServlet(InputStreamDecoder decoder) {
        this.decoder = decoder;
    }

    @Override
    protected void configure(JettyWebSocketServletFactory jettyWebSocketServletFactory) {
        ExampleServerWebSocketCreator creator = new ExampleServerWebSocketCreator(decoder);
        jettyWebSocketServletFactory.setCreator(creator);
    }

}
