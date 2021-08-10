package com.labudzinski.EventDispatcher;

import com.labudzinski.EventDispatcher.exceptions.InvalidArgumentException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GenericEvent extends Event {

    protected Object subject;
    protected HashMap<String, Object> arguments;

    public GenericEvent(Object subject,
                        HashMap<String, Object> arguments) {

        this.subject = subject;
        this.arguments = arguments;
    }

    public Object getSubject() {
        return subject;
    }

    public void setSubject(Object subject) {
        this.subject = subject;
    }

    public Object getArgument(String key) throws InvalidArgumentException {
        if(this.arguments.containsKey(key)) {
            return this.arguments.get(key);
        }

        throw new InvalidArgumentException(String.format("Argument \"%s\" not found.", key));
    }

    public HashMap<String, Object> getArguments() {
        return arguments;
    }

    public Event setArgument(String key, Object value) {
        this.arguments.put(key, value);

        return this;
    }

    public Event setArguments(HashMap<String, Object> arguments) {
        this.arguments = arguments;

        return this;
    }

    public boolean hasArgument(String key) {
        return this.arguments.containsKey(key);
    }

    public Object offsetGet(String key) {
        try {
            return this.getArgument(key);
        } catch (InvalidArgumentException e) {
            return null;
        }
    }

    public void offsetSet(String key, Object value) {
        this.setArgument(key, value);
    }

    public void offsetUnset(String key) {
        if (this.hasArgument(key)) {
            this.arguments.remove(key);
        }
    }

    public boolean offsetExists(String key) {
        return this.hasArgument(key);
    }


    public Iterator<Map.Entry<String, Object>> getIterator() {
        return this.arguments.entrySet().iterator();
    }
}
