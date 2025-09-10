package io.github.carjooj.server;

import io.github.carjooj.client.clienthandler.factory.ChatClientHandlerFactory;
import io.github.carjooj.client.clienthandler.factory.ClientHandlerFactory;
import io.github.carjooj.client.clientregistry.ClientRegistry;
import io.github.carjooj.client.reader.SocketMultiLineReader;
import io.github.carjooj.logger.AppLogger;
import io.github.carjooj.logger.Slf4jAppLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ServerIntegrationTest {

    private Server server;
    private ServerSocket serverSocket;
    private ExecutorService serverExecutor;
    private final String testSyncMessage = "SYNC";
    private final String messageFormat = "[%s]: %s";


    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        serverSocket = new ServerSocket(0);
        ClientRegistry clientRegistry = new ClientRegistry();
        AppLogger registryLogger = Slf4jAppLogger.getLogger(ClientRegistry.class);
        ClientHandlerFactory clientHandlerFactory = new ChatClientHandlerFactory(clientRegistry, registryLogger);
        ExecutorService threadPool = Executors.newVirtualThreadPerTaskExecutor();
        AppLogger serverLogger = Slf4jAppLogger.getLogger(Server.class);
        server = new Server(serverSocket, threadPool, clientHandlerFactory, serverLogger);

        CountDownLatch serverReadyLatch = new CountDownLatch(1);

        serverExecutor = Executors.newSingleThreadExecutor();
        serverExecutor.submit(() -> {
            serverReadyLatch.countDown();
            server.awaitConnection();
        });

        assertTrue(serverReadyLatch.await(6, TimeUnit.SECONDS), "Servidor não iniciou a tempo");
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
        String username = "testUser";
        try (
                ClientConnection user = connectAndLogin(username, port)
        ) {

            sendMessage(user.out(), "User test message");

            sendMessage(user.out(), "\\quit");

            assertNull(user.in().readMessage());

        }

    }

    @Test
    void shouldHandleMultipleClientsConcurrently() throws IOException {
        int port = serverSocket.getLocalPort();
        String username1 = "testClient";
        String username2 = "anotherTestClient";

        try (
                ClientConnection testClient = connectAndLogin(username1, port);
                ClientConnection anotherTestClient = connectAndLogin(username2, port)
        ) {
            sendMessage(anotherTestClient.out(), testSyncMessage);

            String expectedSyncMessage = String.format(messageFormat, username2, testSyncMessage);
            assertEquals(expectedSyncMessage, testClient.in().readMessage());

            String message1 = "hello";
            sendMessage(testClient.out(), message1);
            assertEquals(String.format(messageFormat, username1, message1), anotherTestClient.in().readMessage());

            String message2 = "hi";
            sendMessage(anotherTestClient.out(), message2);
            assertEquals(String.format(messageFormat, username2, message2), testClient.in().readMessage());
        }
    }

    @Test
    void shouldBroadcastMessageToOtherClients() throws IOException {
        int port = serverSocket.getLocalPort();
        String username1 = "testClient";
        String username2 = "anotherTestClient";


        try (
                ClientConnection testClient = connectAndLogin(username1, port);
                ClientConnection anotherTestClient = connectAndLogin(username2, port)
        ) {

            sendMessage(anotherTestClient.out, testSyncMessage);

            String expectedSync = String.format(messageFormat, username2, testSyncMessage);
            assertEquals(expectedSync, testClient.in().readMessage());

            String messageToClient = "hello everyone!";
            sendMessage(testClient.out(), messageToClient);
            String expectedMessage = String.format(messageFormat, username1, messageToClient);
            assertEquals(expectedMessage, anotherTestClient.in().readMessage());
        }

    }

    @Test
    void shouldPrefixMessagesWithUsername() throws IOException {
        int port = serverSocket.getLocalPort();
        String username1 = "testUser";
        String username2 = "anotherTestUser";

        try (
                ClientConnection testUser = connectAndLogin(username1, port);
                ClientConnection anotherTestUser = connectAndLogin(username2, port)
        ) {
            sendMessage(anotherTestUser.out(), testSyncMessage);

            String expectedSync = String.format(messageFormat, username2, testSyncMessage);
            assertEquals(expectedSync, testUser.in().readMessage());

            String messageSent = "Olá, Chatin";
            sendMessage(testUser.out(), messageSent);

            String expectedMessage = String.format(messageFormat, username1, messageSent);
            assertEquals(expectedMessage, anotherTestUser.in().readMessage());
        }

    }

    @Test
    void shouldHandleMultilineMessages() throws IOException {
        int port = serverSocket.getLocalPort();
        String username1 = "testUser";
        String username2 = "anotherTestUser";
        try (
                ClientConnection client1 = connectAndLogin(username1, port);
                ClientConnection client2 = connectAndLogin(username2, port)
        ) {


            sendMessage(client2.out, testSyncMessage);

            String expectedSync = String.format(messageFormat, username2, testSyncMessage);
            assertEquals(expectedSync, client1.in.readMessage());

            String multilineMessage = """
                    This is a multiline
                    Test message
                    Hello""";

            sendMessage(client1.out(), multilineMessage);

            String expectedMessage = String.format(messageFormat, username1, multilineMessage);

            assertEquals(expectedMessage, client2.in.readMessage());

        }
    }

    private ClientConnection connectAndLogin(String username, int port) throws IOException {
        String address = "localhost";
        int soTimeout = 6000;
        Socket socket = new Socket(address, port);
        socket.setSoTimeout(soTimeout);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        SocketMultiLineReader in = new SocketMultiLineReader(socket);
        sendMessage(out, username);
        return new ClientConnection(socket, out, in);
    }

    private void sendMessage(PrintWriter out, String message) {
        out.println(message);
        out.println("<<EOF>>");
    }

    private record ClientConnection(Socket socket, PrintWriter out, SocketMultiLineReader in) implements AutoCloseable {
        @Override
        public void close() throws IOException {
            socket.close();
        }
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
