package com.supremacy.event.lookup;

public interface EventIDService {
    int getMaxID();
    void registerEvent(int id);
}