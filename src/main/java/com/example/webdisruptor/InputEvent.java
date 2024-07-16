package com.example.webdisruptor;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Data
public class InputEvent {

    private Map<Long, Long> items;
    private CompletableFuture<String> response;

    public void clear() {
        items = null;
        response = null;
    }
}
