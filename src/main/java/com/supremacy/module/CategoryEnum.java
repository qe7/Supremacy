package com.supremacy.module;

public enum CategoryEnum {
    COMBAT,
    MOVEMENT,
    PLAYER,
    RENDER,
    MISC,
    EXPLOIT
    ;

    public String getName() {
        return this.name().substring(0, 1).toUpperCase() + this.name().substring(1).toLowerCase();
    }
}
