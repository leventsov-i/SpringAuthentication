package ru.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfiguration {

    @Bean
    public MailSender mailSender(
            @Value("${spring.mail.protocol}") String protocol,
            @Value("${spring.mail.host}") String host,
            @Value("${spring.mail.port}") Integer port,
            @Value("${spring.mail.username}") String username,
            @Value("${spring.mail.password}") String password
    ) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setProtocol(protocol);
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        return javaMailSender;
    }
}
