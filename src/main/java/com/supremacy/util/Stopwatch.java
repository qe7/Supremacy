package com.supremacy.util;

public final class Stopwatch {

    private long startTime;

    public Stopwatch() {
        this.startTime = System.currentTimeMillis();
    }

    public long elapsed() {
        return System.currentTimeMillis() - this.startTime;
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
    }

    public boolean elapsed(final long time) {
        return this.elapsed() >= time;
    }

    public boolean elapsed(final long time, final boolean reset) {
        if (this.elapsed(time)) {
            if (reset) {
                this.reset();
            }
            return true;
        }
        return false;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.startTime;
    }
}