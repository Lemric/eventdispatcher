/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.labudzinski.eventdispatcher;

import com.labudzinski.eventdispatcher.util.HashCode;
import com.labudzinski.eventdispatchercontracts.Event;
import com.labudzinski.eventdispatchercontracts.EventListenerInterface;

public abstract class Closure<V> implements EventListenerInterface {
    private Event event;

    @Override
    public int hashCode() {
        HashCode h = new HashCode();
        h.addValue(event);
        return h.hashCode();
    }

    public abstract Object call();

    public void setEvent(Object event) {
        this.event = (Event) event;
    }
}
