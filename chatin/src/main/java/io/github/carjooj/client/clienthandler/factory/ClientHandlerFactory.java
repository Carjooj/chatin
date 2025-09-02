package io.github.carjooj.client.clienthandler.factory;

import java.net.Socket;

public interface ClientHandlerFactory {
    Runnable create(Socket clientSocket);
}
