package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    // 1. Шифровальщик паролей (BCrypt) — золотой стандарт бэкенда
    @Bean
    public PasswordEncoder passwordEncoder(){
        /**
         * Мы сказали Спрингу: «Никогда не сравнивай пароли как текст,
         * используй мощный алгоритм шифрования»
         * */
        return new BCryptPasswordEncoder();
    }
    // 2. Правила доступа: кто куда может ходить
    /**
     * filterChain: Это наш КПП. Мы приказали: «Любой запрос
     * (anyRequest) должен быть подтвержден (authenticated)».
     * */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http){
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()// Все страницы требуют логина
        )
                .formLogin(Customizer.withDefaults())// Стандартная форма входа
                .logout(Customizer.withDefaults());// Возможность выйти из аккаунта

        return http.build();
    }
}
