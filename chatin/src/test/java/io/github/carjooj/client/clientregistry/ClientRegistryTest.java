package io.github.carjooj.client.clientregistry;

import io.github.carjooj.client.Client;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ClientRegistryTest {

    @Test
    void shouldDeliverBroadcastToAddedClient() {
        ClientRegistry clientRegistry = new ClientRegistry();
        Client sender = mock(Client.class, "sender");
        Client newClient = mock(Client.class, "newClient");

        when(sender.getUsername()).thenReturn("Sender");

        String message = "bem-vindo";

        clientRegistry.add(sender);
        clientRegistry.add(newClient);

        clientRegistry.broadcast(sender, message);

        String expectedMessage = "[Sender]: " + message;

        verify(newClient).sendMessage(expectedMessage);
        verify(sender, never()).sendMessage(expectedMessage);
    }

    @Test
    void shouldNotDeliverBroadcastToRemovedClient() {
        ClientRegistry clientRegistry = new ClientRegistry();
        Client sender = mock(Client.class, "sender");
        Client clientToKeep = mock(Client.class, "clientToKeep");
        Client clientToRemove = mock(Client.class, "clientToRemove");
        when(sender.getUsername()).thenReturn("Sender");

        String message = "ainda está ai?";

        clientRegistry.add(sender);
        clientRegistry.add(clientToKeep);
        clientRegistry.add(clientToRemove);

        clientRegistry.remove(clientToRemove);
        clientRegistry.broadcast(sender, message);

        String expectedMessage = "[Sender]: " + message;

        verify(clientToKeep).sendMessage(expectedMessage);
        verify(sender, never()).sendMessage(expectedMessage);
        verify(clientToRemove, never()).sendMessage(anyString());
    }

    @Test
    void shouldBroadcastMessageToAllClientsExceptSender() {
        ClientRegistry clientRegistry = new ClientRegistry();
        Client sender = mock(Client.class, "sender");
        Client receiver1 = mock(Client.class, "receiver1");
        Client receiver2 = mock(Client.class, "receiver2");
        when(sender.getUsername()).thenReturn("Carlos");


        clientRegistry.add(sender);
        clientRegistry.add(receiver1);
        clientRegistry.add(receiver2);

        String message = "hello";

        clientRegistry.broadcast(sender, message);

        String expectedMessageFormat = "[Carlos]: " + message;

        verify(receiver1).sendMessage(expectedMessageFormat);
        verify(receiver2).sendMessage(expectedMessageFormat);
        verify(sender, never()).sendMessage(anyString());
    }

}
