/*
 * Copyright (c) 2021.
 * This file is part of the com.labudzinski package.
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.labudzinski.eventdispatcher;

import com.labudzinski.eventdispatcher.events.Dispatcher;

public class TestWithDispatcher {
    public String name;
    public Object dispatcher;
    public boolean invoked = false;

    public Dispatcher foo(Dispatcher event) {
        this.name = event.getName();
        this.dispatcher = event.getDispatcher();

        return event;
    }

    public Dispatcher onEvent(Dispatcher event) {
        this.name = event.getName();
        this.invoked = true;
        this.dispatcher = event.getDispatcher();

        return event;
    }
}
