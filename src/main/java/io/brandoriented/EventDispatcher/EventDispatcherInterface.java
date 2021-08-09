package io.brandoriented.EventDispatcher;

import io.brandoriented.EventDispatcher.exceptions.BadMethodCallException;

import java.util.HashMap;

public interface EventDispatcherInterface {

    /**
     * Adds an event listener that listens on the specified events.
     *
     * @param priority Integer The higher this value, the earlier an event
     *                      listener will be triggered in the chain (defaults to 0)
     */
    public void addListener(String eventName, HashMap<EventSubscriberInterface, Object> listener, Integer priority) throws BadMethodCallException, Throwable;

    /**
     * Adds an event subscriber.
     *
     * The subscriber is asked for all the events it is
     * interested in and added as a listener for these events.
     */
    public void addSubscriber(EventSubscriberInterface subscriber) throws Throwable;

    /**
     * Removes an event listener from the specified events.
     */
    public void removeListener(String eventName, Runnable listener) throws Throwable;

    public void removeSubscriber(EventSubscriberInterface subscriber) throws Throwable;

    /**
     * Gets the listeners of a specific event or all listeners sorted by descending priority.
     *
     * @return array The event listeners for the specified event, or all event listeners by event name
     */
    public Object getListeners(String eventName);

    /**
     * Gets the listener priority for a specific event.
     *
     * Returns null if the event or the listener does not exist.
     *
     * @return int|null The event listener priority
     */
    public Integer getListenerPriority(String eventName, Runnable listener);

    /**
     * Checks whether an event has any registered listeners.
     *
     * @return bool true if the specified event has any listeners, false otherwise
     */
    public boolean hasListeners(String eventName);

    Event dispatch(Event event, String eventName);
}
