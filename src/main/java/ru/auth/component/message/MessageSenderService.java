package ru.auth.component.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageSenderService {
    private final MessageSenderFactory messageSenderFactory;

    @Autowired
    public MessageSenderService(MessageSenderFactory messageSenderFactory) {
        this.messageSenderFactory = messageSenderFactory;
    }

    public void send(String to, String text, MessageSenderEnum sender) {
        MessageSender messageSender = messageSenderFactory.getSender(sender);
        messageSender.send(to, text);
    }
}
