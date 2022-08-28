package com.mom.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.host}")
    private String host;
    @Value("${rabbitmq.username}")
    private String username;
    @Value("${rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.phone.queue}")
    private String phoneQueueName;
    @Value("${rabbitmq.laptop.queue}")
    private String laptopQueueName;
    @Value("${rabbitmq.tv.queue}")
    private String tvQueueName;
    @Value("${rabbitmq.dlq.order.queue}")
    private String dlqOrderQueue;
    @Value("${rabbitmq.wait.dlq.tv.queue}")
    private String waitDlqTvQueue;
    @Value("${rabbitmq.order.exchange}")
    private String orderExchange;
    @Value("${rabbitmq.wait.dlq.tv.exchange}")
    private String waitDlqTvExchange;
    @Value("${rabbitmq.dlq.order.exchange}")
    private String dlqOrderExchange;
    @Value("${rabbitmq.phone.routingkey}")
    private String phoneRoutingkey;
    @Value("${rabbitmq.laptop.routingkey}")
    private String laptopRoutingkey;
    @Value("${rabbitmq.tv.routingkey}")
    private String tvRoutingkey;
    @Value("${rabbitmq.dlq.routingkey}")
    private String dlqOrderRoutingkey;
    @Value("${rabbitmq.wait.dlq.tv.routingkey}")
    private String waitDlqTvRoutingkey;

    //Task1: reusing existing infrastructure
//    @Bean
//    @Qualifier("phoneQueue")
//    Queue phoneQueue() {
//        return new Queue(phoneQueueName, true);
//    }
//
//    @Bean
//    @Qualifier("laptopQueue")
//    Queue laptopQueue() {
//        return new Queue(laptopQueueName, true);//durable
//    }
//
//    @Bean
//    DirectExchange exchange() {
//        return new DirectExchange(exchange);
//    }
//
//    @Bean
//    Binding phoneBinding(@Qualifier("phoneQueue") Queue queue, DirectExchange exchange) {
//        return BindingBuilder.bind(queue).to(exchange).with(phoneRoutingkey);
//    }
//
//    @Bean
//    Binding laptopBinding(@Qualifier("laptopQueue") Queue queue, DirectExchange exchange) {
//        return BindingBuilder.bind(queue).to(exchange).with(laptopRoutingkey);
//    }


    //Creating infrastructure from code
    @Bean
    Queue laptopQueue() {
        return QueueBuilder.durable(laptopQueueName).build();
    }

    @Bean
    Queue phoneQueue() {
        return QueueBuilder.durable(phoneQueueName).withArgument("x-dead-letter-exchange", dlqOrderExchange)
                .withArgument("x-dead-letter-routing-key", dlqOrderRoutingkey).build();
    }

    @Bean
    Queue dlqOrderQueue() {
        return QueueBuilder.durable(dlqOrderQueue).build();
    }

    @Bean
    DirectExchange orderExchange() {
        return new DirectExchange(orderExchange);
    }

    @Bean
    DirectExchange dlqOrderExchange() {
        return new DirectExchange(dlqOrderExchange);
    }

    @Bean
    Binding laptopBinding() {
        return BindingBuilder.bind(laptopQueue()).to(orderExchange()).with(laptopRoutingkey);
    }

    @Bean
    Binding phoneBinding() {
        return BindingBuilder.bind(phoneQueue()).to(orderExchange()).with(phoneRoutingkey);
    }

    @Bean
    Binding dlqOrderBinding() {
        return BindingBuilder.bind(dlqOrderQueue()).to(dlqOrderExchange()).with(dlqOrderRoutingkey);
    }


    //Task4
    @Bean
    Queue tvQueue() {
        return QueueBuilder.durable(tvQueueName)
                .deadLetterExchange(waitDlqTvExchange)
                .deadLetterRoutingKey(waitDlqTvRoutingkey)
                .build();
    }

    @Bean
    DirectExchange waitDlqTvExchange() {
        return new DirectExchange(waitDlqTvExchange);
    }

    @Bean
    Queue waitDLQTvQueue() {
        return QueueBuilder.durable(waitDlqTvQueue)
                .deadLetterExchange(orderExchange)
                .deadLetterRoutingKey(tvRoutingkey)
                .ttl(10_000)
                .build();
    }

    @Bean
    Binding tvBinding(){
        return BindingBuilder.bind(tvQueue()).to(orderExchange()).with(tvRoutingkey);
    }

    @Bean
    Binding waitDlqTvBinding() {
        return BindingBuilder.bind(waitDLQTvQueue()).to(waitDlqTvExchange()).with(waitDlqTvRoutingkey);
    }

}