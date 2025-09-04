package io.github.carjooj.client.clienthandler.factory;

import io.github.carjooj.client.Client;
import io.github.carjooj.client.SocketClient;
import io.github.carjooj.client.clienthandler.ClientHandler;
import io.github.carjooj.client.clientregistry.ClientRegistry;
import io.github.carjooj.client.reader.MessageReader;
import io.github.carjooj.client.reader.SocketLineReader;
import io.github.carjooj.exceptions.ClientConnectionException;
import io.github.carjooj.exceptions.HandlerCreationException;
import io.github.carjooj.logger.AppLogger;

import java.io.IOException;
import java.net.Socket;

public class ChatClientHandlerFactory implements ClientHandlerFactory {

    private final ClientRegistry clientRegistry;

    private final AppLogger logger;

    public ChatClientHandlerFactory(ClientRegistry clientRegistry, AppLogger logger) {
        this.clientRegistry = clientRegistry;
        this.logger = logger;
    }

    @Override
    public Runnable create(Socket clientSocket) throws HandlerCreationException {
        try {
            Client client = new SocketClient(clientSocket);

            MessageReader messageReader = new SocketLineReader(clientSocket);

            return new ClientHandler(client, clientSocket, clientRegistry, messageReader, logger);
        } catch (ClientConnectionException | IOException e) {
            throw new HandlerCreationException("Não foi possível criar ClientHandler", e);
        }


    }
}
