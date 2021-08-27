/*
 * Copyright (c) 2021-2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.labudzinski.eventdispatcher;

import java.util.Map;

@FunctionalInterface
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
     * * ["eventName" => [[EventListenerInterface, priority]]]
     * <p>
     * The code must not depend on runtime state as it will only be called at compile time.
     * All logic depending on runtime state must be put into the individual methods handling the events.
     *
     * @return array<string, mixed> The event names to listen to
     */
    Map<String, Map<EventListenerInterface<?>, Integer>> getSubscribedEvents();
}