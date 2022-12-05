package org.example.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;

public class ExampleWebSocketServer {

    public static void main(String[] args) throws Exception {
        ExampleWebSocketServer server = new ExampleWebSocketServer(ExampleWebSocketServer::decodeData);
        server.setHost("localhost");
        server.setPort(8080);
        server.start();
        server.join();
    }

    private final Server server;
    private final ServerConnector connector;

    public ExampleWebSocketServer(InputStreamDecoder decoder) {
        server = new Server();
        connector = new ServerConnector(server);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        ExampleWebSocketServlet servlet = new ExampleWebSocketServlet(decoder);
        context.addServlet(new ServletHolder(servlet), "/events/*");
        server.setHandler(context);

        JettyWebSocketServletContainerInitializer.configure(context, (servletContext, wsContainer) -> {});
    }

    private static void decodeData(InputStream inputStream) throws IOException {
        DataInputStream in = new DataInputStream(inputStream);
        short s = in.readShort();
        if (s != 15511) {
            throw new IllegalArgumentException(String.format("[Server] Expected %d but got %d", 15511, s));
        }
    }

    public String getHost() {
        return connector.getHost();
    }

    public void setHost(String host) {
        connector.setHost(host);
    }

    public int getPort() {
        return connector.getLocalPort();
    }

    public void setPort(int port) {
        connector.setPort(port);
    }

    public void start() throws Exception {
        server.start();
    }

    public URI getURI() {
        return server.getURI();
    }

    public void stop() throws Exception {
        server.stop();
    }

    public void join() throws InterruptedException {
        System.out.println("Use Ctrl+C to stop server");
        server.join();
    }

}
