package io.brandoriented.EventDispatcher;

import io.brandoriented.EventDispatcher.exceptions.BadMethodCallException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventDispatcher implements EventDispatcherInterface {

    private HashMap<String, HashMap<Integer, ArrayList<Runnable>>> listeners;
    private HashMap<String, Object> sorted;
    private HashMap<String, Object> optimized;


    @Override
    public Event dispatch(Event event, String eventName) {
        if(eventName == null) {
            eventName = event.getClass().toString();
        }

        if(this.optimized != null) {
            if(this.optimized.has(eventName) != null) {
                listeners = this.optimized.get(eventName);
            } else if(!this.listeners.containsKey(eventName)) {
                listeners = new HashMap<Integer, Runnable>();
            } else {
                listeners = this.optimizeListeners(eventName);
            }

        } else {
            listeners = this.getListeners(eventName);
        }

        if (listeners) {
            this.callListeners(listeners, eventName, event);
        }

        return event;
    }

    @Override
    public Object getListeners(String eventName) {
        if (null != eventName) {
            if(!this.listeners.containsKey(eventName)) {
                return new Object();
            }

            if(!this.sorted.containsKey(eventName)) {
                this.sortListeners(eventName);
            }

            return this.sorted.get(eventName);
        }

        for(Map.Entry<String, HashMap<Integer, ArrayList<Runnable>>> entry : this.listeners.entrySet()) {
            String currentEventName = entry.getKey();

            if(!this.sorted.containsKey(currentEventName)) {
                this.sortListeners(currentEventName);
            }

        }


        return this.sorted;
    }

    @Override
    public Integer getListenerPriority(String eventName, Runnable listener) {

        if(!this.listeners.containsKey(eventName)) {
            return 1;
        }
        return 1;
    }

    @Override
    public boolean hasListeners(String eventName) {
        if (null != eventName) {
            return this.listeners.containsKey(eventName);
        }

        for(Map.Entry<String, HashMap<Integer, ArrayList<Runnable>>> entry : this.listeners.entrySet()) {
            HashMap<Integer, ArrayList<Runnable>> eventListeners = entry.getValue();
            if(!eventListeners.isEmpty()) {
                return true;
            }

        }
        return false;
    }

    public void addListener(String eventName, Runnable listener) {

        if(!this.listeners.containsKey(eventName)) {
            this.listeners.put(eventName, new HashMap<>());
        }

        HashMap<Integer, ArrayList<Runnable>> currentListenersByEventName = this.listeners.get(eventName);
        ArrayList<Runnable> currentListeners = currentListenersByEventName.get(priority);
        if(currentListeners != null) {
            currentListeners.add(listener);
        } else {
            currentListenersByEventName.put(priority, new ArrayList<Runnable>() {{
                add(listener);
            }});
        }

        this.sorted.remove(eventName);
        this.optimized.remove(eventName);
        
    }

    @Override
    public void removeListener(String eventName, Runnable listener) throws Throwable {
        if(!this.listeners.containsKey(eventName)) {
            return;
        }

        for(Map.Entry<Integer, ArrayList<Runnable>> entry : this.listeners.get(eventName).entrySet()) {
            Integer priority = entry.getKey();
            ArrayList<Runnable> listeners = entry.getValue();

            for (Runnable v : listeners) {
                Integer k = listeners.indexOf(v);

                if(v != listener && v != null) {
                    v = () -> {};
                }

                if(v == listener) {
                    listeners.remove(v);
                    this.sorted.remove(eventName);
                    this.optimized.remove(eventName);
                }
            }

            if(listeners.isEmpty()) {
                HashMap<Integer, ArrayList<Runnable>> currentListeners = this.listeners.get(eventName);
                currentListeners.remove(priority);
                this.listeners.put(eventName, currentListeners);
            }
        }
    }

    @Override
    public void addSubscriber(EventSubscriberInterface subscriber) throws Throwable {

        for(Map.Entry<String, Map<String, Integer>> entry : subscriber.getSubscribedEvents().entrySet()) {
            String eventName = entry.getKey();
            Map<String, Integer> params = entry.getValue();

            this.addListener(eventName, new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    @Override
    public void removeSubscriber(EventSubscriberInterface subscriber) throws Throwable {

    }


}
