package ru.auth.component.message;

public interface MessageSender {
    void send(String to, String text);
    MessageSenderEnum use();
}
