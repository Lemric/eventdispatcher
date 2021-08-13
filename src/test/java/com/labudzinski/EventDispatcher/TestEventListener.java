package com.labudzinski.EventDispatcher;

public class TestEventListener implements ClosureInterface {
    public boolean preFooInvoked = false;
    public boolean postFooInvoked = false;

    /* Listener methods */

    public void preFoo(Event event) {
        preFooInvoked = true;
    }

    public void postFoo(Event event)
    {
        postFooInvoked = true;

        if (!preFooInvoked) {
            event.stopPropagation();
        }
    }

    public void invoke()
    {
    }
}

