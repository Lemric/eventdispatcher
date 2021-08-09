package io.brandoriented.EventDispatcher;

import io.brandoriented.EventDispatcher.exceptions.BadMethodCallException;

public class ImmutableEventDispatcher implements EventDispatcherInterface {

    private EventDispatcherInterface dispatcher;

    public ImmutableEventDispatcher(EventDispatcherInterface dispatcher) {

        this.dispatcher = dispatcher;
    }

    public Object dispatch(Object event, String eventName) {
        return this.dispatcher.dispatch(event, eventName);
    }

    @Override
    public void addListener(String eventName, Runnable listener, int priority) throws Throwable {
        throw new BadMethodCallException("Unmodifiable event dispatchers must not be modified.");
    }

    @Override
    public void addSubscriber(EventSubscriberInterface subscriber) throws Throwable {
        throw new BadMethodCallException("Unmodifiable event dispatchers must not be modified.");
    }

    @Override
    public void removeListener(String eventName, Runnable listener) throws Throwable {
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
    public int getListenerPriority(String eventName, Runnable listener) {
        return this.dispatcher.getListenerPriority(eventName, listener);
    }

    @Override
    public boolean hasListeners(String eventName) {
        return this.dispatcher.hasListeners(eventName);
    }
}
