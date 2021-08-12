package com.labudzinski.EventDispatcher;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ClosureRunnable {
    private Closure closure;
    private String method = null;
    private Integer priority = 0;

    private Object[] parameters;

    public ClosureRunnable(Closure closure) {
        this.setConstructor(closure, null, null);
    }

    public ClosureRunnable(Closure closure, String method) {
        this.setConstructor(closure, method, null);
    }

    public ClosureRunnable(Closure closure, String method, Integer priority) {
        this.setConstructor(closure, method, priority);
    }

    private void setConstructor(Closure closure, String method, Integer priority) {
        this.closure = closure;
        this.method = method;
        this.priority = (priority == null) ? 0 : priority;

        try {
            this.hasMethod(closure);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void hasMethod(Closure closure) throws NoSuchMethodException {
        if(this.method == null) {
            for (Method currentMethod : closure.getClass().getMethods()) {
                if(currentMethod.getName().equals("invoke")) {
                    this.method = "invoke";
                    return;
                }
            }
            return;
        }

        for (Method currentMethod : closure.getClass().getMethods()) {
            if(currentMethod.getName().equals(this.method)) {
                return;
            }
        }

        throw new NoSuchMethodException();
    }

    public Closure getClosure() {
        return closure;
    }

    public String getMethod() {
        return method;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClosureRunnable that = (ClosureRunnable) o;

        return that.closure.equals(closure) && method == that.method &&
                priority.equals(that.priority);
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
