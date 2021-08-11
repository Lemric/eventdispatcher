package com.labudzinski.EventDispatcher;

import java.util.UUID;

public class TestWithDispatcher implements EventListenerImpl {
    public String name;
    public Object dispatcher;
    public boolean invoked = false;

    public void foo(Object e, String name, Object dispatcher) {
        this.name = name;
        this.dispatcher = dispatcher;
    }

    @Override
    public UUID getUuid() {
        return UUID.randomUUID();
    }

    @Override
    public String getName() {
        return null;
    }
}
