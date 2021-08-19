/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.labudzinski.EventDispatcher;

import com.labudzinski.EventDispatcher.events.Dispatcher;
import com.labudzinski.EventDispatcher.util.HashCode;

import java.util.concurrent.Callable;

public abstract class Closure<V> implements Callable {

    private int priority;
    private Event event;

    @Override
    public int hashCode() {
        HashCode h = new HashCode();
        h.addValue(event);
        return h.hashCode();
    }

    public abstract Object call();

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
