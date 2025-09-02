package io.github.carjooj.client.clienthandler;

import io.github.carjooj.client.Client;
import io.github.carjooj.client.clientregistry.ClientRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Client client;

    private final Socket socket;

    private final ClientRegistry clientRegistry;

    public ClientHandler(Client client, Socket clientSocket, ClientRegistry clientRegistry) {
        this.client = client;
        this.socket = clientSocket;
        this.clientRegistry = clientRegistry;
    }

    public void run() {
        Socket clientSocket = this.socket;

        try (clientSocket) {

            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                String username = in.readLine();
                if (username == null || username.trim().isEmpty()) {
                    return;
                }
                client.setUsername(username);
                clientRegistry.add(client);
                String messageFromClient;
                while ((messageFromClient = in.readLine()) != null) {
                    if ("\\quit".equals(messageFromClient)) {
                        break;
                    }
                    clientRegistry.broadcast(client, messageFromClient);
                }
            } finally {
                clientRegistry.remove(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
