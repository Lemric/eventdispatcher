/*
 * Copyright (c) 2021-2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.labudzinski.eventdispatcher;

import java.util.HashMap;
import java.util.Map;

public class TestEventSubscriberWithPriorities implements EventSubscriberInterface {
    @Override
    public Map<String, Map<EventListenerInterface<?>, Integer>> getSubscribedEvents() {
        return new HashMap<>() {{
            put("pre.foo", new HashMap<>() {{
                put((event) -> null, 10);
            }});
            put("post.foo", new HashMap<>() {{
                put((event) -> null, 0);
            }});
        }};
    }
}
