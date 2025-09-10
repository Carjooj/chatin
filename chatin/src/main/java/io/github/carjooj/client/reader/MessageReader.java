package io.github.carjooj.client.reader;

import java.io.IOException;

public interface MessageReader {
    String readMessage() throws IOException;
}
