package com.labudzinski.EventDispatcher;

import java.util.ArrayList;
import java.util.HashMap;

public interface EventDispatcherInterface {
    void addListener(String eventName, ClosureRunnable listener) throws Throwable;

    void addListener(String eventName, ClosureRunnable listener, Integer priority) throws Throwable;

    void addSubscriber(EventSubscriberInterface subscriber) throws Throwable;

    void removeListener(String eventName, ClosureRunnable listener) throws Throwable;

    void removeSubscriber(EventSubscriberInterface subscriber) throws Throwable;

    HashMap<String, ArrayList<ClosureRunnable>> getListeners();

    ArrayList<ClosureRunnable> getListeners(String eventName);

    Integer getListenerPriority(String eventName, ClosureRunnable listener);

    boolean hasListeners(String eventName);

    boolean hasListeners();

    Event dispatch(Event event, String eventName);
}
