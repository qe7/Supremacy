package com.supremacy.event;

import com.supremacy.event.consumer.EventConsumer;
import com.supremacy.event.events.Event;

import java.util.function.Predicate;

public interface EventLinker {
    <T extends Event> EventLinker link(EventConsumer<T> listener, int eventID);

    <T extends Event> EventLinker link(EventConsumer<T> listener, Predicate<T> acceptIfTrue, int eventID);
}
