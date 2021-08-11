package com.labudzinski.EventDispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventDispatcher implements EventDispatcherInterface {

    protected final Logger log = Logger.getLogger(getClass().getName());
    private final HashMap<String, HashMap<Integer, ArrayList<EventListenerInterface>>> listeners = new HashMap<>();

    @Override
    public Event dispatch(Event event, String eventName) {
        if (eventName == null) {
            eventName = event.getClass().toString();
        }

        ArrayList<ArrayList<EventListenerInterface>> listeners = this.getListeners(eventName);
        if (!listeners.isEmpty()) {
            this.callListeners(listeners, eventName, event);
        }

        return event;
    }


    @Override
    public ArrayList<ArrayList<EventListenerInterface>> getListeners(String eventName) {
        ArrayList<ArrayList<EventListenerInterface>> listenersArray = new ArrayList<>();

        if (!this.listeners.containsKey(eventName)) {
            return new ArrayList<>();
        }
        SortedSet<Integer> keys = new TreeSet<>(this.listeners.get(eventName).keySet());
        for (Integer key : keys) {
            ArrayList<EventListenerInterface> value = this.listeners.get(eventName).get(key);
            if (value.size() > 0) {
                listenersArray.add(value);
            }
        }

        return listenersArray;
    }

    public ArrayList<ArrayList<EventListenerInterface>> getListeners() {
        ArrayList<ArrayList<EventListenerInterface>> listenersArray = new ArrayList<>();
        for (Map.Entry<String, HashMap<Integer, ArrayList<EventListenerInterface>>> entry : this.listeners.entrySet()) {
            HashMap<Integer, ArrayList<EventListenerInterface>> eventListeners = entry.getValue();
            ArrayList<EventListenerInterface> currentEventNamedListener = new ArrayList<>();
            TreeMap<Integer, ArrayList<EventListenerInterface>> sorted = new TreeMap<>(eventListeners);
            for (Map.Entry<Integer, ArrayList<EventListenerInterface>> integerObjectEntry : sorted.entrySet()) {
                if (integerObjectEntry.getValue().size() > 0) {
                    currentEventNamedListener.addAll(integerObjectEntry.getValue());
                }
            }
            if (currentEventNamedListener.size() > 0) {
                listenersArray.add(currentEventNamedListener);
            }
        }
        return listenersArray;
    }

    public ArrayList<String> getListenersAsArrayList() {
        ArrayList<String> expectedList = new ArrayList<>();
        for (ArrayList<EventListenerInterface> eventListenerInterfaces : this.getListeners()) {
            for (EventListenerInterface eventListenerInterface : eventListenerInterfaces) {
                expectedList.add(eventListenerInterface.getListener().getUuid().toString());
            }
        }

        return expectedList;
    }

    public ArrayList<String> getListenersAsArrayList(String eventName) {
        ArrayList<String> expectedList = new ArrayList<>();
        for (ArrayList<EventListenerInterface> eventListenerInterfaces : this.getListeners(eventName)) {
            for (EventListenerInterface eventListenerInterface : eventListenerInterfaces) {
                expectedList.add(eventListenerInterface.getListener().getUuid().toString());
            }
        }

        return expectedList;
    }

    @Override
    public Integer getListenerPriority(String eventName, Object listener) {
        if (!this.listeners.containsKey(eventName) || this.listeners.get(eventName).isEmpty()) {
            return null;
        }
        for (Map.Entry<Integer, ArrayList<EventListenerInterface>> integerArrayListEntry : this.listeners.get(eventName).entrySet()) {
            Integer priority = integerArrayListEntry.getKey();
            ArrayList<EventListenerInterface> listeners = integerArrayListEntry.getValue();
            for (EventListenerInterface object : listeners) {
                if (object.getListener().equals(listener)) {
                    return priority;
                }
            }
        }
        return null;
    }

    public boolean hasListeners() {
        return this.hasListeners(null);
    }

    @Override
    public boolean hasListeners(String eventName) {
        if (null != eventName) {
            return this.listeners.containsKey(eventName) && this.getListeners(eventName).size() > 0;
        }

        for (ArrayList<EventListenerInterface> eventListeners : this.getListeners()) {
            if (eventListeners.size() > 0) {
                return true;
            }
        }

        return false;
    }

    public void addListener(String eventName, EventListenerInterface listener) {
        this.addListener(eventName, listener, 0);
    }

    public void addListener(String eventName, EventListenerInterface listener, Integer priority) {

        if (!this.listeners.containsKey(eventName)) {
            this.listeners.put(eventName, new HashMap<>());
        }

        HashMap<Integer, ArrayList<EventListenerInterface>> currentListenersByEventName = this.listeners.get(eventName);
        currentListenersByEventName.computeIfAbsent(priority, k -> new ArrayList<>());
        currentListenersByEventName.get(priority).add(listener);
    }

    protected boolean equalsListeners(Object o, Object that) {
        if (that == o) return true;
        if (o == null) return false;
        if (!that.getClass().getName().equals(o.getClass().getName())) return false;

        for (Method method : that.getClass().getMethods()) {
            try {
                o.getClass().getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();

                return false;
            }
        }

        return true;
    }

    @Override
    public void removeListener(String eventName, EventListenerInterface listener) throws Throwable {
        if (!this.hasListeners(eventName)) {
            return;
        }
        for (Map.Entry<Integer, ArrayList<EventListenerInterface>> integerArrayListEntry : this.listeners.get(eventName).entrySet()) {
            Integer priority = integerArrayListEntry.getKey();
            ArrayList<EventListenerInterface> listeners = integerArrayListEntry.getValue();

            for (EventListenerInterface v : new ArrayList<>(listeners)) {
                if (this.equalsListeners(v.getListener(), listener.getListener())) {
                    this.listeners.get(eventName).get(priority).remove(v);
                    log.log(Level.INFO, "Remove {2} event ({0}) from priority ({1})", new String[]{eventName, String.valueOf(priority), v.getListener().getName()});
                }
            }
            if (this.listeners.get(eventName).get(priority).size() == 0) {
                this.listeners.get(eventName).put(priority, new ArrayList<>());
                log.log(Level.INFO, "Remove all event ({0}) from priority ({1})", new String[]{eventName, String.valueOf(priority)});
            }
            if (this.listeners.get(eventName).size() == 0) {
                this.listeners.remove(eventName);
                log.log(Level.INFO, "Remove all event ({0})", eventName);
            }
        }
    }

    @Override
    public void addSubscriber(EventSubscriberInterface subscriber) {
        for (Map.Entry<String, ArrayList<EventListenerInterface>> entry : subscriber.getSubscribedEvents().entrySet()) {
            String eventName = entry.getKey();
            ArrayList<EventListenerInterface> calls = entry.getValue();
            for (EventListenerInterface call : calls) {
                if (call.getListener() instanceof EventSubscriberImpl) {
                    EventSubscriberImpl currentSubscriber = (EventSubscriberImpl) call.getListener();
                    this.addListener(eventName, call, currentSubscriber.getPriority());
                } else {
                    this.addListener(eventName, call, 0);
                }
            }
        }
    }

    @Override
    public void removeSubscriber(EventSubscriberInterface subscriber) throws Throwable {
        for (Map.Entry<String, ArrayList<EventListenerInterface>> stringArrayListEntry : subscriber.getSubscribedEvents().entrySet()) {
            String eventName = stringArrayListEntry.getKey();
            ArrayList<EventListenerInterface> currentListeners = stringArrayListEntry.getValue();
            for (EventListenerInterface currentListener : currentListeners) {
                this.removeListener(eventName, currentListener);
            }
        }
    }

    protected void callListeners(ArrayList<ArrayList<EventListenerInterface>> listeners, String eventName, Event event) {
        boolean stoppable = event != null;

        for (ArrayList<EventListenerInterface> listener : listeners) {
            if (stoppable && (event).isPropagationStopped()) {
                break;
            }

            for (EventListenerInterface eventListener : listener) {
                String method = eventListener.getMethod();
                try {
                    if (Objects.equals(method, "__invoke")) {
                        eventListener.getListener().getClass().getMethod(eventListener.getMethod()).invoke(eventListener.getListener());
                    } else {
                        eventListener.getListener().getClass().getMethod(eventListener.getMethod(), Event.class).invoke(eventListener.getListener(), event);
                    }
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
