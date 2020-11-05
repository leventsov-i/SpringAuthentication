package ru.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.auth.entity.TimeCodeActivateUser;

import java.util.Optional;

public interface TimeCodeActivateUserRepository extends JpaRepository<TimeCodeActivateUser, Long> {
    Optional<TimeCodeActivateUser> findByTimeCode(String timeCode);
}
