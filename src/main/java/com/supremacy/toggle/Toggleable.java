package com.supremacy.toggle;

import com.supremacy.event.EventSubscriber;

public interface Toggleable extends EventSubscriber {
    default void onEnable() {

    }

    default void onDisable() {

    }
}
