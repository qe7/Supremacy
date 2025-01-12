package com.supremacy.animation;

public class Animation {

    private final Easing easing;
    private long duration;
    private long startTime;
    private double startValue;
    private double destinationValue;
    private double value;
    private boolean finished;

    // New variable to handle the direction of the animation
    private Direction direction = Direction.FORWARDS;

    public Animation(final Easing easing, final long duration) {
        this.easing = easing;
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
    }

    public void run(final double destinationValue) {
        if (this.destinationValue != destinationValue) {
            this.destinationValue = destinationValue;
            this.reset();
        } else {
            this.finished = System.currentTimeMillis() - this.startTime >= this.duration;

            if (this.finished) {
                this.value = destinationValue;
                return;
            }
        }

        final double progress = this.getProgress();
        final double result = this.easing.getFunction().apply(progress);

        // Adjust value calculation based on the direction
        if (this.direction == Direction.FORWARDS) {
            this.value = this.startValue + (destinationValue - this.startValue) * result;
        } else {
            this.value = this.startValue - (this.startValue - destinationValue) * result;
        }
    }

    public double getProgress() {
        return (double) (System.currentTimeMillis() - this.startTime) / (double) this.duration;
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
        this.startValue = this.value;
        this.finished = false;
    }

    // Setter for direction
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    // Getter for the value
    public double getValue() {
        return value;
    }

    public boolean isFinished() {
        return finished;
    }

    // Other getters and setters
    public Easing getEasing() {
        return easing;
    }

    public long getDuration() {
        return duration;
    }

    public long getStartTime() {
        return startTime;
    }

    public double getStartValue() {
        return startValue;
    }

    public double getDestinationValue() {
        return destinationValue;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setStartValue(double startValue) {
        this.startValue = startValue;
    }

    public void setDestinationValue(double destinationValue) {
        this.destinationValue = destinationValue;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}
