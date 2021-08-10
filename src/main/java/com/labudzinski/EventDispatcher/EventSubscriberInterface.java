package com.labudzinski.EventDispatcher;

import java.util.ArrayList;
import java.util.Map;

public interface EventSubscriberInterface {
    /**
     * Returns an array of event names this subscriber wants to listen to.
     * <p>
     * The array keys are event names and the value can be:
     * <p>
     * * The method name to call (priority defaults to 0)
     * * An array composed of the method name to call and the priority
     * * An array of arrays composed of the method names to call and respective
     * priorities, or 0 if unset
     * <p>
     * For instance:
     * <p>
     * * ['eventName' => ['methodName', $priority]]
     * <p>
     * The code must not depend on runtime state as it will only be called at compile time.
     * All logic depending on runtime state must be put into the individual methods handling the events.
     *
     * @return array<string, mixed> The event names to listen to
     */
    Map<String, ArrayList<EventListenerInterface>> getSubscribedEvents();
}
