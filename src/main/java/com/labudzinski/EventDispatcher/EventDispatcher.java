package com.labudzinski.EventDispatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventDispatcher implements EventDispatcherInterface {

    private HashMap<String, HashMap<Integer, ArrayList<EventListenerInterface>>> listeners;
    private HashMap<String, Object> sorted;
    private HashMap<String, Object> optimized;


    @Override
    public Event dispatch(Event event, String eventName) {
        if (eventName == null) {
            eventName = event.getClass().toString();
        }

        HashMap<Integer, ArrayList<EventListenerInterface>> listeners = this.getListeners(eventName);
        if (!listeners.isEmpty()) {
            this.callListeners(listeners, eventName, event);
        }

        return event;
    }

    @Override
    public HashMap<Integer, ArrayList<EventListenerInterface>> getListeners(String eventName) {
        if (!this.listeners.containsKey(eventName)) {
            return new HashMap<>();
        }

        return this.listeners.get(eventName);
    }

    @Override
    public Integer getListenerPriority(String eventName, EventListenerInterface listener) {

        if (!this.listeners.containsKey(eventName)) {
            return listener.getPriority();
        }
        return 1;
    }

    @Override
    public boolean hasListeners(String eventName) {
        if (null != eventName) {
            return this.listeners.containsKey(eventName);
        }

        for (Map.Entry<String, HashMap<Integer, ArrayList<EventListenerInterface>>> entry : this.listeners.entrySet()) {
            HashMap<Integer, ArrayList<EventListenerInterface>> eventListeners = entry.getValue();
            if (!eventListeners.isEmpty()) {
                return true;
            }

        }
        return false;
    }

    public void addListener(String eventName, EventListenerInterface listener) {

        if (!this.listeners.containsKey(eventName)) {
            this.listeners.put(eventName, new HashMap<>());
        }

        Integer priority = listener.getPriority();

        HashMap<Integer, ArrayList<EventListenerInterface>> currentListenersByEventName = this.listeners.get(eventName);
        ArrayList<EventListenerInterface> currentListeners = currentListenersByEventName.get(priority);
        if (currentListeners != null) {
            currentListeners.add(listener);
        } else {
            currentListenersByEventName.put(priority, new ArrayList<EventListenerInterface>() {{
                add(listener);
            }});
        }
    }

    @Override
    public void removeListener(String eventName, EventListenerInterface listener) throws Throwable {
        if (!this.listeners.containsKey(eventName)) {
            return;
        }

        for (Map.Entry<Integer, ArrayList<EventListenerInterface>> entry : this.listeners.get(eventName).entrySet()) {
            Integer priority = entry.getKey();
            ArrayList<EventListenerInterface> listeners = entry.getValue();

            for (EventListenerInterface v : listeners) {
                Integer k = listeners.indexOf(v);
                if (v == listener) {
                    listeners.remove(v);
                }
            }

            if(listeners.isEmpty()) {
                HashMap<Integer, ArrayList<EventListenerInterface>> currentListeners = this.listeners.get(eventName);
                currentListeners.remove(priority);
                this.listeners.put(eventName, currentListeners);
            }
        }
    }

    @Override
    public void addSubscriber(EventSubscriberInterface subscriber) throws Throwable {

        for (Map.Entry<String, ArrayList<EventListenerInterface>> entry : subscriber.getSubscribedEvents().entrySet()) {
            String eventName = entry.getKey();
            ArrayList<EventListenerInterface> calls = entry.getValue();
            for (EventListenerInterface call : calls) {
                this.addListener(eventName, call);
            }
        }
    }

    @Override
    public void removeSubscriber(EventSubscriberInterface subscriber) throws Throwable {

    }

    protected void callListeners(HashMap<Integer, ArrayList<EventListenerInterface>> listeners, String eventName, Event event) {
        boolean stoppable = event != null;

        for (Map.Entry<Integer, ArrayList<EventListenerInterface>> stringObjectEntry : listeners.entrySet()) {
            if (stoppable && (event).isPropagationStopped()) {
                break;
            }

            for (EventListenerInterface eventListener : stringObjectEntry.getValue()) {
                try {
                    stringObjectEntry.getClass().getMethod(eventListener.getMethod()).invoke(event, eventName, eventListener);
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
