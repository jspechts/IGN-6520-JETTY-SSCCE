package org.example.server;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface InputStreamDecoder {

    void decode(InputStream inputStream) throws IOException;

}
