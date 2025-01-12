package com.supremacy.event.events.type.key;

import com.supremacy.event.events.Event;

public record KeyPressEvent(int key) implements Event {
    public static final int ID = 3;

    @Override
    public int id() {
        return ID;
    }
}
