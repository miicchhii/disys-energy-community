package at.fhtechnikum.energy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@SpringBootApplication
@EnableRabbit
public class EnergyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnergyServiceApplication.class, args);
    }
}