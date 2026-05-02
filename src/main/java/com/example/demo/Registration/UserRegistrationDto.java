package com.example.demo.Registration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {
    @NotBlank(message = "Логин не может быть пустым")
    private String username;

    @Size(min = 5, message = "Пароль должен быть минимум 6 символов")
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
