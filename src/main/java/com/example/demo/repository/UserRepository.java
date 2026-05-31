package com.example.demo.repository;

import com.example.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Магия Spring Data JPA: нам не нужно писать SQL.
     * Метод findBy + Username сам сгенерирует запрос:
     * "SELECT * FROM users WHERE username = ?"
     */
    User findByUsername(String userName);
}
