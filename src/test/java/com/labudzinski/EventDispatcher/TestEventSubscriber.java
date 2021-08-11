package com.labudzinski.EventDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestEventSubscriber implements EventSubscriberInterface {
    @Override
    public Map<String, ArrayList<EventListenerInterface>> getSubscribedEvents() {
        return new HashMap<>() {{
            put("pre.foo", new ArrayList<>() {{
                add(new EventListener(new EventListenerImpl() {
                    @Override
                    public UUID getUuid() {
                        return UUID.randomUUID();
                    }

                    @Override
                    public String getName() {
                        return null;
                    }
                }, "preFoo"));
            }});
            put("post.foo", new ArrayList<>() {{
                add(new EventListener(new EventListenerImpl() {
                    @Override
                    public UUID getUuid() {
                        return UUID.randomUUID();
                    }

                    @Override
                    public String getName() {
                        return null;
                    }
                }, "postFoo"));
            }});
        }};
    }
}
