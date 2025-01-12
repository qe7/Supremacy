package com.supremacy.event.events;

public interface CancellableEvent extends Event {
    boolean isCancelled();

    void setCancelled(boolean cancelled);

    void cancel();
}