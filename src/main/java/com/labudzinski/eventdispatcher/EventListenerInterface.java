/*
 * Copyright (c) 2021.
 * This file is part of the com.labudzinski package.
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.labudzinski.eventdispatcher;

/**
 * A task that returns a result and may throw an exception. Implementors define a single method with no arguments called call.
 * The Callable interface is similar to Runnable, in that both are designed for classes whose instances are potentially executed by another thread. A Runnable, however, does not return a result and cannot throw a checked exception.
 * The Executors class contains utility methods to convert from other common forms to Callable classes.
 * Since:
 * 1.5
 * See Also:
 * Executor
 * Author:
 * Doug Lea
 * Type parameters:
 * <V> – the result type of method call
 *
 * @param <V>
 */
@FunctionalInterface
public interface EventListenerInterface<V> {
    static <V> V callable(EventListenerInterface<V> cf, V arg) throws Exception {
        return cf.call(arg);
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     * Returns:
     * computed result
     * Throws:
     * Exception – if unable to compute a result
     *
     * @return V
     */
    V call(V arg) throws Exception;
}