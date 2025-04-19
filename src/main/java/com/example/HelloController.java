package com.example;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;

@RestController
public class HelloController {

    @RequestMapping(value = "/", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public String hello() {
        System.out.println("Calling external service...");
        RestTemplate client = new RestTemplate();
        String response = client.getForObject("https://httpbin.org/get", String.class);
        return response;
    }
}