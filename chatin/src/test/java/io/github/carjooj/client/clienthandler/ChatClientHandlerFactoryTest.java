package io.github.carjooj.client.clienthandler;


import io.github.carjooj.client.clienthandler.factory.ChatClientHandlerFactory;
import io.github.carjooj.client.clienthandler.factory.ClientHandlerFactory;
import io.github.carjooj.client.clientregistry.ClientRegistry;
import io.github.carjooj.exceptions.HandlerCreationException;
import io.github.carjooj.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatClientHandlerFactoryTest {

    @Mock
    private ClientRegistry mockRegistry;

    @Mock
    private AppLogger mockLogger;

    @Mock
    private Socket mockSocket;

    @Test
    void shouldThrowHandlerCreationExceptionWhenCreationFails() throws IOException {
        IOException ioException = new IOException("Socket exception");
        when(mockSocket.getOutputStream()).thenThrow(ioException);

        ClientHandlerFactory clientHandlerFactory = new ChatClientHandlerFactory(mockRegistry, mockLogger);

        assertThrows(HandlerCreationException.class, () -> clientHandlerFactory.create(mockSocket));
    }
}
