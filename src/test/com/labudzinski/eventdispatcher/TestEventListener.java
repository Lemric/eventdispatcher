package com.labudzinski.eventdispatcher;

public class TestEventListener  {
    public boolean preFooInvoked = false;
    public boolean postFooInvoked = false;

    public Event preFoo(Event event) {
        preFooInvoked = true;

        return event;
    }

    public Event preFoo() {
        preFooInvoked = true;

        return null;
    }

    public Event postFoo(Event event)
    {
        postFooInvoked = true;
        if (!preFooInvoked) {
            event.stopPropagation();
        }

        return event;
    }

    protected Event onEvent() {
        return null;
    }

    public Object onEvent(Object o) {
        return null;
    }

    public Object preFoo(Object o) {
        return null;
    }
}

