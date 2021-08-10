package com.labudzinski.EventDispatcher;

public interface EventListenerInterface {
    Integer getPriority();

    String getMethod();

    Object getParameters();
}
