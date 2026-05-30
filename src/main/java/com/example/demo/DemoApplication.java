package com.example.demo;

import com.example.demo.DBFlyway.DatabaseMigrator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);

//		ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
//
// Закомментировано для перехода на стандартный автозапуск Spring Boot
//		DatabaseMigrator migrator = context.getBean(DatabaseMigrator.class);
//		migrator.runMigration();
	}
}
