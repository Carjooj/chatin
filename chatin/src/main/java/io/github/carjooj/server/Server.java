package io.github.carjooj.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ServerSocket serverSocket;

    private final ExecutorService vThreadPool = Executors.newVirtualThreadPerTaskExecutor();

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void awaitConnection() throws IOException {
        while (true) {
            Socket client = serverSocket.accept();

            vThreadPool.submit(() -> {
               try {
                   handleClient(client);
               } catch (IOException e) {
                   e.printStackTrace();
               }
            });
        }
    }

    public void handleClient(Socket client) throws IOException {
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));


        String messageFromClient;
        while ((messageFromClient = in.readLine()) != null) {
            if ("\\quit".equals(messageFromClient)) {
                break;
            }
            out.println(messageFromClient);
        }

        client.close();
    }

}
