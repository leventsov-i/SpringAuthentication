package ru.auth.component.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class EmailMessageSender implements MessageSender {
    private final static String SUBJECT = "Активировать учетную запись";
    private final MailSender mailSender;
    private final String from;

    @Autowired
    public EmailMessageSender(MailSender mailSender, @Value("${spring.mail.address}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void send(String to, String text) {
        send(to, from, text, SUBJECT);
    }

    @Override
    public MessageSenderEnum use() {
        return MessageSenderEnum.EMAIL;
    }

    private void send(String to, String from, String text, String subject) {
        mailSender.send(MessageBuilder.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .text(text)
                .build()
        );
    }

    private static class MessageBuilder {
        private String from;
        private String to;
        private String subject;
        private String text;

        public static MessageBuilder builder() {
            return new MessageBuilder();
        }

        public MessageBuilder from(String from) {
            this.from = from;
            return this;
        }

        public MessageBuilder to(String to) {
            this.to = to;
            return this;
        }

        public MessageBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public MessageBuilder text(String text) {
            this.text = text;
            return this;
        }

        public SimpleMailMessage build() {
            if (from == null || to == null) {
                throw new NullPointerException("From or to mail is null");
            }
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(from);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(text);
            return mailMessage;
        }
    }
}
