package com.labudzinski.eventdispatcher;

import com.labudzinski.eventdispatchercontracts.Event;
import com.labudzinski.eventdispatchercontracts.EventDispatcherInterface;
import com.labudzinski.eventdispatchercontracts.EventListenerInterface;

import java.util.*;
import java.util.Map.Entry;

public class EventDispatcher implements EventDispatcherInterface {

    private final HashMap<String, HashMap<Integer, List<EventListenerInterface>>> listeners = new HashMap<>();

    public <T extends Event> T dispatch(T event, String eventName) {
        List<EventListenerInterface> listeners = getListeners(eventName);
        if (listeners.size() > 0) {
            for (EventListenerInterface listener : listeners) {
                if (event.isPropagationStopped()) {
                    return event;
                }

                Object result = null;
                try {
                    result = listener.call(event);
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

    public EventDispatcher addListener(String eventName, EventListenerInterface listener) {
        return addListener(eventName, listener, 0);
    }

    public EventDispatcher addListener(String eventName, EventListenerInterface listener, Integer priority) {
        HashMap<Integer, List<EventListenerInterface>> currentListenersSet = listeners.get(eventName);

        if (currentListenersSet == null) {
            currentListenersSet = new HashMap<>();
        }

        if (!currentListenersSet.containsKey(priority)) {
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

    public List<EventListenerInterface> getListeners() {
        return getListeners(null);
    }

    public List<EventListenerInterface> getListeners(String eventName) {

        HashMap<Integer, List<EventListenerInterface>> currentListenersSet = new HashMap<>();
        if (eventName == null) {
            for (Entry<String, HashMap<Integer, List<EventListenerInterface>>> stringHashMapEntry : listeners.entrySet()) {
                for (Entry<Integer, List<EventListenerInterface>> integerListEntry : stringHashMapEntry.getValue().entrySet()) {
                    for (EventListenerInterface callable : integerListEntry.getValue()) {
                        if (!currentListenersSet.containsKey(integerListEntry.getKey())) {
                            currentListenersSet.put(integerListEntry.getKey(), new ArrayList<>());
                        }
                        currentListenersSet.get(integerListEntry.getKey()).add(callable);
                    }
                }
            }
        } else {
            currentListenersSet = listeners.get(eventName);
        }
        if (currentListenersSet == null) {
            return new ArrayList<>();
        }
        LinkedHashMap<Integer, List<EventListenerInterface>> reverseSortedMap = new LinkedHashMap<>();
        currentListenersSet.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        ArrayList<EventListenerInterface> result = new ArrayList<>();
        for (Entry<Integer, List<EventListenerInterface>> integerListEntry : reverseSortedMap.entrySet()) {
            result.addAll(integerListEntry.getValue());
        }
        return result;
    }

    public Integer getListenerPriority(String eventName, EventListenerInterface listener) {
        HashMap<Integer, List<EventListenerInterface>> currentListenersSet = listeners.get(eventName);

        if (currentListenersSet == null) {
            return null;
        }

        for (Entry<Integer, List<EventListenerInterface>> integerListEntry : currentListenersSet.entrySet()) {
            Integer priority = integerListEntry.getKey();

            for (EventListenerInterface callable : integerListEntry.getValue()) {
                if (listener == callable) {
                    return priority;
                }
            }
        }

        return null;
    }

    public void removeListener(String eventName, EventListenerInterface listener) {
        HashMap<Integer, List<EventListenerInterface>> currentListenersSet = this.listeners.get(eventName);

        if (currentListenersSet == null) {
            return;
        }
        for (Iterator<Entry<Integer, List<EventListenerInterface>>> integerListEntry = currentListenersSet.entrySet().iterator(); integerListEntry.hasNext(); ) {
            Entry<Integer, List<EventListenerInterface>> entity = integerListEntry.next();
            entity.getValue().removeIf(callable -> listener == callable);
            if (entity.getValue().isEmpty()) {
                integerListEntry.remove();
            }
        }

        if (currentListenersSet.isEmpty()) {
            this.listeners.remove(eventName);
        } else {
            this.listeners.put(eventName, currentListenersSet);
        }
    }

}
