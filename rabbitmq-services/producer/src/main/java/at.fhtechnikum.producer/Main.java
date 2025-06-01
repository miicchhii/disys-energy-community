package at.fhtechnikum.producer;

import at.fhtechnikum.shared.EchoMessage;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

public class Main {
    public static void main(String[] args) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());

        Queue queue = new Queue("echo.input");

        EchoMessage echoMessage = new EchoMessage("This is our echo message from the producer");

        template.convertAndSend("echo.processing.exchange", queue.getName(), echoMessage);

    }
}
