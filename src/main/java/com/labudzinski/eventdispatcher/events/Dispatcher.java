package com.labudzinski.eventdispatcher.events;

import com.labudzinski.eventdispatchercontracts.Event;
import com.labudzinski.eventdispatchercontracts.EventDispatcherInterface;

public class Dispatcher extends Event {
    private String name;
    private EventDispatcherInterface dispatcher;
    private Boolean invoked = false;

    public Dispatcher() {
        this.name = null;
        this.dispatcher = null;
    }

    public Dispatcher(Boolean invoked) {
        this.name = null;
        this.dispatcher = null;
        this.invoked = invoked;
    }

    public Dispatcher(String name, EventDispatcherInterface dispatcher) {
        this.name = name;
        this.dispatcher = dispatcher;
    }

    public Dispatcher(Boolean invoked, String name) {
        this.name = name;
        this.dispatcher = null;
        this.invoked = invoked;
    }

    public Dispatcher(Boolean invoked, String name, EventDispatcherInterface dispatcher) {
        this.name = name;
        this.dispatcher = dispatcher;
        this.invoked = invoked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventDispatcherInterface getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(EventDispatcherInterface dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Boolean getInvoked() {
        return invoked;
    }
}
