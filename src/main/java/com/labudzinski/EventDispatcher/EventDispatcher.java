package com.labudzinski.EventDispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventDispatcher implements EventDispatcherInterface {

    private static volatile EventDispatcher instance;
    private HashMap<String, List<EventListener<? extends Event>>> listeners =  new HashMap<>();

    public static EventDispatcher getInstance() {
        if (instance == null) {
            synchronized (EventDispatcher.class) {
                if (instance == null) {
                    instance = new EventDispatcher();
                }
            }
        }
        return instance;
    }

    public void destroy() {
        listeners = null;
        instance = null;
    }

    @Override
    public <T extends Event> T dispatch(T event, String eventName) {

        List<EventListener<? extends Event>> listeners = getListeners(eventName);

        if (listeners.size() > 0) {

            for (EventListener listener : listeners) {
                if (event.isPropagationStopped())
                    break;

                listener.invoke(event);
            }
        }

        return event;
    }

    @Override
    public List<EventListener<? extends Event>> getListeners() {
        return getListeners(null);
    }

    @Override
    public List<EventListener<? extends Event>> getListeners(String eventName) {

        if (eventName != null) {

            List<EventListener<? extends Event>> currentListenersSet = listeners.get(eventName);

            if (currentListenersSet == null) {
                return new ArrayList<>();
            }

            currentListenersSet.sort(Comparator.<EventListener<? extends Event>>comparingInt(EventListener::getPriority).reversed());
            return currentListenersSet;
        }

        List<EventListener<? extends Event>> result = new CopyOnWriteArrayList<>();
        for (Entry<String, List<EventListener<? extends Event>>> stringListEntry : listeners.entrySet()) {
            List<EventListener<? extends Event>> currentResult = new CopyOnWriteArrayList<>(stringListEntry.getValue());
            currentResult.sort(Comparator.<EventListener<? extends Event>>comparingInt(EventListener::getPriority).reversed());
            result.addAll(currentResult);
        }

        return result;
    }

    @Override
    public Integer getListenerPriority(String eventName, EventListener<? extends Event> listener) {
        List<EventListener<? extends Event>> currentListenersSet = listeners.get(eventName);

        if (currentListenersSet == null) {
            return null;
        }
        for (EventListener<? extends Event> eventListener : currentListenersSet) {
            if(eventListener.equals(listener)) {
                return eventListener.getPriority();
            }
        }

        return null;
    }

    @Override
    public boolean hasListeners(String eventName) {
        return listeners.get(eventName) != null && listeners.get(eventName).size() > 0;
    }

    @Override
    public boolean hasListeners() {
        return listeners.size() > 0;
    }

    @Override
    public EventDispatcher addListener(String eventName, EventListener<? extends Event> listener) {
        return addListener(eventName, listener, null);
    }

    @Override
    public EventDispatcher addListener(String eventName, EventListener<? extends Event> listener, Integer priority) {

        if (priority != null) {
            listener.setPriority(priority);
        }

        List<EventListener<? extends Event>> currentListenersSet = listeners.get(eventName);

        if (currentListenersSet == null) {
            currentListenersSet = new CopyOnWriteArrayList<>();
        }

        currentListenersSet.add(listener);

        listeners.put(eventName, currentListenersSet);

        return this;
    }

    @Override
    public void addSubscriber(EventSubscriberInterface subscriber) {
        for (Entry<String, List<EventListener<? extends Event>>> entry : subscriber.getSubscribedEvents().entrySet()) {
            String eventName = entry.getKey();
            List<EventListener<? extends Event>> calls = entry.getValue();
            for (EventListener<? extends Event> call : calls) {
                this.addListener(eventName, call, call.getPriority());
            }
        }
    }

    @Override
    public EventDispatcher removeListener(String eventName, EventListener<? extends Event> listener) {

        List<EventListener<? extends Event>> currentListenersSet = listeners.get(eventName);

        if (currentListenersSet == null) {
            return this;
        }

        for (EventListener<? extends Event> eventListener : currentListenersSet) {
            System.out.println("eventListener.equals(listener):" + eventListener.equals(listener));
            System.out.println(listener.hashCode());
            System.out.println(eventListener.hashCode());
            if(eventListener.equals(listener)) {
                currentListenersSet.remove(listener);
                System.out.println("REMOVED");
            }
        }

        if (currentListenersSet.size() == 0) {
            listeners.remove(eventName);
        } else {
            listeners.put(eventName, currentListenersSet);
        }

        return this;
    }

    @Override
    public void removeSubscriber(EventSubscriberInterface subscriber) {
        for (Entry<String, List<EventListener<? extends Event>>> entry : subscriber.getSubscribedEvents().entrySet()) {
            String eventName = entry.getKey();
            List<EventListener<? extends Event>> calls = entry.getValue();
            for (EventListener<? extends Event> call : calls) {
                System.out.println("Remove");
                this.removeListener(eventName, call);
            }
        }
    }
}
