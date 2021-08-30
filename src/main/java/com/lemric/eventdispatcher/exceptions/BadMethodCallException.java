/*
 * Copyright (c) 2021.
 * This file is part of the com.labudzinski package.
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.lemric.eventdispatcher.exceptions;

public class BadMethodCallException extends Exception {
    public BadMethodCallException(String message) {
        super(message);
    }
}
