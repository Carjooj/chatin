package io.github.carjooj.client.clienthandler.factory;

import io.github.carjooj.client.Client;
import io.github.carjooj.client.SocketClient;
import io.github.carjooj.client.clienthandler.ClientHandler;
import io.github.carjooj.client.clientregistry.ClientRegistry;

import java.io.IOException;
import java.net.Socket;

public class ChatClientHandlerFactory implements ClientHandlerFactory {

    private final ClientRegistry clientRegistry;

    public ChatClientHandlerFactory(ClientRegistry clientRegistry) {
        this.clientRegistry = clientRegistry;
    }

    @Override
    public Runnable create(Socket clientSocket) {
        try {
            Client client = new SocketClient(clientSocket);

            return new ClientHandler(client, clientSocket, clientRegistry);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar ClientHandler", e);
        }


    }
}
