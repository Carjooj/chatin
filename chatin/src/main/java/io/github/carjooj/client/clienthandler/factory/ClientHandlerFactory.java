package io.github.carjooj.client.clienthandler.factory;

import io.github.carjooj.exceptions.HandlerCreationException;

import java.net.Socket;

public interface ClientHandlerFactory {
    Runnable create(Socket clientSocket) throws HandlerCreationException;
}
