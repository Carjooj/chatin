package io.github.carjooj.server;

import io.github.carjooj.client.clienthandler.factory.ClientHandlerFactory;
import io.github.carjooj.exceptions.HandlerCreationException;
import io.github.carjooj.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServerTest {

    @Mock
    private ServerSocket mockServerSocket;

    @Mock
    private ExecutorService mockThreadPool;

    @Mock
    private ClientHandlerFactory mockFactory;

    @Mock
    private AppLogger mockLogger;

    private Server server;

    @BeforeEach
    void setUp() {
        server = new Server(mockServerSocket, mockThreadPool, mockFactory, mockLogger);
    }

    @Test
    void shouldAcceptConnectionAndSubmitToThreadPool() throws IOException, HandlerCreationException {
        Socket mockSocket = mock();
        Runnable mockHandler = mock();

        when(mockServerSocket.isClosed()).thenReturn(false, true);
        when(mockServerSocket.accept()).thenReturn(mockSocket);
        when(mockFactory.create(mockSocket)).thenReturn(mockHandler);

        server.awaitConnection();


        verify(mockFactory).create(mockSocket);

        verify(mockThreadPool).submit(mockHandler);
    }

    @Test
    void shouldLogErrorWhenAcceptFails() throws IOException {
        String exceptionMessage = "Test exception";
        IOException ioException = new IOException(exceptionMessage);

        when(mockServerSocket.accept()).thenThrow(ioException);
        when(mockServerSocket.isClosed()).thenReturn(false, true);

        server.awaitConnection();

        verify(mockLogger).error("Erro ao aceitar conexão: ", ioException);
    }

}
