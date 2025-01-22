package com.supremacy.event.events.type.game;

import com.supremacy.event.events.AbstractPrePostEvent;

public class HandleInputEvent extends AbstractPrePostEvent {

    public static final int ID = 6;

    @Override
    public int id() {
        return ID;
    }
}
