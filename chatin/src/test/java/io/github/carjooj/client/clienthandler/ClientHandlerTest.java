package io.github.carjooj.client.clienthandler;

import io.github.carjooj.client.Client;
import io.github.carjooj.client.clientregistry.ClientRegistry;
import io.github.carjooj.client.reader.MessageReader;
import io.github.carjooj.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
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

    private final String testUser = "testUser";

    private ClientHandler clientHandler;
    private final String quitCommand = "\\quit";
    @Mock
    private MessageReader mockReader;

    @BeforeEach
    void setUp() {
        clientHandler = new ClientHandler(mockClient, mockSocket, mockRegistry, mockReader, mockLogger);
    }


    @Test
    void shouldAddAndRemoveClientDuringLifecycle() throws IOException {

        when(mockReader.readMessage()).thenReturn(testUser).thenReturn(quitCommand);

        clientHandler.run();

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);

        verify(mockRegistry).add(clientCaptor.capture());

        verify(mockRegistry).remove(clientCaptor.getValue());

        verify(mockSocket).close();
    }

    @Test
    void shouldBroadcastMessagesAfterLogin() throws IOException {
        String message = "hello server";

        when(mockReader.readMessage()).thenReturn(testUser).thenReturn(message).thenReturn(quitCommand);

        clientHandler.run();

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);

        verify(mockRegistry).add(clientCaptor.capture());

        verify(mockRegistry).broadcast(clientCaptor.getValue(), message);
    }

    @Test
    void shouldSetUsernameAndRegisterClient() throws IOException {

        when(mockReader.readMessage()).thenReturn(testUser).thenReturn(quitCommand);

        clientHandler.run();

        verify(mockClient).setUsername(testUser);
    }

    @Test
    void shouldLogErrorWhenStreamFailOnCreation() throws IOException {
        String exceptionMessage = "Stream failed";
        IOException ioException = new IOException(exceptionMessage);

        when(mockReader.readMessage()).thenThrow(ioException);

        clientHandler.run();

        verify(mockLogger).error("Erro na comunicação com o cliente: ", ioException);

        verify(mockRegistry, never()).add(any());
        verify(mockRegistry, never()).remove(any());
    }

    @Test
    void shouldLogErrorAndRemoveClientWhenReadFailsAfterLogin() throws IOException {
        String exceptionMessage = "Communication failed test";
        IOException ioException = new IOException(exceptionMessage);

        when(mockReader.readMessage()).thenReturn("TestUser").thenThrow(ioException);


        clientHandler.run();

        verify(mockLogger).error(anyString(), eq(ioException));

        verify(mockRegistry).add(mockClient);
        verify(mockRegistry).remove(mockClient);
    }
}
