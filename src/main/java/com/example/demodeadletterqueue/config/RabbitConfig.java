package com.example.demodeadletterqueue.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${rabbit.queue.name}")
    private String queueName;

    @Value("${rabbit.exchange.name}")
    private String exchangeName;

    @Value("${rabbit.routing.key}")
    private String routingKey;

    @Value("${rabbit.parking.lot}")
    private String parkingLot;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(routingKey);
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(queueName)
                .deadLetterExchange(exchangeName + ".dlx")
                .build();
    }

    @Bean
    public DirectExchange exchange() {
        return ExchangeBuilder.directExchange(exchangeName)
                .build();
    }

    @Bean
    public Binding deadLetterBinding(){
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange());
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder
                .durable(queueName + ".dlx")
                .build();
    }

    @Bean
    public FanoutExchange deadLetterExchange() {
        return ExchangeBuilder.fanoutExchange(exchangeName + ".dlx").delayed()
                .build();
    }

    @Bean
    public Queue parkingLot(){
        return new Queue(parkingLot);
    }
}