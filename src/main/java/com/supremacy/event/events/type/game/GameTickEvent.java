package com.supremacy.event.events.type.game;

import com.supremacy.event.events.AbstractPrePostEvent;

public final class GameTickEvent extends AbstractPrePostEvent {

    public static final int ID = 4;


    @Override
    public int id() {
        return ID;
    }
}
