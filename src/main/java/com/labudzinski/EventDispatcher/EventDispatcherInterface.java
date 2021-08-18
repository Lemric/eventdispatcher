package com.labudzinski.EventDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface EventDispatcherInterface {
    EventDispatcherInterface addListener(String eventName, EventListener<? extends Event> listener) throws Throwable;

    EventDispatcherInterface addListener(String eventName, EventListener<? extends Event> listener, Integer priority) throws Throwable;

    void addSubscriber(EventSubscriberInterface subscriber) throws Throwable;

    EventDispatcher removeListener(String eventName, EventListener<? extends Event> listener) throws Throwable;

    void removeSubscriber(EventSubscriberInterface subscriber) throws Throwable;

    List<EventListener<? extends Event>> getListeners();

    List<EventListener<? extends Event>> getListeners(String eventName);

    Integer getListenerPriority(String eventName, EventListener<? extends Event> listener);

    boolean hasListeners(String eventName);

    boolean hasListeners();

    public <T extends Event> T dispatch(T event, String eventName);
}
