package io.brandoriented.EventDispatcher;

public interface StoppableEventInterface {
    public boolean isPropagationStopped();
    public void stopPropagation();
}
