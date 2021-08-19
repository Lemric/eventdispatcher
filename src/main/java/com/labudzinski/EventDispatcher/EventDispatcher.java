package com.labudzinski.EventDispatcher;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labudzinski.EventDispatcher.util.HashCode;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

public class EventDispatcher implements EventDispatcherInterface {

    private HashMap<String, HashMap<Integer, List<Callable>>> listeners =  new HashMap<>();

    public <T extends Event> T dispatch(T event, String eventName) {

        List<Callable> listeners = getListeners(eventName);

        if (listeners.size() > 0) {

            for (Callable listener : listeners) {
                if (event.isPropagationStopped()) {
                    return event;
                }
                Object result = null;
                if(listener instanceof Closure) {
                    ((Closure) listener).setEvent(event);
                }
                try {
                    result = listener.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(!(result instanceof Event)) {
                    try {
                        if(result != null) {
                            result.getClass().getMethod("onEvent", event.getClass()).invoke(result, event);
                        }
                    } catch (Exception ignored) {
                    }
                } else {
                    event = (T) result;
                }
            }
        }

        return event;
    }

    public EventDispatcher addListener(String eventName, Callable listener) {
        return addListener(eventName, listener, 0);
    }
    public EventDispatcher addListener(String eventName, Callable listener, Integer priority) {
        HashMap<Integer, List<Callable>> currentListenersSet = listeners.get(eventName);

        if (currentListenersSet == null) {
            currentListenersSet = new HashMap<>();
        }

        if(!currentListenersSet.containsKey(priority)) {
            currentListenersSet.put(priority, new ArrayList<>());
        }

        currentListenersSet.get(priority).add(listener);

        listeners.put(eventName, currentListenersSet);

        return this;
    }

    public boolean hasListeners(String eventName) {
        return listeners.get(eventName) != null && listeners.get(eventName).size() > 0;
    }

    public boolean hasListeners() {
        return listeners.size() > 0;
    }

    public List<Callable> getListeners() {
        return getListeners(null);
    }

    public List<Callable> getListeners(String eventName) {

        HashMap<Integer, List<Callable>> currentListenersSet = new HashMap<>();
        if(eventName == null) {
            for (Entry<String, HashMap<Integer, List<Callable>>> stringHashMapEntry : listeners.entrySet()) {
                for (Entry<Integer, List<Callable>> integerListEntry : stringHashMapEntry.getValue().entrySet()) {
                    for (Callable callable : integerListEntry.getValue()) {
                        if(!currentListenersSet.containsKey(integerListEntry.getKey())) {
                            currentListenersSet.put(integerListEntry.getKey(), new ArrayList<>());
                        }
                        currentListenersSet.get(integerListEntry.getKey()).add(callable);
                    }
                }
            }
        } else {
            currentListenersSet = listeners.get(eventName);
        }
        if(currentListenersSet == null) {
            return new ArrayList<>();
        }
        LinkedHashMap<Integer, List<Callable>> reverseSortedMap = new LinkedHashMap<>();
        currentListenersSet.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        ArrayList<Callable> result = new ArrayList<>();
        for (Entry<Integer, List<Callable>> integerListEntry : reverseSortedMap.entrySet()) {
            result.addAll(integerListEntry.getValue());
        }
        return result;
    }

    public Integer getListenerPriority(String eventName, Callable listener) {
        HashMap<Integer, List<Callable>> currentListenersSet = listeners.get(eventName);

        if (currentListenersSet == null) {
            return null;
        }

        for (Entry<Integer, List<Callable>> integerListEntry : currentListenersSet.entrySet()) {
            Integer priority = integerListEntry.getKey();

            for (Callable callable : integerListEntry.getValue()) {
                if(listener == callable) {
                    return priority;
                }
            }
        }

        return null;
    }

    public void removeListener(String eventName, Callable listener) {
        HashMap<Integer, List<Callable>> currentListenersSet = this.listeners.get(eventName);

        if (currentListenersSet == null) {
            return;
        }
        for (Iterator<Entry<Integer, List<Callable>>> integerListEntry = currentListenersSet.entrySet().iterator(); integerListEntry.hasNext();) {
            Entry<Integer, List<Callable>> entity = integerListEntry.next();
            entity.getValue().removeIf(callable -> listener == callable);
            if(entity.getValue().isEmpty()) {
                integerListEntry.remove();
            }
        }

        if(currentListenersSet.isEmpty()) {
            this.listeners.remove(eventName);
        } else {
            this.listeners.put(eventName, currentListenersSet);
        }
    }

}
