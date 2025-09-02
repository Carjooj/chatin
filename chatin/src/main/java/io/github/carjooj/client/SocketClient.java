package io.github.carjooj.client;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.UUID;

public class SocketClient implements Client {
    private final UUID id = UUID.randomUUID();
    private final PrintWriter writer;
    private String username;


    public SocketClient(Socket socket) throws IOException {
        this.writer = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void sendMessage(String message) {
        writer.println(message);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SocketClient that = (SocketClient) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
