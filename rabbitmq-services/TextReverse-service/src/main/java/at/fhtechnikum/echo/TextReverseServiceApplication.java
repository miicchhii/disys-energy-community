package at.fhtechnikum.echo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@SpringBootApplication
@EnableRabbit
public class TextReverseServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TextReverseServiceApplication.class, args);
    }
}