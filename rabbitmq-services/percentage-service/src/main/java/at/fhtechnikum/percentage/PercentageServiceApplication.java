package at.fhtechnikum.percentage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "at.technikumwien.database.repository")
@EntityScan(basePackages = "at.technikumwien.database.entity")
public class PercentageServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PercentageServiceApplication.class, args);
    }
}