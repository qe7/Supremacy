package com.supremacy.event.events;

public abstract class AbstractCancellableEvent implements CancellableEvent {

    private boolean cancelled;

    protected AbstractCancellableEvent() {}

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public void cancel() {
        this.setCancelled(true);
    }
}