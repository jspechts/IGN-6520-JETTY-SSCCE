package org.example.client;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
public interface OutputStreamEncoder {

    void encode(OutputStream outputStream) throws IOException;

}
