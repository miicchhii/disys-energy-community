package at.fhtechnikum.percentage;

import at.fhtechnikum.shared.EnergyMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class PercentageService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "energy.input", ackMode = "MANUAL")
    public void processEnergy(@Payload byte[] messageBytes,
                              Channel channel,
                              @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        String jsonString = new String(messageBytes, StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();

        // Empfange und deserialisiere EnergyMessage
        EnergyMessage msg = objectMapper.readValue(jsonString, EnergyMessage.class);

        System.out.println("=== ENERGY SERVICE ===");
        System.out.println("Received: " + msg.getType() +
                " | Assoc: " + msg.getAssociation() +
                " | kWh: " + msg.getKwh() +
                " | datetime: " + msg.getDatetime());


        // Update einer Datenbank (Platzhalter)
        // energyRepository.update(...);

        // optional: Ergebnis/Verbrauch/Statistik in eine Output-Queue weiterleiten
        // rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,
        //     RabbitMQConfig.OUTPUT_QUEUE, new EnergyResultMessage(...));

        // Message bestätigen
        channel.basicAck(deliveryTag, false);

        System.out.println("=======================\n");
    }
}