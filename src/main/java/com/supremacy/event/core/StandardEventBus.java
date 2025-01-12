package com.supremacy.event.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.supremacy.event.*;
import com.supremacy.event.consumer.EventConsumer;
import com.supremacy.event.consumer.context.ConsumerContext;
import com.supremacy.event.events.Event;
import com.supremacy.event.lookup.EventIDService;

import java.util.*;
import java.util.function.Predicate;

@Singleton
public final class StandardEventBus implements EventBus {
    private final Collection<ConsumerContext<?>>[] eventConsumers;
    private final Map<EventSubscriber, Set<ConsumerContext<?>>> subscribersConsumersMap;

    @SuppressWarnings("unchecked")
    @Inject
    public StandardEventBus(final EventIDService idService) {
        this.eventConsumers = new Collection[idService.getMaxID() + 1];
        for (int i = 0; i <= idService.getMaxID(); i++) {
            eventConsumers[i] = new HashSet<>();
        }
        this.subscribersConsumersMap = new HashMap<>();
    }

    @Override
    public EventLinker constructLinker(EventSubscriber eventSubscriber) {
        return new StandardEventLinker(eventSubscriber);
    }

    @Override
    public void subscribe(EventSubscriber eventSubscriber) {
        final Set<ConsumerContext<?>> consumerContexts = subscribersConsumersMap.get(eventSubscriber);
        if (consumerContexts == null || consumerContexts.isEmpty()) {
            System.out.println("consumerContexts is null, returning");
            return;
        }

        for (ConsumerContext<?> consumer : consumerContexts) {

            eventConsumers[consumer.id()].add(consumer);
        }
    }

    @Override
    public void unsubscribe(EventSubscriber eventSubscriber) {
        final Set<ConsumerContext<?>> consumerContexts = subscribersConsumersMap.get(eventSubscriber);
        if (consumerContexts == null || consumerContexts.isEmpty()) {
            return;
        }

        for (ConsumerContext<?> consumer : consumerContexts) {
            eventConsumers[consumer.id()].remove(consumer);
        }
    }

    @Override
    public <T extends Event> void publish(T event) {
        final Collection<ConsumerContext<?>> consumers = eventConsumers[event.id()];
        if (consumers.isEmpty()) {
            return;
        }


        for (ConsumerContext<?> consumer : consumers) {
            @SuppressWarnings("unchecked")
            ConsumerContext<T> typedConsumer = (ConsumerContext<T>) consumer;
            typedConsumer.consumer().consume(event);
        }
    }

    public final class StandardEventLinker implements EventLinker {
        private final EventSubscriber eventSubscriber;

        private StandardEventLinker(EventSubscriber eventSubscriber) {
            this.eventSubscriber = eventSubscriber;
            subscribersConsumersMap.computeIfAbsent(eventSubscriber, k -> new HashSet<>());
        }

        @Override
        public <T extends Event> EventLinker link(EventConsumer<T> listener, int eventID) {
            return link(listener, e -> true, eventID);
        }

        @Override
        public <T extends Event> EventLinker link(EventConsumer<T> listener, Predicate<T> acceptIfTrue, int eventID) {
            final ConsumerContext<T> context = new ConsumerContext<>(e -> {
                if (acceptIfTrue.test(e)) {
                    listener.consume(e);
                }
            }, eventID);

            subscribersConsumersMap.get(eventSubscriber).add(context);
            return this;
        }
    }
}
