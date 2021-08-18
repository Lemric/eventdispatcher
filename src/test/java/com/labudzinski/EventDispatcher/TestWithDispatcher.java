package com.labudzinski.EventDispatcher;

import com.labudzinski.EventDispatcher.events.Dispatcher;

import java.util.function.Consumer;

public class TestWithDispatcher extends EventListener<Event> {
    public String name;
    public Object dispatcher;
    public boolean invoked = false;

    public Consumer<Dispatcher> foo() {
        return (event) -> {
            System.out.println("Foo");
            this.name = event.getName();
            this.dispatcher = event.getDispatcher();
        };
    }

    public Consumer<Dispatcher>  invoke() {
        return (event) -> {
            System.out.println("Invoke");
            this.name = event.getName();
            this.invoked = true;
            this.dispatcher = event.getDispatcher();
        };
    }
}
