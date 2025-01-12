package com.supremacy.event.lookup;


public final class StandardEventIDService implements EventIDService {

    private int max;

    public StandardEventIDService() {
        this.max = 32;
    }

    @Override
    public int getMaxID() {
        return this.max;
    }

    @Override
    public void registerEvent(final int id) {
        this.max = Math.max(this.max, id);
    }
}
