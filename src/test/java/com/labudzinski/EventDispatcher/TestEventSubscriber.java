package com.labudzinski.EventDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestEventSubscriber implements EventSubscriberInterface {
    @Override
    public Map<String, List<EventListener<? extends Event>>> getSubscribedEvents() {
        TestEventListener listener = new TestEventListener();
        return new HashMap<String, List<EventListener<? extends Event>>>() {{
            put("pre.foo", new ArrayList<EventListener<? extends Event>>() {{
                add(listener);
            }});
            put("post.foo", new ArrayList<EventListener<? extends Event>>() {{
                add(listener);
            }});
        }};
    }
}
