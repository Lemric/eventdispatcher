package com.labudzinski.EventDispatcher;

public interface EventListenerInterface {

    String getMethod();

    EventListenerImpl getListener();

    Object getParameters();
}
