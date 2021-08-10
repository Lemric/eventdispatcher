package com.labudzinski.EventDispatcher;

public interface StoppableEventInterface {
    boolean isPropagationStopped();

    void stopPropagation();
}
