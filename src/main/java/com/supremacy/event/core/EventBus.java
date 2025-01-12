package com.supremacy.event.core;

import com.supremacy.event.events.Event;
import com.supremacy.event.EventLinker;
import com.supremacy.event.EventSubscriber;

public interface EventBus {
    EventLinker constructLinker(EventSubscriber eventSubscriber);

    void subscribe(EventSubscriber eventSubscriber);
    void unsubscribe(EventSubscriber eventSubscriber);

    <T extends Event> void publish(T event);
}
