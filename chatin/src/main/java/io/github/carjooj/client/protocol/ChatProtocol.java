package io.github.carjooj.client.protocol;

public final class ChatProtocol {

    public static final String MESSAGE_DELIMITER = "<<EOF>>";

    public static final String USER_MESSAGE_FORMAT = "[%s]: %s";

    public static final String SERVER_NOTIFICATION_FORMAT = "[SERVER]: %s";

    public static final String SERVER_JOIN_NOTIFICATION_FORMAT = String.format(SERVER_NOTIFICATION_FORMAT, "%s entrou na sala");

    private ChatProtocol() {
    }

}
