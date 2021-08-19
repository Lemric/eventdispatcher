package com.labudzinski.EventDispatcher;

import com.labudzinski.EventDispatcher.util.HashCode;

import java.util.function.Consumer;

public class TestEventListener  {
    public boolean preFooInvoked = false;
    public boolean postFooInvoked = false;

    /* Listener methods */

    public Event preFoo(Event event) {
        System.out.println("PreFoo");
        preFooInvoked = true;

        return null;
    }

    public Event preFoo() {
        System.out.println("PreFoo");
        preFooInvoked = true;

        return null;
    }

    public Event postFoo(Event event)
    {
        System.out.println("PostFoo");
        postFooInvoked = true;
        System.out.println(preFooInvoked);
        if (!preFooInvoked) {
            event.stopPropagation();
        }

        return event;
    }

    protected Event onEvent() {
        return null;
    }

}

