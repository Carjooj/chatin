package io.github.carjooj.client;

import java.util.UUID;

public interface Client {
    UUID getId();

    void sendMessage(String message);

    String getUsername();

    void setUsername(String username);
}
