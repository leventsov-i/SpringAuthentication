package ru.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.auth.service.UserActivateService;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "t_time_code_activate_user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TimeCodeActivateUser {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long userId;

    @Column(unique = true)
    private String timeCode;

    @Column
    private Timestamp timestamp;
}
