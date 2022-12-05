# Jetty 10 Upgrade [SSCCE](http://sscce.org/)

This project demonstrates a breaking change when upgrading from Jetty `9.4.49.v20220914` -> `10.0.12`.

In Jetty 9, reading any unsigned byte from 0 to 255 inclusive using the `InputStream#read()` API against the `InputStream` instance injected by Jetty into the method annotated with `@OnWebSocketMessage` for class annotated with `@WebSocket` worked well.

However, in Jetty 10, attempting to read an unsigned byte in the range 128 to 255 inclusive using the same setup fails since values in this range are returned as signed bytes, which breaks the `InputStream#read()` contract.

See `jetty-9/src/test/java/org/example/ExampleWebSocketTest` for unit tests which all pass against Jetty 9.

See the equivalent setup against Jetty 10 in `jetty-10/src/test/java/org/example/ExampleWebSocketTest` for unit tests which do not pass for values in the range 128 to 255 inclusive.

I believe a fix for this would be a modification to `org.eclipse.jetty.websocket.core.internal.messages.MessageInputStream#read()` around line 90:

```java
    @Override
    public int read() throws IOException
    {
        byte[] buf = new byte[1];
        while (true)
        {
            int len = read(buf, 0, 1);
            if (len < 0) // EOF
                return -1;
            if (len > 0) // did read something
                return buf[0]; // <-- should be: return buf[0] & 0xFF; 
            // reading nothing (len == 0) tries again
        }
    }
```

This project requires Java 11 to compile and run.

To run all unit tests from the command line, run the following command using Maven 3.5 or greater from the project root directory:

`mvn clean test`

You can also cd into either `jetty-9` or `jetty-10` sub-directories and run the same command to run unit tests against each respective sub-project in isolation.