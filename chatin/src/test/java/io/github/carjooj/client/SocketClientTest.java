package io.github.carjooj.client;

import io.github.carjooj.exceptions.ClientConnectionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SocketClientTest {

    @Mock
    private Socket mockSocket;

    @Test
    void shouldThrowClientConnectionExceptionWhenCreationFails() throws IOException {
        IOException ioException = new IOException("Client connection exception test");

        when(mockSocket.getOutputStream()).thenThrow(ioException);

        assertThrows(ClientConnectionException.class, () -> new SocketClient((mockSocket)));
    }
}
