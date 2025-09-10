package io.github.carjooj.client.reader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SocketMultiLineReaderTest {

    @Mock
    private Socket mockSocket;

    @Test
    void shouldReadMultipleLinesUntilDelimiter() throws IOException {
        String multiLineMessage = """
                This is
                A test
                Multine
                Message
                <<EOF>>""";

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(multiLineMessage.getBytes());

        when(mockSocket.getInputStream()).thenReturn(byteArrayInputStream);

        SocketMultiLineReader socketMultiLineReader = new SocketMultiLineReader(mockSocket);

        String messageRead = socketMultiLineReader.readMessage();

        String expectedMessage = """
                This is
                A test
                Multine
                Message""";

        assertEquals(expectedMessage, messageRead);
    }

    @Test
    void test() throws IOException {
        String multiLineMessage = "client1";

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(multiLineMessage.getBytes());

        when(mockSocket.getInputStream()).thenReturn(byteArrayInputStream);

        SocketMultiLineReader socketMultiLineReader = new SocketMultiLineReader(mockSocket);

        String messageRead = socketMultiLineReader.readMessage();

        String expectedMessage = "client1";

        assertEquals(expectedMessage, messageRead);
    }
}
