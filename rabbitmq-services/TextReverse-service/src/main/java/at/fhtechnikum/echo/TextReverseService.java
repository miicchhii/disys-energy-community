package at.fhtechnikum.echo;

import at.fhtechnikum.shared.EchoMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class TextReverseService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "echo.output", ackMode = "MANUAL")
    public void processText(@Payload byte[] messageBytes,
                            Channel channel,
                            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        String jsonString = new String(messageBytes, StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();

        EchoMessage msg = objectMapper.readValue(jsonString, EchoMessage.class);

        System.out.println("=== ECHO SERVICE ===");
        System.out.println("Received: " + msg);
        String echoMessage = msg.getMessage();
        echoMessage = new StringBuilder(echoMessage).reverse().toString();
        System.out.println("Processed: " + echoMessage);
        System.out.println("===========================\n");


        // positively acknowledge the message
        channel.basicAck(deliveryTag, false);
    }
}
