package at.fhtechnikum.percentage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@SpringBootApplication
@EnableRabbit
public class PercentageServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PercentageServiceApplication.class, args);
    }
}