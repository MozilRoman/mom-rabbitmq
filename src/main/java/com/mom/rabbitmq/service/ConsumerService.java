package com.mom.rabbitmq.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mom.rabbitmq.entity.Order;
import com.mom.rabbitmq.exception.OrderParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.ImmediateRequeueAmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;



@Service
public class ConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);

    @RabbitListener(queues = {"${rabbitmq.phone.queue}"})
    public void phoneReceiver(@Payload String jsonMsg) {
        LOGGER.info("Phone receiver received {}", jsonMsg);
        Order order;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            order = objectMapper.readValue(jsonMsg, Order.class);
            LOGGER.info("Phone receiver converted Order: {}", order);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new OrderParsingException();//will be moved to DLQ
        }
    }

    @RabbitListener(queues = {"${rabbitmq.laptop.queue}"})
    public void laptopReceiver(@Payload String jsonMsg) {
        LOGGER.info("Laptop receiver received jsonMsg {}", jsonMsg);
        Order order;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            order = objectMapper.readValue(jsonMsg, Order.class);
            LOGGER.info("Laptop receiver converted Order: {}", order);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

//    //Task3 - retry 1 time
    /**
     *  Retry processing a message from DLQ for n times and then reject it.
     * For DLQ as a source there is extra metadata in Headers
     * Ex1:      var retriesCnt = msg.getHeaders().get("x-retries-count ", Boolean.class);
     * if (retriesCnt == null) retriesCnt = 1;
     *     if (retriesCnt > MAX_RETRIES_COUNT) {
     *         log.info("Discarding message");
     *         return;
     *     }
     *     //else - process msg
     * Ex2: var retriesCnt = msg.getHeaders().get("x-death", List.class);
     */
    //ParkingLot - it`s kind of Queue(DLQ) where send msg-s that exceed maxRetry limit
//    @RabbitListener(queues = {"${rabbitmq.laptop.queue}"})
//    public void laptopReceiver(@Payload Message<String> msg) {
//        LOGGER.info("Laptop receiver received Message. Body= {}, Headers = {}", msg.getPayload(), msg.getHeaders());
//        Order order;
//        var redelivered = msg.getHeaders().get("amqp_redelivered", Boolean.class);
//        if (redelivered) {
//            throw new AmqpRejectAndDontRequeueException("Discard msg");
//        } else {
//            try {
//                ObjectMapper objectMapper = new ObjectMapper();
//                order = objectMapper.readValue(msg.getPayload(), Order.class);
//                LOGGER.info("Laptop receiver converted Order: {}", order);
//            } catch (Exception e) {
//                LOGGER.error(e.getMessage());
//                throw new ImmediateRequeueAmqpException("Please retry again");//some business exception here e.g. OrderParsingException
//            }
//        }
//    }


    @RabbitListener(queues = {"${rabbitmq.dlq.order.queue}"})
    public void dlqOrderReceiver(@Payload String jsonMsg) {
        LOGGER.info("DLQ order receiver received jsonMsg {}", jsonMsg);
    }

    //Task4
    @RabbitListener(queues = {"${rabbitmq.tv.queue}"})
    public void tvReceiver(@Payload String jsonMsg) {
        LOGGER.info("Tv receiver received {}", jsonMsg);
        Order order;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            order = objectMapper.readValue(jsonMsg, Order.class);
            LOGGER.info("Tv receiver converted Order: {}", order);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new OrderParsingException();//will be moved to DLQ
        }
    }

}
