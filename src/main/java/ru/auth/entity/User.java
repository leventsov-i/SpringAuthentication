package ru.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;

@Entity
@Table(name = "t_user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min=7, message = "Не меньше 7 знаков")
    @Column(unique = true)
    private String email;

    @Size(min=7, message = "Не меньше 7 знаков")
    private String password;

    private boolean isLocked;

    private boolean isCredentialsExpired;

    private boolean isEnabled;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !isCredentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    //не делал ролевую модель, только авторизация
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return getUsername();
    }

    public Long getId() {
        return id;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
