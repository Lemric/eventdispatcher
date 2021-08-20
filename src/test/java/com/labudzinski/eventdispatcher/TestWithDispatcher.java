package com.labudzinski.eventdispatcher;

import com.labudzinski.eventdispatcher.events.Dispatcher;

public class TestWithDispatcher {
    public String name;
    public Object dispatcher;
    public boolean invoked = false;

    public Dispatcher foo(Dispatcher event) {
        this.name = event.getName();
        this.dispatcher = event.getDispatcher();

        return event;
    }

    public Dispatcher onEvent(Dispatcher event) {
        this.name = event.getName();
        this.invoked = true;
        this.dispatcher = event.getDispatcher();

        return event;
    }
}
