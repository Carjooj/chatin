package io.github.carjooj.server;

import io.github.carjooj.client.clienthandler.factory.ClientHandlerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private Server server;


    @Test
    void shouldAcceptConnectionAndSubmitToThreadPool() throws IOException {
        Socket mockSocket = mock();
        Runnable mockHandler = mock();

        when(mockServerSocket.isClosed()).thenReturn(false, true);
        when(mockServerSocket.accept()).thenReturn(mockSocket);
        when(mockFactory.create(mockSocket)).thenReturn(mockHandler);

        server.awaitConnection();


        verify(mockFactory).create(mockSocket);

        verify(mockThreadPool).submit(mockHandler);
    }

}
