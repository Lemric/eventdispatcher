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

public class TestEventSubscriberWithMultipleListeners implements EventSubscriberInterface {
    @Override
    public Map<String, Map<EventListenerInterface<?>, Integer>> getSubscribedEvents() {
        return new HashMap<>() {{
            put("pre.foo", new HashMap<>() {{
                put((event) -> null, 0);
                put((event) -> null, 10);
            }});
        }};
    }
}
