package ru.auth.component.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.auth.component.message.exception.NotFoundImplementationMessageSender;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MessageSenderFactory {
    private final Map<MessageSenderEnum, MessageSender> messageSenderMap;

    @Autowired
    public MessageSenderFactory(Map<String, MessageSender> messageSenderMap) {
        this.messageSenderMap = messageSenderMap.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        entry -> entry.getValue().use(),
                        Map.Entry::getValue
                ));
    }

    public MessageSender getSender(MessageSenderEnum sender) {
        MessageSender messageSender = messageSenderMap.get(sender);
        if (messageSender == null) {
            throw new NotFoundImplementationMessageSender("Nof found " + sender + " implementation");
        }
        return messageSender;
    }
}
