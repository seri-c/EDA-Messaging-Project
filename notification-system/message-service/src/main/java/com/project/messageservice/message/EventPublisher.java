package com.project.messageservice.message;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.project.messageservice.message.dto.MessageEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final RabbitTemplate rabbitTemplate;

    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(MessageEvent event) {
        rabbitTemplate.convertAndSend("message.exchange","message.created", "event");
        log.info("Published a %s event".formatted(event.eventType()));
    }
}
