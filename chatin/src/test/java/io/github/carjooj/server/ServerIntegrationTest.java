package io.github.carjooj.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ServerIntegrationTest {

    private Server server;
    private ServerSocket serverSocket;
    private String address = "localhost";

    @BeforeEach
    void setUp() throws IOException {
        serverSocket = new ServerSocket(0);
        server = new Server(serverSocket);

        Thread.ofVirtual().start(() -> {
            try {
                server.awaitConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void shouldHandleMultipleMessagesAndQuit() throws IOException {
        int port = serverSocket.getLocalPort();
        Socket client = new Socket(address, port);
        PrintWriter out = new PrintWriter(new BufferedOutputStream(client.getOutputStream()), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        out.println("mensagem 1");
        assertEquals("mensagem 1", in.readLine());

        out.println("mensagem 2");
        assertEquals("mensagem 2", in.readLine());

        out.println("\\quit");

        assertNull(in.readLine());


        client.close();
    }

    @Test
    void shouldHandleMultipleClientsSequentially() throws IOException {
        int port = serverSocket.getLocalPort();
        Socket client = new Socket(address, port);
        PrintWriter out = new PrintWriter(new BufferedOutputStream(client.getOutputStream()), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        out.println("hello from client 1");
        assertEquals("hello from client 1", in.readLine());

        out.println("\\quit");
        assertNull(in.readLine());
        client.close();

        Socket client2 = new Socket(address, port);
        PrintWriter out2 = new PrintWriter(new BufferedOutputStream(client2.getOutputStream()), true);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));

        out2.println("hello from client 2");
        assertEquals("hello from client 2", in2.readLine());

        out2.println("\\quit");
        assertNull(in2.readLine());

        client2.close();
    }

    @Test
    void shouldHandleMultipleClientsConcurrently() throws IOException {
        int port = serverSocket.getLocalPort();
        Socket client1 = new Socket(address, port);
        PrintWriter out1 = new PrintWriter(new BufferedOutputStream(client1.getOutputStream()), true);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
        out1.println("hello from client1");

        Socket client2 = new Socket(address, port);
        PrintWriter out2 = new PrintWriter(new BufferedOutputStream(client2.getOutputStream()), true);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(client2.getInputStream()));
        out2.println("hello from client2");

        assertEquals("hello from client1", in1.readLine());
        assertEquals("hello from client2", in2.readLine());

        client1.close();
        client2.close();
    }


}
