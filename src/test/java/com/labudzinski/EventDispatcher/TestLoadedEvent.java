package com.labudzinski.EventDispatcher;

public class TestLoadedEvent extends Event {
    private final Boolean testLoaded;

    public TestLoadedEvent(Boolean testLoaded) {
        this.testLoaded = testLoaded;
    }

    public Boolean getTestLoaded() {
        return testLoaded;
    }
}
