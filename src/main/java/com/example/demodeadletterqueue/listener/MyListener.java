package com.example.demodeadletterqueue.listener;

import com.example.demodeadletterqueue.dto.TestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyListener {
    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbit.exchange.name}")
    private String exchange;
    @Value("${rabbit.routing.key}")
    private String key;
    @Value("${rabbit.parking.lot}")
    private String parkingLot;

    @RabbitListener(queues = "${rabbit.queue.name}")
    public void listen(TestMessage testMessage) {
        log.info("TestMessage {}", testMessage);
        if (testMessage.message().equals("exception")) {
            throw new RuntimeException("BAD_MESSAGE!");
        }
    }

    @RabbitListener(queues = "${rabbit.queue.name}" + ".dlx")
    public void listenDeadLetterQueue(Message badMessage) {
        Map<String, Object> headers = badMessage.getMessageProperties().getHeaders();
        String RABBIT_FAILED_RETRIES = "count";
        Integer retryCount = (Integer) headers.get(RABBIT_FAILED_RETRIES);
        if (retryCount == null) {
            retryCount = 0;
        }
        log.info("retryCount:" + retryCount);
        if (retryCount < 3) {
            headers.put(RABBIT_FAILED_RETRIES, retryCount + 1);
            headers.put("x-delay", 5000 * retryCount);
            rabbitTemplate.send(exchange, key, badMessage);
            log.info("Sending again {}", new String(badMessage.getBody()));
        } else {
            log.info("Sending to parking Lot ");
            rabbitTemplate.send(parkingLot, badMessage);
        }
    }
}
