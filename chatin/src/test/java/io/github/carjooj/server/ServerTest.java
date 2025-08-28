package io.github.carjooj.server;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ServerTest {

    @Test
    void shouldEchoReceivedMessage() throws IOException {
        ServerSocket mockServerSocket = mock();
        Socket mockSocket = mock();
        String clientMessage = "hello server\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(clientMessage.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(mockServerSocket.accept()).thenReturn(mockSocket);
        when(mockSocket.getInputStream()).thenReturn(inputStream);
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        Server server = new Server(mockServerSocket);

        server.handleClient(mockSocket);

        assertEquals(clientMessage, outputStream.toString());

    }

    @Test
    void shoudCloseSocketWhenClientSendsQuitCommand() throws IOException {
        ServerSocket mockServerSocket = mock();
        Socket mockSocket = mock();

        String quitCommand = "\\quit\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(quitCommand.getBytes());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(mockServerSocket.accept()).thenReturn(mockSocket);
        when(mockSocket.getInputStream()).thenReturn(inputStream);
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        Server server = new Server(mockServerSocket);

        server.handleClient(mockSocket);

        verify(mockSocket).close();
    }
    
}
