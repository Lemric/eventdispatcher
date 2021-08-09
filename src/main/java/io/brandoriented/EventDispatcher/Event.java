package io.brandoriented.EventDispatcher;

public class Event implements StoppableEventInterface {

    private boolean propagationStopped = false;

    @Override
    public boolean isPropagationStopped() {
        return this.propagationStopped;
    }

    @Override
    public void stopPropagation() {
        this.propagationStopped = true;
    }
}
