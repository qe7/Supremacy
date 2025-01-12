package com.supremacy.event.consumer;

import com.supremacy.event.events.Event;

@FunctionalInterface
public interface EventConsumer<T extends Event> {
    void consume(T event);
}