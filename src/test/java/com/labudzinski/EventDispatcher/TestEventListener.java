package com.labudzinski.EventDispatcher;

import java.lang.reflect.Method;
import java.util.UUID;

public class TestEventListener implements EventListenerImpl {
    public UUID uuid = UUID.randomUUID();
    public String name;
    public boolean preFooInvoked = false;
    public boolean postFooInvoked = false;

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
    /* Listener methods */

    public void preFoo(Event event) {
        this.preFooInvoked = true;
    }

    public void postFoo(Event event) {
        this.postFooInvoked = true;
        if (!this.preFooInvoked) {
            event.stopPropagation();
        }
    }

    public void __invoke() {
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !getClass().getName().equals(o.getClass().getName())) return false;
        for (Method method : this.getClass().getMethods()) {
            try {
                o.getClass().getMethod(method.getName());
            } catch (NoSuchMethodException e) {
                return false;
            }
        }

        return true;
    }
}

