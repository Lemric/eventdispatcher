package com.labudzinski.EventDispatcher;

public class EventListener implements EventListenerInterface {

    private final EventListenerImpl listener;
    private final String method;

    public EventListener(EventListenerImpl listener) {
        this.listener = listener;
        this.method = "__invoke";
    }

    public EventListener(EventListenerImpl listener, String method) {
        this.listener = listener;
        this.method = method;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public EventListenerImpl getListener() {
        return this.listener;
    }

    @Override
    public Object getParameters() {
        return null;
    }
}
