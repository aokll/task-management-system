package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Data;

import com.example.demo.Entity.Role;

import java.util.ArrayList;
import java.util.List;
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
    @Column(name = "roles", nullable = false)
    private Set<Role> roles;

    /**
     * mappedBy = "owner": Мы говорим Hibernate: «Эй, за эту связь отвечает поле owner
     * внутри класса Task. Смотри настройки там».
     * cascade = CascadeType.ALL: Это наш главный инструмент. Если мы удаляем User,
     * Hibernate автоматически пойдет в таблицу tasks и удалит все записи, связанные с этим id.
     * orphanRemoval = true: Если задача вдруг «отвяжется» от пользователя (станет сиротой),
     * она будет удалена из базы автоматически
     * */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();
}
