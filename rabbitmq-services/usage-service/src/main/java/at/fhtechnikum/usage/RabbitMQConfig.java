package at.fhtechnikum.usage;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String INPUT_QUEUE = "energy.input";
    public static final String OUTPUT_QUEUE = "energy.output";
    public static final String EXCHANGE_NAME = "energy.processing.exchange";

    @Bean
    public DirectExchange energyProcessingExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue inputQueue() {
        return QueueBuilder.durable(INPUT_QUEUE).build();
    }

    @Bean
    public Queue outputQueue() {
        return QueueBuilder.durable(OUTPUT_QUEUE).build();
    }

    @Bean
    public Binding inputBinding() {
        return BindingBuilder
                .bind(inputQueue())
                .to(energyProcessingExchange())
                .with(INPUT_QUEUE); // routing key: "energy.input"
    }

    @Bean
    public Binding outputBinding() {
        return BindingBuilder
                .bind(outputQueue())
                .to(energyProcessingExchange())
                .with(OUTPUT_QUEUE); // routing key: "energy.output"
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
}