package com.labudzinski.EventDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestEventSubscriberWithMultipleListeners implements EventSubscriberInterface {
    @Override
    public Map<String, ArrayList<ClosureRunnable>> getSubscribedEvents() {
        return new HashMap<>() {{
            put("pre.foo", new ArrayList<>() {{
                add(new ClosureRunnable(new Closure() {
                    public void preFoo1(Event event) {
                    }
                }, "preFoo1"));
                add(new ClosureRunnable(new Closure() {
                    public void preFoo2(Event event) {
                    }
                }, "preFoo2"));
            }});
        }};
    }
}
