package io.github.carjooj.client.clienthandler;

import io.github.carjooj.client.Client;
import io.github.carjooj.client.clientregistry.ClientRegistry;
import io.github.carjooj.client.reader.MessageReader;
import io.github.carjooj.logger.AppLogger;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Client client;

    private final Socket socket;

    private final ClientRegistry clientRegistry;

    private final MessageReader messageReader;

    private final AppLogger logger;

    public ClientHandler(Client client, Socket clientSocket, ClientRegistry clientRegistry, MessageReader messageReader, AppLogger logger) {
        this.client = client;
        this.socket = clientSocket;
        this.clientRegistry = clientRegistry;
        this.messageReader = messageReader;
        this.logger = logger;
    }

    public void run() {
        Socket clientSocket = this.socket;
        try (
                clientSocket
        ) {

            String username = messageReader.readMessage();
            if (username == null || username.trim().isEmpty()) {
                return;
            }
            client.setUsername(username);
            clientRegistry.add(client);
            try {
                String messageFromClient;
                while ((messageFromClient = messageReader.readMessage()) != null) {
                    if ("\\quit".equals(messageFromClient)) {
                        break;
                    }
                    clientRegistry.broadcast(client, messageFromClient);
                }
            } finally {
                clientRegistry.remove(client);
            }
        } catch (IOException e) {
            logger.error("Erro na comunicação com o cliente: ", e);
        }
    }
}
