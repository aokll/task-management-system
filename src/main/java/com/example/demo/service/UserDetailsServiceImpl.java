package com.example.demo.service;

import com.example.demo.Entity.Role;
import com.example.demo.Entity.User;
import com.example.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Процесс аутентификации: попытка загрузки пользователя [{}]", username);
        // 1. Ищем нашего юзера в базе через репозиторий
        User user = userRepository.findByUsername(username);

        if (user == null){
            log.warn("Неудачная попытка входа: пользователь [{}] не найден в базе данных", username);
            throw new UsernameNotFoundException("Пользователь не найден: " + username);
        }
        // 2. Превращаем нашего User (Entity) в объект UserDetails (который понимает Spring Security)

        log.info("Пользователь [{}] успешно найден. Роли: {}", username, user.getRoles());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,// Доп. флаги (срок действия пароля и т.д.)
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toList())
        );
    }
    public void saveNewUser(String username, String password){
        User user = new User();

        user.setUsername(username);

        user.setPassword(passwordEncoder.encode(password)); // Хешируем! Без этого Security не пустит пользователя

        user.setRoles(Collections.singleton(Role.USER));

        user.setActive(true);

        userRepository.save(user);

        log.info("Зарегистрирован новый пользователь с логином [{}]", username);
    }
}
