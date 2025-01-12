package com.supremacy.event.events;

import com.supremacy.event.PrePostEvent;

public abstract class AbstractPrePostEvent implements PrePostEvent {

    private boolean post;

    protected AbstractPrePostEvent() {}

    @Override
    public final boolean isPre() {
        return !this.post;
    }

    @Override
    public final boolean isPost() {
        return this.post;
    }

    @Override
    public final void setState(final boolean state) {
        this.post = state;
    }

    @Override
    public final void setPost() {
        this.post = true;
    }
}
