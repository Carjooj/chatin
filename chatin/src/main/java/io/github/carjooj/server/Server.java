package io.github.carjooj.server;

import io.github.carjooj.client.clienthandler.factory.ClientHandlerFactory;
import io.github.carjooj.exceptions.HandlerCreationException;
import io.github.carjooj.logger.AppLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class Server {
    private final ServerSocket serverSocket;

    private final ExecutorService threadPool;

    private final ClientHandlerFactory clientHandlerFactory;

    private final AppLogger logger;

    public Server(ServerSocket serverSocket, ExecutorService threadPool, ClientHandlerFactory clientHandlerFactory, AppLogger logger) {
        this.serverSocket = serverSocket;
        this.threadPool = threadPool;
        this.clientHandlerFactory = clientHandlerFactory;
        this.logger = logger;
    }

    public void awaitConnection() {
        while (!serverSocket.isClosed()) {
            try {
                Socket client = serverSocket.accept();
                Runnable clientHandler = clientHandlerFactory.create(client);
                threadPool.submit(clientHandler);
            } catch (HandlerCreationException | IOException e) {
                logger.error("Erro ao aceitar conexão: ", e);
            }
        }
    }

}
