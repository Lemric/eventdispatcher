package com.labudzinski.eventdispatcher;

public interface StoppableEventInterface {
    boolean isPropagationStopped();

    void stopPropagation();
}
