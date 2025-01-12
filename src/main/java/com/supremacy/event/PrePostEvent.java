package com.supremacy.event;

import com.supremacy.event.events.Event;

public interface PrePostEvent extends Event {
    boolean isPre();

    boolean isPost();

    void setState(boolean state);

    void setPost();
}