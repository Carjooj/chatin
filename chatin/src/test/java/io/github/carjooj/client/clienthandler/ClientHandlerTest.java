package io.github.carjooj.client.clienthandler;

import io.github.carjooj.client.Client;
import io.github.carjooj.client.clientregistry.ClientRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientHandlerTest {

    @Mock
    private Client mockClient;

    @Mock
    private Socket mockSocket;

    @Mock
    private ClientRegistry mockRegistry;

    @Mock
    private ClientHandler clientHandler;


    @Test
    void shouldHandleClientLifecycleCorrectly() throws IOException {
        String quitCommand = "\\quit\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(quitCommand.getBytes());


        when(mockSocket.getInputStream()).thenReturn(inputStream);

        clientHandler = new ClientHandler(mockClient, mockSocket, mockRegistry);

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

        clientHandler = new ClientHandler(mockClient, mockSocket, mockRegistry);

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

        clientHandler = new ClientHandler(mockClient, mockSocket, mockRegistry);

        clientHandler.run();

        verify(mockClient).setUsername(username);
    }
}
