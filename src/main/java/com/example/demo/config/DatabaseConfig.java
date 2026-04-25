package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DatabaseConfig {
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void init(){
        try (Statement statement = dataSource.getConnection().createStatement())
        {
            statement.executeUpdate("CREATE DATABASE javarush_tracker");
        } catch (SQLException e) {
        }
    }
}
