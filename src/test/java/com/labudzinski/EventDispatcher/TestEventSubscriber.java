package com.labudzinski.EventDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestEventSubscriber implements EventSubscriberInterface {
    @Override
    public Map<String, ArrayList<ClosureRunnable>> getSubscribedEvents() {
        return new HashMap<>() {{
            put("pre.foo", new ArrayList<>() {{
                add(new ClosureRunnable(new Closure() {
                    public void preFoo(Event event) {
                    }
                }, "preFoo"));
            }});
             put("post.foo", new ArrayList<>() {{
                add(new ClosureRunnable(new Closure() {
                    public void postFoo(Event event) {
                    }
                }, "postFoo"));
            }});
        }};
    }
}
