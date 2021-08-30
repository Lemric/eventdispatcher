/*
 * Copyright (c) 2021.
 * This file is part of the com.labudzinski package.
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.lemric.eventdispatcher;

public class TestEventListener {
    public boolean preFooInvoked = false;
    public boolean postFooInvoked = false;

    public Event preFoo(Event event) {
        preFooInvoked = true;

        return event;
    }

    public Event preFoo() {
        preFooInvoked = true;

        return null;
    }

    public Event postFoo(Event event) {
        postFooInvoked = true;
        if (!preFooInvoked) {
            event.stopPropagation();
        }

        return event;
    }

    protected Event onEvent() {
        return null;
    }

    public Object onEvent(Object o) {
        return null;
    }

    public Object preFoo(Object o) {
        return null;
    }
}

