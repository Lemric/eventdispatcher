package com.labudzinski.EventDispatcher;

public interface EventListenerInterface {

    String getMethod();

    Object getListener();

    Object[] getParameters();
}
