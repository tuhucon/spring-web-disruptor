package com.example.webdisruptor;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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

    private final Map<Long, Long> database;

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
        Map<Long, Long> result = new HashMap<>();
        for (Long id: ids) {
            result.put(id, database.getOrDefault(id, 0L));
        }
        return result;
    }
}
