package ru.auth.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.auth.component.message.MessageSenderEnum;
import ru.auth.component.message.MessageSenderService;
import ru.auth.entity.TimeCodeActivateUser;
import ru.auth.entity.User;
import ru.auth.repository.TimeCodeActivateUserRepository;
import ru.auth.repository.UserRepository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserActivateService {
    public static final Integer LENGTH_ACTIVATE_CODE = 255;
    private static final int FIVE_MINUTES = 5;
    private final MessageSenderService senderService;
    private final TimeCodeActivateUserRepository activateUserRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserActivateService(MessageSenderService senderService,
                               TimeCodeActivateUserRepository activateUserRepository,
                               UserRepository userRepository) {
        this.senderService = senderService;
        this.activateUserRepository = activateUserRepository;
        this.userRepository = userRepository;
    }

    public void sentActivateMessage(String to, Long userId) {
        String activateTimeCode = generateActivateTimeCode();
        saveActivateCodeForUser(userId, activateTimeCode);
        senderService.send(to, generateUserMessage(activateTimeCode), MessageSenderEnum.EMAIL);
    }

    public void activateUser(String timeCode) {
        TimeCodeActivateUser timeCodeActivateUser = activateUserRepository
                .findByTimeCode(timeCode)
                .orElseThrow(() -> new RuntimeException("Not found time code"));
        long timeStampWithFiveMinutes = timeCodeActivateUser.getTimestamp().getTime() + TimeUnit.MINUTES.toMillis(FIVE_MINUTES);
        if (timeStampWithFiveMinutes < System.currentTimeMillis()) {
            throw new RuntimeException("Time code not found");
        }

        User user = userRepository
                .findById(timeCodeActivateUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Not found user"));

        user.setEnabled(true);
        userRepository.save(user);
    }

    private String generateActivateTimeCode() {
        return RandomStringUtils.random(LENGTH_ACTIVATE_CODE, true, true);
    }

    private String generateActivateUrl(String activateTimeCode) {
        return "http://localhost:8080/api/auth/activate/" + activateTimeCode;
    }

    private String generateUserMessage(String activateTimeCode) {
        return "Для активации аккаунта перейдите по ссылке: " + generateActivateUrl(activateTimeCode);
    }

    private void saveActivateCodeForUser(Long userId, String activateCode) {
        activateUserRepository.save(TimeCodeActivateUser.builder()
                .userId(userId)
                .timeCode(activateCode)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build()
        );
    }
}
