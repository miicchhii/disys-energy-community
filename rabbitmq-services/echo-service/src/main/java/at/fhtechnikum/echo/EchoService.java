package at.fhtechnikum.echo;

import at.fhtechnikum.shared.EchoMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Queue;
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
public class EchoService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "echo.input", ackMode = "MANUAL")
    public void processText(@Payload byte[] messageBytes,
                            Channel channel,
                            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        String jsonString = new String(messageBytes, StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();

        EchoMessage msg = objectMapper.readValue(jsonString, EchoMessage.class);

        System.out.println("=== ECHO SERVICE ===");
        System.out.println("Received: " + msg);
        String echoMessage = msg.getMessage();

        String processed = msg.getMessage()+" (echoed)";
        EchoMessage outMsg = new EchoMessage(processed);   //Die Message für die nachfolgende Queue

        System.out.println("Processed: " + echoMessage);
        System.out.println("===========================\n");


        // positively acknowledge the message
        channel.basicAck(deliveryTag, false);

        rabbitTemplate.convertAndSend(   //Send the Message to the next Queue
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.OUTPUT_QUEUE, outMsg
        );


    }
}
