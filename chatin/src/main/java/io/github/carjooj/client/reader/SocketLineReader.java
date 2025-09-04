package io.github.carjooj.client.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketLineReader implements MessageReader {
    private final BufferedReader reader;

    public SocketLineReader(Socket socket) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public String readMessage() throws IOException {
        return reader.readLine();
    }
}
