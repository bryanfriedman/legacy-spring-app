package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

@RestController
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping(value = "/", produces=MediaType.APPLICATION_JSON_VALUE)
    public String hello() {
        logger.info("Calling external service...");
        RestTemplate client = new RestTemplate();
        String response = client.getForObject("https://httpbin.org/get", String.class);
        return response;
    }
}