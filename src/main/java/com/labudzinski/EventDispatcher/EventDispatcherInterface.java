package com.labudzinski.EventDispatcher;

public interface EventDispatcherInterface {
    void addListener(String eventName, EventListenerInterface listener) throws Throwable;

    void addSubscriber(EventSubscriberInterface subscriber) throws Throwable;

    void removeListener(String eventName, EventListenerInterface listener) throws Throwable;

    void removeSubscriber(EventSubscriberInterface subscriber) throws Throwable;

    Object getListeners(String eventName);

    Integer getListenerPriority(String eventName, EventListenerInterface listener);

    boolean hasListeners(String eventName);

    Event dispatch(Event event, String eventName);
}
