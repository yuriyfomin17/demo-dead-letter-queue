package com.example.demodeadletterqueue;

import com.example.demodeadletterqueue.dto.TestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {

    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbit.exchange.name}")
    private String exchange;
    @Value("${rabbit.routing.key}")
    private String key;
    @EventListener(ContextRefreshedEvent.class)
    public void sendMessage(){
        var message = new TestMessage("exception");
        rabbitTemplate.convertAndSend(exchange, key, message);
    }
}
