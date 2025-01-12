package com.supremacy.event.events;

/**
 * @author G8LOL
 * @since 10/26/2024
 */
public abstract class AbstractCancellable implements Event {

    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void cancel() {
        setCancelled(true);
    }
}
