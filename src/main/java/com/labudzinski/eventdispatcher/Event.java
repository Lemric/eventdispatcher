/*
 * Copyright (c) 2021.
 * This file is part of the com.labudzinski package.
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.labudzinski.eventdispatcher;

public class Event implements StoppableEventInterface {

    private boolean propagationStopped = false;

    @Override
    public boolean isPropagationStopped() {
        return this.propagationStopped;
    }

    @Override

    public void stopPropagation() {
        this.propagationStopped = true;
    }
}