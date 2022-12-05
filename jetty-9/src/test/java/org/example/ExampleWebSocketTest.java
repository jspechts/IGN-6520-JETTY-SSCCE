package org.example;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.jetty.websocket.api.CloseStatus;
import org.example.client.ExampleWebSocketClient;
import org.example.server.ExampleWebSocketServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExampleWebSocketTest {

    ExampleWebSocketServer server;
    int i;

    @BeforeEach
    public void setup() throws Exception {
        server = new ExampleWebSocketServer(this::decode);
        server.setHost("localhost");
        server.setPort(0);
        server.start();
    }

    @AfterEach
    public void tearDown() throws Exception {
        server.stop();
    }

    private void encode(OutputStream outputStream) throws IOException {
        outputStream.write(i);
    }

    private void decode(InputStream inputStream) throws IOException {
        int i = inputStream.read();
        if (i < 0) {
            throw new EOFException(String.format("Expected %d but got %d", this.i, i));
        }
        if (i != this.i) {
            throw new IllegalArgumentException(String.format("Expected %d but got %d", this.i, i));
        }
    }

    static Stream<Arguments> args() {
        return IntStream.rangeClosed(0, 255)
            .mapToObj(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("args")
    void test(int i) throws Exception {
        this.i = i;
        ExampleWebSocketClient client = new ExampleWebSocketClient(this::encode);
        int port = server.getPort();
        URI uri = URI.create(String.format("ws://localhost:%d/events/", port));
        CloseStatus closeStatus = client.run(uri);
        assertEquals(1000, closeStatus.getCode());
    }

}
