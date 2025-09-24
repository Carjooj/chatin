package io.github.carjooj.client.clientregistry;

import io.github.carjooj.client.Client;
import io.github.carjooj.client.protocol.ChatProtocol;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientRegistry {

    private final List<Client> clientList = new CopyOnWriteArrayList<>();

    public void add(Client newClient) {
        clientList.add(newClient);
        String notification = String.format(ChatProtocol.SERVER_JOIN_NOTIFICATION_FORMAT, newClient.getUsername());
        clientList.stream()
                .filter(client -> !client.equals(newClient))
                .forEach(client -> client.sendMessage(notification));

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
