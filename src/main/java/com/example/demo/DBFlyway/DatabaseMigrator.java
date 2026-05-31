package com.example.demo.DBFlyway;

import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("entityManagerFactory")
public class DatabaseMigrator {

    private final String url;
    private final String username;
    private final String password;

    // Спринг сам вычитает эти данные из нужного application.yml (локального или докеровского!)
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
        System.out.println("==============================================");
        System.out.println(">>> СТАРТ МИГРАЦИИ FLYWAY <<<");
        System.out.println("==============================================");

        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(url, username, password)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .baselineVersion("0")
                    .load();

            flyway.migrate();
            System.out.println("===========================================================");
            System.out.println(">>> FLYWAY: МИГРАЦИЯ УСПЕШНО ЗАВЕРШЕНА! ТАБЛИЦЫ СОЗДАНЫ <<<");
            System.out.println("===========================================================");

        } catch (Exception e) {
            System.out.println("===========================================================");
            System.err.println(">>> КРИТИЧЕСКАЯ ОШИБКА ЗАПУСКА FLYWAY: " + e.getMessage());
            System.out.println("===========================================================");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
