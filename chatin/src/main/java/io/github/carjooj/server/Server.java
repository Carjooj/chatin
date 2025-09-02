package io.github.carjooj.server;

import io.github.carjooj.client.clienthandler.factory.ClientHandlerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class Server {
    private final ServerSocket serverSocket;

    private final ExecutorService threadPool;

    private final ClientHandlerFactory clientHandlerFactory;

    public Server(ServerSocket serverSocket, ExecutorService threadPool, ClientHandlerFactory clientHandlerFactory) {
        this.serverSocket = serverSocket;
        this.threadPool = threadPool;
        this.clientHandlerFactory = clientHandlerFactory;
    }

    public void awaitConnection() {
        while (!serverSocket.isClosed()) {
            try {
                Socket client = serverSocket.accept();
                Runnable clientHandler = clientHandlerFactory.create(client);
                threadPool.submit(clientHandler);
            } catch (IOException e) {
                System.err.println("Erro no accept: " + e.getMessage());
            }
        }
    }

}
