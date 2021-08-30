/*
 * Copyright (c) 2021-2021.
 *
 * This file is part of the com.labudzinski package.
 *  For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *  @author Dominik Labudzinski <dominik@labudzinski.com>
 */

package com.lemric.eventdispatcher;

import java.util.HashMap;
import java.util.Map;

public class TestEventSubscriber implements EventSubscriberInterface {
    @Override
    public Map<String, Map<EventListenerInterface<Event>, Integer>> getSubscribedEvents() {
        return new HashMap<>() {{
            put("pre.foo", new HashMap<>() {{
                put((event) -> null, 0);
            }});
            put("post.foo", new HashMap<>() {{
                put((event) -> null, 0);
            }});
        }};
    }
}
