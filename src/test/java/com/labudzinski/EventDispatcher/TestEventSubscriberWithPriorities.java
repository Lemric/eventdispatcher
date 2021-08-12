package com.labudzinski.EventDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestEventSubscriberWithPriorities implements EventSubscriberInterface {
    @Override
    public Map<String, ArrayList<ClosureRunnable>> getSubscribedEvents() {
        return new HashMap<>() {{
            put("pre.foo", new ArrayList<>() {{
                add(new ClosureRunnable(new Closure() {
                }, "preFoo", 10));
            }});
            put("post.foo", new ArrayList<>() {{
                add(new ClosureRunnable(new Closure() {
                }, "postFoo"));
            }});
        }};
    }
}
