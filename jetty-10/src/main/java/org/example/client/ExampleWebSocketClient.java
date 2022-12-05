package org.example.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.eclipse.jetty.websocket.api.CloseStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.client.WebSocketClient;

public class ExampleWebSocketClient {

    public static void main(String[] args) throws Exception {
        ExampleWebSocketClient client = new ExampleWebSocketClient(ExampleWebSocketClient::encodeData);
        URI uri = URI.create("ws://localhost:8080/events/");
        client.run(uri);
    }

    private final OutputStreamEncoder encoder;

    public ExampleWebSocketClient(OutputStreamEncoder encoder) {
        this.encoder = encoder;
    }

    private static void encodeData(OutputStream outputStream) throws IOException {
        DataOutputStream out = new DataOutputStream(outputStream);
        out.writeShort(15511);
    }

    public CloseStatus run(URI uri) throws Exception {
        WebSocketClient client = new WebSocketClient();
        client.setIdleTimeout(Duration.ofSeconds(30));

        CloseStatus closeStatus;
        try {
            client.start();
            // The socket that receives events
            ExampleClientWebSocket socket = new ExampleClientWebSocket();
            // Attempt Connect
            Future<Session> fut = client.connect(socket, uri);
            // Wait for Connect
            Session session = fut.get();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            encoder.encode(baos);
            byte[] bytes = baos.toByteArray();
            CompletableFuture<Void> future = new CompletableFuture<>();
            // Send a message
            session.getRemote().sendBytes(
                ByteBuffer.wrap(bytes),
                new WriteCallback() {
                    @Override
                    public void writeFailed(Throwable x) {
                        future.completeExceptionally(x);
                    }

                    @Override
                    public void writeSuccess() {
                        future.complete(null);
                    }
                }
            );
            future.join();

            // Wait for other side to close
            closeStatus = socket.awaitClosure();

            // Close session
            session.close();
        } finally {
            client.stop();
        }

        return closeStatus;
    }

}
