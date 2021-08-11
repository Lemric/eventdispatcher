package com.labudzinski.EventDispatcher;

import java.util.UUID;

public interface EventListenerImpl {
    UUID getUuid();

    String getName();

    boolean equals(Object o);
}
