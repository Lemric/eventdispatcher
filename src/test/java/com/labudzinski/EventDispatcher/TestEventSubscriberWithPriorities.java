package com.labudzinski.EventDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestEventSubscriberWithPriorities implements EventSubscriberInterface {
    @Override
    public Map<String, ArrayList<ClosureRunnable>> getSubscribedEvents() {
        return new HashMap<String, ArrayList<ClosureRunnable>>() {{
            put("pre.foo", new ArrayList<ClosureRunnable>() {{
                add(new ClosureRunnable(new Closure() {
                    public void preFoo(Event event) {
                    }
                }, "preFoo", 10));
            }});
            put("post.foo", new ArrayList<ClosureRunnable>() {{
                add(new ClosureRunnable(new Closure() {
                    public void postFoo(Event event) {
                    }
                }, "postFoo"));
            }});
        }};
    }
}
