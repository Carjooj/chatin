package io.github.carjooj.server;

import io.github.carjooj.client.clienthandler.factory.ChatClientHandlerFactory;
import io.github.carjooj.client.clienthandler.factory.ClientHandlerFactory;
import io.github.carjooj.client.clientregistry.ClientRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ServerIntegrationTest {

    private Server server;
    private ServerSocket serverSocket;
    private ExecutorService serverExecutor;
    private final String address = "localhost";

    @BeforeEach
    void setUp() throws IOException {
        serverSocket = new ServerSocket(0);
        ClientRegistry clientRegistry = new ClientRegistry();
        ClientHandlerFactory clientHandlerFactory = new ChatClientHandlerFactory(clientRegistry);
        ExecutorService threadPool = Executors.newVirtualThreadPerTaskExecutor();
        server = new Server(serverSocket, threadPool, clientHandlerFactory);

        serverExecutor = Executors.newSingleThreadExecutor();
        serverExecutor.submit(() -> server.awaitConnection());

    }

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }

        if (serverExecutor != null) {
            serverExecutor.awaitTermination(1, TimeUnit.SECONDS);
            serverExecutor.shutdownNow();
        }
    }

    @Test
    void shouldHandleMultipleMessagesAndQuit() throws IOException {
        int port = serverSocket.getLocalPort();
        Socket client = new Socket(address, port);
        PrintWriter out = new PrintWriter(new BufferedOutputStream(client.getOutputStream()), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out.println("User");

        out.println("User test message");

        out.println("\\quit");

        assertNull(in.readLine());


        client.close();
    }

    @Test
    void shouldHandleMultipleClientsConcurrently() throws IOException {
        int port = serverSocket.getLocalPort();
        final CountDownLatch messageReceivedLatch = new CountDownLatch(1);
        Socket client1 = new Socket(address, port);
        PrintWriter out1 = new PrintWriter(new BufferedOutputStream(client1.getOutputStream()), true);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
        String username1 = "client1";
        out1.println(username1);

        Socket client2 = new Socket(address, port);
        PrintWriter out2 = new PrintWriter(new BufferedOutputStream(client2.getOutputStream()), true);
        BufferedReader in2 = new LatchingBufferedReader(new InputStreamReader(client2.getInputStream()), messageReceivedLatch);
        String username2 = "client2";
        out2.println(username2);

        String message1 = "hello";
        out1.println(message1);
        assertEquals(String.format("[%s]: %s", username1, message1), in2.readLine());

        String message2 = "hi";
        out2.println(message2);
        assertEquals(String.format("[%s]: %s", username2, message2), in1.readLine());

        client1.close();
        client2.close();
    }

    @Test
    void shouldBroadcastMessageToOtherClients() throws IOException {
        int port = serverSocket.getLocalPort();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Socket client1 = new Socket(address, port);
        PrintWriter out1 = new PrintWriter(new BufferedOutputStream(client1.getOutputStream()), true);
        String username1 = "client1";
        out1.println(username1);

        Socket client2 = new Socket(address, port);
        PrintWriter out2 = new PrintWriter(new BufferedOutputStream(client2.getOutputStream()), true);
        BufferedReader in2 = new LatchingBufferedReader(new InputStreamReader(client2.getInputStream()), countDownLatch);
        out2.println("client2");

        String messageToClient = "hello everyone!";
        out1.println(messageToClient);

        String expectedMessage = String.format("[%s]: %s", username1, messageToClient);
        assertEquals(expectedMessage, in2.readLine());

        client1.close();
        client2.close();
    }

    @Test
    void shouldPrefixMessagesWithUsername() throws IOException {
        int port = serverSocket.getLocalPort();
        final CountDownLatch messageReceivedLatch = new CountDownLatch(1);
        Socket client1 = new Socket(address, port);
        PrintWriter out1 = new PrintWriter(new BufferedOutputStream(client1.getOutputStream()), true);

        Socket client2 = new Socket(address, port);
        PrintWriter out2 = new PrintWriter(new BufferedOutputStream(client2.getOutputStream()), true);
        BufferedReader in2 = new LatchingBufferedReader(new InputStreamReader(client2.getInputStream()), messageReceivedLatch);

        out1.println("Carlos");
        out2.println("Ana");

        String messageSent = "Olá, Ana";
        out1.println(messageSent);

        String expectedMessage = "[Carlos]: " + messageSent;
        assertEquals(expectedMessage, in2.readLine());

        client1.close();
        client2.close();
    }

    private static class LatchingBufferedReader extends BufferedReader {
        private final CountDownLatch latch;

        public LatchingBufferedReader(Reader in, CountDownLatch latch) {
            super(in);
            this.latch = latch;
        }

        @Override
        public String readLine() throws IOException {
            String line = super.readLine();
            if (line != null) {
                latch.countDown();
            }
            return line;
        }
    }

}
