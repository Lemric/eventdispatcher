package com.labudzinski.EventDispatcher;

import java.util.ArrayList;

public interface EventDispatcherInterface {
    void addListener(String eventName, EventListenerInterface listener) throws Throwable;

    void addListener(String eventName, EventListenerInterface listener, Integer priority) throws Throwable;

    void addSubscriber(EventSubscriberInterface subscriber) throws Throwable;

    void removeListener(String eventName, EventListenerInterface listener) throws Throwable;

    void removeSubscriber(EventSubscriberInterface subscriber) throws Throwable;

    ArrayList<ArrayList<EventListenerInterface>> getListeners();

    ArrayList<ArrayList<EventListenerInterface>> getListeners(String eventName);

    ArrayList<String> getListenersAsArrayList();

    ArrayList<String> getListenersAsArrayList(String eventName);

    Integer getListenerPriority(String eventName, Object listener);

    boolean hasListeners(String eventName);

    boolean hasListeners();

    Event dispatch(Event event, String eventName);
}
