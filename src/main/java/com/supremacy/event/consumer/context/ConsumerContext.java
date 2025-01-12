package com.supremacy.event.consumer.context;

import com.supremacy.event.consumer.EventConsumer;
import com.supremacy.event.events.Event;

public record ConsumerContext<T extends Event>(EventConsumer<T> consumer, int id) {}
