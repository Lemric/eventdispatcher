# event-dispatcher

[![CI](https://github.com/labudzinski/event-dispatcher/actions/workflows/maven.yml/badge.svg)](https://github.com/labudzinski/event-dispatcher/actions/workflows/maven.yml) [![CircleCI](https://circleci.com/gh/labudzinski/eventdispatcher/tree/master.svg?style=svg)](https://circleci.com/gh/labudzinski/eventdispatcher/tree/master)

### Events

When an event is dispatched, it’s identified by a unique name (e.g. kernel.response), which any number of listeners
might be listening to. An com.labudzinski.eventdispatcher.Event instance is also created and passed to all of the
listeners. As you’ll see later, the Event object itself often contains data about the event being dispatched.

### The Dispatcher

The dispatcher is the central object of the event dispatching system. Generally one dispatcher is created, which keeps a
register of listeners. When an event is dispatched via the dispatcher, it notifies all listeners registered in that
event:

```java
import com.labudzinski.eventdispatcher.EventDispatcher;
class Application {
    public static void main(String[] args) {
        EventDispatcher dispatcher = new EventDispatcher();
    }
}
```

### Connecting Listeners

To use an existing event, connect the listener to the dispatcher so that it can be notified when the event is sent.
Calling the dispatcher's addListener() method associates all valid Callable to an Event:

```java
import com.labudzinski.eventdispatcher.EventDispatcher;
import com.labudzinski.eventdispatcher.EventListenerInterface;

class Application {
    public static void main(String[] args) {
        AcmeListener listener = new AcmeListener();
        EventDispatcher dispatcher = new EventDispatcher();
        dispatcher.addListener('foo', listener::preFoo);
        dispatcher.addListener('foo', (event) -> listener.postFoo(event), 10);
    }
}
```

The addListener() method takes up to three arguments:

- The name of the event (string) this listener wants to listen to;
- Callable that will be executed when the specified event is fired;
- Optional priority, defined as a positive or negative integer (0 by default). The higher the number, the earlier it
  calls the listener. If two receivers have the same priority, they are executed in the order in which they were added
  to the dispatcher.

After registering the listener with the dispatcher, it waits for notification about the event. In the example above,
when the foo event is dispatched, the dispatcher calls the AcmeListener.preFoo() method and passes the Event object as a
single argument:

```java
import com.labudzinski.eventdispatcher.Event;

class AcmeListener {
    public Event onFooAction(Event event) {
        //... some action
      return event;
    }
}
```

The event argument is the event object that was passed when the event was dispatched. In many cases, the special event
subclass is passed with additional information. You can check the documentation or implementation of each event to
determine which instance is being passed.