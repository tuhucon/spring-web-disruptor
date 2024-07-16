package com.example.webdisruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequiredArgsConstructor
public class HelloController {

    private final RingBuffer<InputEvent> inputRingBuffer;

    @GetMapping("/publish")
    public String publicEvent() {
        inputRingBuffer.publishEvent((t, s) ->
                System.out.println(String.format("public event %s with sequence %d in thread %s", t.toString(), s, Thread.currentThread().toString())));
        return "OK";
    }

    @GetMapping("/hello")
    public String hello() throws ExecutionException, InterruptedException {
        CompletableFuture<String> x = new CompletableFuture<>();
        Thread.ofVirtual().start(() -> {
            try {
                Thread.sleep(1_000L);
                if (ThreadLocalRandom.current().nextInt(10) < 8) {
                    throw new RuntimeException("Random exception for completable future");
                } else {
                    x.complete("Hello Tu hu con");
                }
            } catch (InterruptedException e) {
                x.exceptionNow();
            } catch (RuntimeException ex) {
                x.completeExceptionally(ex);
            }
        });
        return x.get();
    }

    @GetMapping("/products")
    public Map<Long, Long> products(@RequestParam List<Long> ids) {
        Map<Long, Long> items = new HashMap<>();
        for (Long id: ids) {
            items.put(id, Database.instance.getOrDefault(id, 0L));
        }
        return items;
    }

    @PostMapping("/products")
    public String buyProducts(@RequestBody Map<Long, Long> items) {
        CompletableFuture<String> result = new CompletableFuture<>();

        inputRingBuffer.publishEvent((event, seq, eventItems, eventResult) -> {
            System.out.println("public event at seq = " + seq);
            event.setItems(eventItems);
            event.setResponse(eventResult);
        }, items, result);

        try {
            return result.get();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
