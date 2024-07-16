package com.example.webdisruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
public class WebDisruptorApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebDisruptorApplication.class, args);
    }

    @PostConstruct
    private void initDatabase() {
        for (int i = 1; i < 1_000_000; i++) {
            Database.instance.put((long) i, ThreadLocalRandom.current().nextLong(20L));
        }
    }

    @Bean
    public Disruptor<InputEvent> inputDisruptor() {
        InputEventFactory inputEventFactory = new InputEventFactory();
        InputEventHandler handler1 = new InputEventHandler();
        InputEventHandler handler2 = new InputEventHandler();
        InputEventHandler handler3 = new InputEventHandler();
        Disruptor<InputEvent> disruptor = new Disruptor<>(inputEventFactory, 4, DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(handler1, handler2, handler2);
        disruptor.start();
        return disruptor;
    }

    @Bean
    public RingBuffer<InputEvent> inputRingBuffer(Disruptor<InputEvent> inputDisruptor) {
        return inputDisruptor.getRingBuffer();
    }
}
