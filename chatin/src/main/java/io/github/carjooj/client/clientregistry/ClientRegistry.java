package io.github.carjooj.client.clientregistry;

import io.github.carjooj.client.Client;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientRegistry {

    private final List<Client> clientList = new CopyOnWriteArrayList<>();

    public void add(Client client) {
        clientList.add(client);
    }

    public void broadcast(Client sender, String message) {
        String username = sender.getUsername();

        String usernameFormattedMessage = String.format("[%s]: %s", username, message);

        clientList.stream()
                .filter(client -> !client.equals(sender))
                .forEach(client -> client.sendMessage(usernameFormattedMessage));
    }

    public void remove(Client client) {
        clientList.remove(client);
    }
}
