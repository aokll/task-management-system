package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import com.example.demo.Entity.Role;

import java.util.Set;

@Entity
@Table(name = "users")// В Postgres слово 'user' зарезервировано, поэтому называем таблицу 'users'
@Data// Генерирует геттеры, сеттеры, toString и equals через Lombok
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique = true гарантирует, что не будет двух пользователей с одинаковым логином
    @Column(unique = true, nullable = false)
    private String username;

    // Пароль будет храниться в зашифрованном виде (BCrypt)
    @Column(nullable = false)
    private String password;

    // Позволяет "забанить" пользователя, не удаляя его из базы
    private boolean active;

    /**
     * Роли пользователя (USER, ADMIN).
     * @ElementCollection создает вспомогательную таблицу 'user_role'.
     * FetchType.EAGER означает, что роли загружаются из базы СРАЗУ вместе с пользователем.
     */
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)// Сохраняет роль как текст (ADMIN), а не как число (1)
    private Set<Role> roles;
}
