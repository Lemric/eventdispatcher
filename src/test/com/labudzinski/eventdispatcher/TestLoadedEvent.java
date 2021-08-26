package com.labudzinski.eventdispatcher;

import com.labudzinski.eventdispatchercontracts.Event;

public class TestLoadedEvent extends Event {
    private final Boolean testLoaded;

    public TestLoadedEvent(Boolean testLoaded) {
        this.testLoaded = testLoaded;
    }

    public Boolean getTestLoaded() {
        return testLoaded;
    }
}
