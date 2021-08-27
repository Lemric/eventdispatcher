/*
 * Copyright (c) 2021.
 * This file is part of the com.labudzinski package.
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.labudzinski.eventdispatcher;

public interface EventDispatcherInterface {
    /**
     * Dispatches an event to all registered listeners.
     *
     * @param event     T      The event to pass to the event handlers/listeners
     * @param eventName String The name of the event to dispatch. If not supplied, the class of $event should be used instead.
     * @return <T extends Event> The passed event MUST be returned
     */
    <T extends Event> T dispatch(T event, String eventName);
}