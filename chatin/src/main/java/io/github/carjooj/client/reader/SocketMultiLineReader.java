package io.github.carjooj.client.reader;

import io.github.carjooj.client.protocol.ChatProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketMultiLineReader implements MessageReader {

    private final BufferedReader reader;

    public SocketMultiLineReader(Socket socket) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }


    @Override
    public String readMessage() throws IOException {
        List<String> lines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null && !line.equals(ChatProtocol.MESSAGE_DELIMITER)) {
            lines.add(line);
        }

        if (lines.isEmpty()) {
            return null;
        }

        return String.join("\n", lines);
    }
}
