package com.labudzinski.EventDispatcher;

import com.labudzinski.EventDispatcher.util.HashCode;

import java.lang.reflect.Method;

public class ClosureRunnable {
    private ClosureInterface closure;
    private String method = null;
    private Integer priority = 0;

    private Object[] parameters = null;

    public ClosureRunnable(ClosureInterface closure) {
        this.setConstructor(closure, null, null);
    }

    public ClosureRunnable(ClosureInterface closure, String method) {
        this.setConstructor(closure, method, null);
    }

    public ClosureRunnable(ClosureInterface closure, String method, Integer priority) {
        this.setConstructor(closure, method, priority);
    }

    private void setConstructor(ClosureInterface closure, String method, Integer priority) {
        this.closure = closure;
        this.method = method;
        this.priority = (priority == null) ? 0 : priority;
        try {
            this.hasMethod(closure);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void hasMethod(ClosureInterface closureInterface) throws NoSuchMethodException {
        if (this.method == null) {
            for (Method currentMethod : closureInterface.getClass().getMethods()) {
                if (currentMethod.getName().equals("invoke")) {
                    this.method = "invoke";
                    return;
                }
            }
            return;
        }

        for (Method currentMethod : closureInterface.getClass().getMethods()) {
            if (currentMethod.getName().equals(this.method)) {
                return;
            }
        }

        throw new NoSuchMethodException();
    }

    public ClosureInterface getClosure() {
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

        return that.closure.hashCode() == closure.hashCode();
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public int hashCode() {
        HashCode h = new HashCode();
        h.addValue(this.getClosure());
        h.addValue(this.getMethod());
        if (this.getParameters() != null) {
            h.addValue(this.getParameters());
        }
        return h.hashCode();
    }
}
