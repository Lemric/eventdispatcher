package com.labudzinski.EventDispatcher;

import com.labudzinski.EventDispatcher.events.Dispatcher;

public class TestWithDispatcher extends Closure {
    public String name;
    public Object dispatcher;
    public boolean invoked = false;

    public void foo(Dispatcher event) {
        System.out.println("Foo");
        this.name = event.getName();
        this.dispatcher = event.getDispatcher();
    }

    public void invoke(Dispatcher event) {
        System.out.println("Invoke");
        this.name = event.getName();
        this.invoked = true;
        this.dispatcher = event.getDispatcher();
    }
}
