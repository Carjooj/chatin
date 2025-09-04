package io.github.carjooj.client.clienthandler;

import io.github.carjooj.client.Client;
import io.github.carjooj.client.clientregistry.ClientRegistry;
import io.github.carjooj.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientHandlerTest {

    @Mock
    private Client mockClient;

    @Mock
    private Socket mockSocket;

    @Mock
    private ClientRegistry mockRegistry;

    @Mock
    private AppLogger mockLogger;

    private ClientHandler clientHandler;

    @BeforeEach
    void setUp() {
        clientHandler = new ClientHandler(mockClient, mockSocket, mockRegistry, mockLogger);
    }


    @Test
    void shouldHandleClientLifecycleCorrectly() throws IOException {
        String quitCommand = "\\quit\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(quitCommand.getBytes());


        when(mockSocket.getInputStream()).thenReturn(inputStream);

        clientHandler.run();

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);

        verify(mockRegistry).add(clientCaptor.capture());

        verify(mockRegistry).remove(clientCaptor.getValue());

        verify(mockSocket).close();
    }

    @Test
    void shouldBroadcastMessagesToRegistry() throws IOException {
        String username = "Test";
        String message = "hello server";
        String clientInput = username + "\n" + message + "\n" + "\\quit\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(clientInput.getBytes());


        when(mockSocket.getInputStream()).thenReturn(inputStream);

        clientHandler.run();

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);

        verify(mockRegistry).add(clientCaptor.capture());

        verify(mockRegistry).broadcast(clientCaptor.getValue(), message);
    }

    @Test
    void shouldReadFirstLineAsUsername() throws IOException {
        String username = "Carlos";

        String clientMessage = username + "\n\\quit\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(clientMessage.getBytes());

        when(mockSocket.getInputStream()).thenReturn(inputStream);

        clientHandler.run();

        verify(mockClient).setUsername(username);
    }

    @Test
    void shouldLogExceptionAndNotInteractWithRegistryWhenStreamCreationFails() throws IOException {
        String exceptionMessage = "Stream failed";
        IOException ioException = new IOException(exceptionMessage);

        when(mockSocket.getInputStream()).thenThrow(ioException);

        clientHandler.run();

        verify(mockLogger).error("Erro na comunicação com o cliente: ", ioException);

        verify(mockRegistry, never()).add(any());
        verify(mockRegistry, never()).remove(any());
    }

    @Test
    void shouldLogErrorAndRemoveClientFromRegistryIfCommunicationFails() throws IOException {
        String exceptionMessage = "Communication failed test";
        IOException ioException = new IOException(exceptionMessage);

        InputStream faultyInputStream = new InputStream() {
            private final ByteArrayInputStream delegate = new ByteArrayInputStream("TestUser\n".getBytes());
            private boolean isUsernameRead = false;

            @Override
            public int read() throws IOException {
                if (!isUsernameRead) {
                    int data = delegate.read();
                    if (data == '\n') {
                        isUsernameRead = true;
                    }
                    return data;
                }
                throw ioException;
            }
        };

        when(mockSocket.getInputStream()).thenReturn(faultyInputStream);

        clientHandler.run();

        verify(mockLogger).error(anyString(), eq(ioException));

        verify(mockRegistry).add(mockClient);
        verify(mockRegistry).remove(mockClient);
    }
}
