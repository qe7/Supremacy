package com.supremacy.bind;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

public final class StandardBindService implements BindService {
    private final Map<Bindable, Integer> bindableKeyMap;

    @Inject
    public StandardBindService() {
        this.bindableKeyMap = new HashMap<>();
    }

    @Override
    public void bind(final Bindable bindable, final int key) {
        if (this.bindableKeyMap.containsKey(bindable)) {
            this.unbind(bindable);
        }

        this.bindableKeyMap.put(bindable, key);
    }

    @Override
    public int getKey(final Bindable bindable) {
        return this.bindableKeyMap.getOrDefault(bindable, -1);
    }

    @Override
    public boolean isBound(final Bindable bindable) {
        return this.bindableKeyMap.containsKey(bindable);
    }

    @Override
    public void unbind(final Bindable bindable) {
        this.bindableKeyMap.remove(bindable);
    }
}
