package com.labudzinski.EventDispatcher;

import java.lang.reflect.Method;
import java.util.UUID;

public class TestEventListener implements Closure {
    public boolean preFooInvoked = false;
    public boolean postFooInvoked = false;

    /* Listener methods */

    public void preFoo(Event event)
    {
        this.preFooInvoked = true;
    }

    public void postFoo(Event event)
    {
        this.postFooInvoked = true;

        if (!this.preFooInvoked) {
            event.stopPropagation();
        }
    }

    public void invoke()
    {
    }
}

