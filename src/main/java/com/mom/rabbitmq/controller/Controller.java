package com.mom.rabbitmq.controller;

import com.mom.rabbitmq.service.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Controller {
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
    private ProducerService producerService;

    @Autowired
    public Controller(ProducerService producerService) {
        this.producerService = producerService;
    }

    //Task1
    @GetMapping("/produceToExchange")
    public ResponseEntity<String> produceToQueue() {
        try {
            LOGGER.info("preparing msg");
            producerService.send();
            return new ResponseEntity<>("Produced ADD messages successfully", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("ADD messages did not delivered", HttpStatus.BAD_REQUEST);
        }
    }

    //Task2
    @GetMapping("/produceInvalidMsgToQueue")
    public ResponseEntity<String> produceInvalidMsgToQueue() {
        try {
            LOGGER.info("preparing msg");
            producerService.sendInvalidMsg();
            return new ResponseEntity<>("Produced ADD messages successfully", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("ADD messages did not delivered", HttpStatus.BAD_REQUEST);
        }
    }

    //Task3
    @GetMapping("/produceLaptopMsgRetryToQueue")
    public ResponseEntity<String> produceLaptopMsgRetryToQueue() {
        try {
            LOGGER.info("preparing msg");
            producerService.sendLaptopMsgRetryTest();
            return new ResponseEntity<>("Produced ADD messages successfully", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("ADD messages did not delivered", HttpStatus.BAD_REQUEST);
        }
    }

    //Task4
    @GetMapping("/produceTvMsgToQueue")
    public ResponseEntity<String> produceTvMsgToQueue() {
        try {
            LOGGER.info("preparing msg");
            producerService.sendTvMsgTest();
            return new ResponseEntity<>("Produced ADD messages successfully", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>("ADD messages did not delivered", HttpStatus.BAD_REQUEST);
        }
    }

}
