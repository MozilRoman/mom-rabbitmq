package com.mom.rabbitmq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mom.rabbitmq.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class ProducerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerService.class);

    @Value("${rabbitmq.order.exchange}")
    private String exchange;
    @Value("${rabbitmq.phone.routingkey}")
    private String phoneRoutingkey;
    @Value("${rabbitmq.laptop.routingkey}")
    private String laptopRoutingkey;
    @Value("${rabbitmq.tv.routingkey}")
    private String tvRoutingkey;

    private AmqpTemplate rabbitTemplate;

    @Autowired
    public ProducerService(AmqpTemplate rabbitTemplate) {//ignore Autowired WARN
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send() throws JsonProcessingException {
        Order phoneOrder = new Order("Phone",ThreadLocalRandom.current().nextInt(1, 1000));
        Order laptopOrder = new Order("Laptop",ThreadLocalRandom.current().nextInt(1, 1000));
        ObjectMapper objectMapper = new ObjectMapper();
        String phoneOrderString = objectMapper.writeValueAsString(phoneOrder);
        String laptopOrderString = objectMapper.writeValueAsString(laptopOrder);

        try{
            rabbitTemplate.convertAndSend(exchange, phoneRoutingkey, phoneOrderString);
            rabbitTemplate.convertAndSend(exchange, laptopRoutingkey, laptopOrderString);
            LOGGER.info("send {} to queue(exchange) {}", phoneOrder, exchange);
            LOGGER.info("send {} to queue(exchange) {}", laptopOrder, exchange);
        }
        catch (Exception e){
            LOGGER.error("Error", e);
        }
    }

    public void sendInvalidMsg() {
        String phoneOrderString = "{\"name\":\"Phone\",\"id\":\"IdShouldBeANumber\"}";

        try {
            rabbitTemplate.convertAndSend(exchange, phoneRoutingkey, phoneOrderString);
            LOGGER.info("send {} to queue(exchange) {}", phoneOrderString, exchange);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
    }

    public void sendLaptopMsgRetryTest() {
        String laptopOrderString = "{\"name\":\"Laptop\",\"id\":\"IdShouldBeANumber\"}";

        try {
            rabbitTemplate.convertAndSend(exchange, laptopRoutingkey, laptopOrderString);
            LOGGER.info("send {} to queue(exchange) {}", laptopOrderString, exchange);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
    }

    public void sendTvMsgTest(){
        String tvString = "{\"name\":\"Tv\",\"id\":\"IdShouldBeANumber\"}";

        try {
            rabbitTemplate.convertAndSend(exchange, tvRoutingkey, tvString);
            LOGGER.info("send {} to queue(exchange) {}", tvString, exchange);
        } catch (Exception e) {
            LOGGER.error("Error", e);
        }
    }

}
