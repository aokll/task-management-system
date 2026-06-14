package com.example.demo.DBFlyway;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("entityManagerFactory")
@Slf4j// Магия Lombok: дает нам готовое поле log
public class DatabaseMigrator {

    private final String url;
    private final String username;
    private final String password;

    public DatabaseMigrator(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    @PostConstruct
    public void runMigration() {
        log.info("==============================================");
        log.info(">>> СТАРТ МИГРАЦИИ FLYWAY <<<");
        log.info("==============================================");

        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(url, username, password)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .baselineVersion("0")
                    .load();

            flyway.migrate();
            log.info("===========================================================");
            log.info(">>> FLYWAY: МИГРАЦИЯ УСПЕШНО ЗАВЕРШЕНА! ТАБЛИЦЫ СОЗДАНЫ <<<");
            log.info("===========================================================");

        } catch (Exception e) {
            log.error("===========================================================");
            log.error(">>> КРИТИЧЕСКАЯ ОШИБКА ЗАПУСКА FLYWAY: " + e);
            log.error("===========================================================");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
