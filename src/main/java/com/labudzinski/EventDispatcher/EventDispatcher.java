package com.labudzinski.EventDispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventDispatcher implements EventDispatcherInterface {

    protected final Logger log = Logger.getLogger(getClass().getName());
    private final HashMap<String, HashMap<Integer, ArrayList<ClosureRunnable>>> listeners = new HashMap<>();

    @Override
    public Event dispatch(Event event, String eventName) {
        if (eventName == null) {
            eventName = event.getClass().toString();
        }

        String finalEventName = eventName;
        ArrayList<ClosureRunnable> listenerList = this.getListeners(finalEventName);
        HashMap<String, ArrayList<ClosureRunnable>> listeners = new HashMap<>() {{
            put(finalEventName, listenerList);
        }};
        if (!listeners.isEmpty()) {
            this.callListeners(listeners, eventName, event);
        }

        return event;
    }


    @Override
    public ArrayList<ClosureRunnable> getListeners(String eventName) {
        HashMap<String, ArrayList<ClosureRunnable>> listenersArray = this.getListeners();

        if (!listenersArray.containsKey(eventName)) {
            return new ArrayList<>();
        }

        return listenersArray.get(eventName);
    }


    public HashMap<String, ArrayList<ClosureRunnable>> getListeners() {
        HashMap<String, ArrayList<ClosureRunnable>> listenersArray = new HashMap<>();
        for (Map.Entry<String, HashMap<Integer, ArrayList<ClosureRunnable>>> entry : this.listeners.entrySet()) {
            String eventName = entry.getKey();
            HashMap<Integer, ArrayList<ClosureRunnable>> eventListeners = entry.getValue();
            ArrayList<ClosureRunnable> currentEventNamedListener = new ArrayList<>();
            NavigableMap<Integer, ArrayList<ClosureRunnable>> sorted = new TreeMap<>(eventListeners);
            for (Map.Entry<Integer, ArrayList<ClosureRunnable>> integerObjectEntry : sorted.entrySet()) {
                if (integerObjectEntry.getValue().size() > 0) {
                    currentEventNamedListener.addAll(integerObjectEntry.getValue());
                }
            }
            if (currentEventNamedListener.size() > 0) {
                listenersArray.put(eventName, currentEventNamedListener);
            }
        }
        return listenersArray;
    }

    @Override
    public Integer getListenerPriority(String eventName, ClosureRunnable listener) {
        if(!this.hasListeners(eventName)) {
            return null;
        }

        for (ClosureRunnable closureRunnable : this.getListeners(eventName)) {
            System.out.println(closureRunnable);
            System.out.println(listener);
            if(closureRunnable.equals(listener)) {
                return closureRunnable.getPriority();
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

        for (Map.Entry<String, ArrayList<ClosureRunnable>> stringArrayListEntry : this.getListeners().entrySet()) {
            if (stringArrayListEntry.getValue().size() > 0) {
                return true;
            }
        }

        return false;
    }

    public void addListener(String eventName, ClosureRunnable listener) {
        this.addListener(eventName, listener, 0);
    }

    public void addListener(String eventName, ClosureRunnable listener, Integer priority) {

        if (!this.listeners.containsKey(eventName)) {
            this.listeners.put(eventName, new HashMap<>());
        }

        HashMap<Integer, ArrayList<ClosureRunnable>> currentListenersByEventName = this.listeners.get(eventName);
        if(!currentListenersByEventName.containsKey(priority)) {
            currentListenersByEventName.put(priority, new ArrayList<>());
        }
        listener.setPriority(priority);
        currentListenersByEventName.get(priority).add(listener);

        this.listeners.put(eventName, currentListenersByEventName);
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
    public void removeListener(String eventName, ClosureRunnable listener) throws Throwable {
        if (!this.hasListeners(eventName)) {
            return;
        }
        for (Map.Entry<Integer, ArrayList<ClosureRunnable>> integerArrayListEntry : this.listeners.get(eventName).entrySet()) {
            Integer priority = integerArrayListEntry.getKey();
            ArrayList<ClosureRunnable> listeners = integerArrayListEntry.getValue();

            for (ClosureRunnable v : new ArrayList<>(listeners)) {
                if (this.equalsListeners(v, listener)) {
                    this.listeners.get(eventName).get(priority).remove(v);
                    log.log(Level.INFO, "Remove {2} event ({0}) from priority ({1})", new String[]{eventName, String.valueOf(priority), v.getClass().getName()});
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
        for (Map.Entry<String, ArrayList<ClosureRunnable>> entry : subscriber.getSubscribedEvents().entrySet()) {
            String eventName = entry.getKey();
            ArrayList<ClosureRunnable> calls = entry.getValue();
            for (ClosureRunnable call : calls) {
                if (call instanceof EventSubscriberImpl) {
                    EventSubscriberImpl currentSubscriber = (EventSubscriberImpl) call;
                    this.addListener(eventName, call, currentSubscriber.getPriority());
                } else {
                    this.addListener(eventName, call, 0);
                }
            }
        }
    }

    @Override
    public void removeSubscriber(EventSubscriberInterface subscriber) throws Throwable {
        for (Map.Entry<String, ArrayList<ClosureRunnable>> stringArrayListEntry : subscriber.getSubscribedEvents().entrySet()) {
            String eventName = stringArrayListEntry.getKey();
            ArrayList<ClosureRunnable> currentListeners = stringArrayListEntry.getValue();
            for (ClosureRunnable currentListener : currentListeners) {
                this.removeListener(eventName, currentListener);
            }
        }
    }

    protected void callListeners(HashMap<String, ArrayList<ClosureRunnable>> listeners, String eventName, Event event) {
        boolean stoppable = event != null;

        for (Map.Entry<String, ArrayList<ClosureRunnable>> stringArrayListEntry : listeners.entrySet()) {
            ArrayList<ClosureRunnable> listener = stringArrayListEntry.getValue();
            for (ClosureRunnable eventListener : listener) {
                System.out.println("listener: " + eventListener.getClosure().key);
                System.out.println("stoppable: " + (stoppable && (event).isPropagationStopped()));
                if (stoppable && (event).isPropagationStopped()) {
                    return;
                }

                try {
                    for (Method currentClassMethod : eventListener.getClosure().getClass().getMethods()) {
                        if (currentClassMethod.getName().equals(eventListener.getMethod())) {
                            System.out.println(currentClassMethod.getName());
                            currentClassMethod.invoke(eventListener.getClosure(), event);
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                System.out.println("stoppable: " + (stoppable && (event).isPropagationStopped()));
            }
        }

    }
}
