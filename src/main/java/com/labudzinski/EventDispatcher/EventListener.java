//package com.labudzinski.EventDispatcher;
//
//public class EventListener implements EventListenerInterface {
//
//    private final Object listener;
//    private final String method;
//    private final Object[] parameters;
//
//    public EventListener(EventListenerImpl listener) {
//        this.listener = listener;
//        this.method = "invoke";
//        this.parameters = null;
//    }
//
//    public EventListener(Object listener, String method) {
//        this.listener = listener;
//        this.method = method;
//        this.parameters = null;
//    }
//
//    public EventListener(EventListenerImpl listener,
//                         String method,
//                         Object[] parameters) {
//        this.listener = listener;
//        this.method = method;
//        this.parameters = parameters;
//    }
//
//    @Override
//    public String getMethod() {
//        return this.method;
//    }
//
//    @Override
//    public EventListenerImpl getListener() {
//        return this.listener;
//    }
//
//    @Override
//    public Object[] getParameters() {
//        return this.parameters;
//    }
//}
