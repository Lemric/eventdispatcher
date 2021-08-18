package com.labudzinski.EventDispatcher;

import com.labudzinski.EventDispatcher.util.HashCode;

import java.util.function.Consumer;

public class TestEventListener extends EventListener<Event> {
    public boolean preFooInvoked = false;
    public boolean postFooInvoked = false;

    /* Listener methods */

    public Consumer<Event> preFoo() {
        return (event) -> preFooInvoked = true;
    }

    public Consumer<Event> postFoo()
    {
        return (event) -> {
            postFooInvoked = true;

            if (!preFooInvoked) {
                event.stopPropagation();
            }
        };
    }

    @Override
    protected Consumer<Event> onEvent() {
        return null;
    }

}

