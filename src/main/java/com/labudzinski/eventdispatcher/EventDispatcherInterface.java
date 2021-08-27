/*
 * Copyright (c) 2021.
 * This file is part of the com.labudzinski package.
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.labudzinski.eventdispatcher;

import java.util.List;

public interface EventDispatcherInterface {
    <T extends Event> T dispatch(T event, String eventName);

    EventDispatcherInterface addListener(String eventName, EventListenerInterface listener);

    EventDispatcherInterface addListener(String eventName, EventListenerInterface listener, Integer priority);

    boolean hasListeners();

    boolean hasListeners(String eventName);

    List<EventListenerInterface> getListeners();

    List<EventListenerInterface> getListeners(String eventName);

    Integer getListenerPriority(String eventName, EventListenerInterface listener);

    void removeListener(String eventName, EventListenerInterface listener);
}