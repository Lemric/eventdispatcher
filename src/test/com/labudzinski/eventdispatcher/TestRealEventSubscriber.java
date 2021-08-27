/*
 * Copyright (c) 2021-2021.
 *
 * This file is part of the com.labudzinski package.
 *  For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *  @author Dominik Labudzinski <dominik@labudzinski.com>
 */

package com.labudzinski.eventdispatcher;

import java.util.HashMap;
import java.util.Map;

public class TestRealEventSubscriber implements EventSubscriberInterface {
    public static Event onKernelResponsePre(Event event) {
        // ...
        return event;
    }

    public static Event onKernelResponsePost(Event event) {
        // ...
        return event;
    }

    public static Event onStoreOrder(Event event) {
        // ...
        return event;
    }

    @Override
    public Map<String, Map<EventListenerInterface<Event>, Integer>> getSubscribedEvents() {
        return new HashMap<>() {{
            put("pre.foo", new HashMap<>() {{
                put(TestRealEventSubscriber::onKernelResponsePre, 0);
                put(TestRealEventSubscriber::onKernelResponsePost, 0);
                put(TestRealEventSubscriber::onStoreOrder, -10);
            }});
        }};
    }
}
