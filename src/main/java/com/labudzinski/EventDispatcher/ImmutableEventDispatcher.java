package com.labudzinski.EventDispatcher;

import com.labudzinski.EventDispatcher.exceptions.BadMethodCallException;

public class ImmutableEventDispatcher implements EventDispatcherInterface {

    private final EventDispatcherInterface dispatcher;

    public ImmutableEventDispatcher(EventDispatcherInterface dispatcher) {

        this.dispatcher = dispatcher;
    }

    public Event dispatch(Event event, String eventName) {
        return this.dispatcher.dispatch(event, eventName);
    }

    @Override
    public void addListener(String eventName, EventListenerInterface listener) throws Throwable {
        throw new BadMethodCallException("Unmodifiable event dispatchers must not be modified.");
    }

    @Override
    public void addSubscriber(EventSubscriberInterface subscriber) throws Throwable {
        throw new BadMethodCallException("Unmodifiable event dispatchers must not be modified.");
    }

    @Override
    public void removeListener(String eventName, EventListenerInterface listener) throws Throwable {
        throw new BadMethodCallException("Unmodifiable event dispatchers must not be modified.");
    }

    @Override
    public void removeSubscriber(EventSubscriberInterface subscriber) throws Throwable {
        throw new BadMethodCallException("Unmodifiable event dispatchers must not be modified.");
    }

    @Override
    public Object getListeners(String eventName) {
        return null;
    }

    @Override
    public Integer getListenerPriority(String eventName, EventListenerInterface listener) {
        return this.dispatcher.getListenerPriority(eventName, listener);
    }

    @Override
    public boolean hasListeners(String eventName) {
        return this.dispatcher.hasListeners(eventName);
    }
}
