package at.fhtechnikum.producer;

import at.fhtechnikum.shared.EnergyMessage;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import java.time.LocalDateTime;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Verbindung aufbauen
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());

        // Die Queue, an die gesendet wird
        String queueName = "energy.input"; // oder "energy_queue", je nachdem wie es im Backend heißt

        Random random = new Random();

        // Endlosschleife: Jede 5 Sekunden eine PRODUCER-Nachricht senden, wird zukünfigt mit Weather API ersetzt
        while (true) {
            double kwh = 0.002 + (0.004 - 0.002) * random.nextDouble(); // Zufallswert zwischen 0.002 und 0.004

            EnergyMessage energyMessage = new EnergyMessage(
                    "PRODUCER",
                    "COMMUNITY",
                    kwh,
                    LocalDateTime.now().toString()
            );


            template.convertAndSend(queueName, energyMessage);
            System.out.println("Gesendet: " + energyMessage.getType() + " " + energyMessage.getKwh());

            Thread.sleep(5000); // 5 Sekunden Pause
        }
    }
}
