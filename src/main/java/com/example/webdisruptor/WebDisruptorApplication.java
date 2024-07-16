package com.example.webdisruptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
public class WebDisruptorApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebDisruptorApplication.class, args);
    }

    @Bean
    public Map<Long, Long> databaseMap() {
        Map<Long, Long> db = new ConcurrentHashMap<>();
        for (int i = 1; i < 1_000_000; i++) {
            db.put((long) i, ThreadLocalRandom.current().nextLong(20L));
        }
        return db;
    }

}
