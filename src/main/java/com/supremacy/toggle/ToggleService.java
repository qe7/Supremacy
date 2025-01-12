package com.supremacy.toggle;

import com.google.inject.Inject;
import com.supremacy.event.core.EventBus;

import java.util.HashMap;
import java.util.Map;

public final class ToggleService {

    // module -> enabled map
    private final Map<Toggleable, Boolean> toggleableMap;

    private final EventBus eventBus;

    @Inject
    public ToggleService(final EventBus eventBus) {
        this.eventBus = eventBus;
        this.toggleableMap = new HashMap<>();
    }

    public void toggle(final Toggleable toggleable, final boolean state) {
        final boolean toggled = this.toggleableMap.computeIfAbsent(toggleable, _ -> false);

        if (toggled == state) {
            return;
        }

        if (state) {
            this.eventBus.subscribe(toggleable);

            toggleable.onEnable();

            this.toggleableMap.put(toggleable, true);
        } else {
            this.eventBus.unsubscribe(toggleable);

            toggleable.onDisable();

            this.toggleableMap.remove(toggleable);
        }


        this.toggleableMap.put(toggleable, state);
    }



    public boolean isModuleEnabled(final Toggleable toggleable) {
        // set all modules to disabled by default
        return this.toggleableMap.computeIfAbsent(toggleable, t -> false);
    }
}
