package com.labudzinski.EventDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestEventSubscriberWithMultipleListeners implements EventSubscriberInterface {
    @Override
    public Map<String, List<EventListener<? extends Event>>> getSubscribedEvents() {
        return new HashMap<String, List<EventListener<? extends Event>>>() {{
            put("pre.foo", new ArrayList<EventListener<? extends Event>>() {{
                add(new EventListener<>((event) -> {}));
                add(new EventListener<>((event) -> {}));
            }});

        }};
    }
}
