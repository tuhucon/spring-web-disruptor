package com.example.webdisruptor;

import com.lmax.disruptor.EventHandler;

import javax.xml.crypto.Data;
import java.util.concurrent.ConcurrentHashMap;

public class InputEventHandler implements EventHandler<InputEvent> {

    @Override
    public void onEvent(InputEvent inputEvent, long l, boolean b) throws Exception {
        if (inputEvent.getItems() == null || inputEvent.getResponse() == null) {
            //nothing to do, just return
            return;
        }
        try {
            //validate
            for (var entry: inputEvent.getItems().entrySet()) {
                Long currentQuantity = Database.instance.getOrDefault(entry.getKey(), 0L);
                if (currentQuantity < entry.getValue()) {
                    throw new RuntimeException(String.format("%d dont have enough quality: current: %d, request: %d", entry.getKey(), currentQuantity, entry.getValue()));
                }
            }
            //update db
            for (var entry: inputEvent.getItems().entrySet()) {
                Long currentQuantity = Database.instance.getOrDefault(entry.getKey(), 0L);
                Database.instance.put(entry.getKey(), currentQuantity - entry.getValue());
            }
            inputEvent.getResponse().complete("Order is created");
        } catch (Exception ex) {
            System.out.println(ex);
            inputEvent.getResponse().completeExceptionally(ex);
        } finally {
            inputEvent.clear();
        }
    }
}
