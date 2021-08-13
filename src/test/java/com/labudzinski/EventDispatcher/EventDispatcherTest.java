package com.labudzinski.EventDispatcher;

import com.labudzinski.EventDispatcher.events.Dispatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EventDispatcherTest {

    private static final String preFoo = "pre.foo";
    private static final String preBar = "pre.bar";
    private static final String postFoo = "post.foo";

    private EventDispatcher dispatcher;
    private TestEventListener listener;

    @BeforeEach
    void setUp() {
        this.dispatcher = new EventDispatcher();
        this.listener = new TestEventListener();
    }

    @AfterEach
    void tearDown() {
        this.dispatcher = null;
        this.listener = null;
    }

    @Test
    public void testInitialState() {
        assertEquals(new HashMap<>(), this.dispatcher.getListeners());
        assertFalse(this.dispatcher.hasListeners(preFoo));
        assertFalse(this.dispatcher.hasListeners(postFoo));
    }

    @Test
    public void testAddListener() throws NoSuchMethodException {
        this.dispatcher.addListener(preFoo, new ClosureRunnable(this.listener, "preFoo"));
        this.dispatcher.addListener(postFoo, new ClosureRunnable(this.listener, "postFoo"));

        assertTrue(this.dispatcher.hasListeners());
        assertTrue(this.dispatcher.hasListeners(preFoo));
        assertTrue(this.dispatcher.hasListeners(postFoo));

        assertEquals(1, this.dispatcher.getListeners(preFoo).size());
        assertEquals(1, this.dispatcher.getListeners(postFoo).size());
        assertEquals(2, this.dispatcher.getListeners().size());
    }

    @Test
    public void testGetListenersSortsByPriority() {
        TestEventListener listener1 = new TestEventListener();
        TestEventListener listener2 = new TestEventListener();
        TestEventListener listener3 = new TestEventListener();


        this.dispatcher.addListener(preFoo, new ClosureRunnable(listener1, "preFoo"), -10);
        this.dispatcher.addListener(preFoo, new ClosureRunnable(listener2, "preFoo"), 10);
        this.dispatcher.addListener(preFoo, new ClosureRunnable(listener3, "preFoo"));

        HashMap<String, ArrayList<ClosureRunnable>> expected = new HashMap<>() {{
            put(preFoo, new ArrayList<>() {{
                add(new ClosureRunnable(listener2, "preFoo", 10));
                add(new ClosureRunnable(listener3, "preFoo", 0));
                add(new ClosureRunnable(listener1, "preFoo", -10));
            }});
        }};
        assertThat(this.dispatcher.getListeners(preFoo))
                .usingRecursiveComparison()
                .isEqualTo(expected.get(preFoo));
    }

    @Test
    public void testGetAllListenersSortsByPriority() {
        TestEventListener listener1 = new TestEventListener();
        TestEventListener listener2 = new TestEventListener();
        TestEventListener listener3 = new TestEventListener();
        TestEventListener listener4 = new TestEventListener();
        TestEventListener listener5 = new TestEventListener();
        TestEventListener listener6 = new TestEventListener();

        this.dispatcher.addListener(preFoo, new ClosureRunnable(listener1), -10);
        this.dispatcher.addListener(preFoo, new ClosureRunnable(listener2));
        this.dispatcher.addListener(preFoo, new ClosureRunnable(listener3), 10);
        this.dispatcher.addListener(postFoo, new ClosureRunnable(listener4), -10);
        this.dispatcher.addListener(postFoo, new ClosureRunnable(listener5));
        this.dispatcher.addListener(postFoo, new ClosureRunnable(listener6), 10);

        HashMap<String, ArrayList<ClosureRunnable>> expected = new HashMap<>() {{
            put(preFoo, new ArrayList<>() {{
                add(new ClosureRunnable(listener3, null, 10));
                add(new ClosureRunnable(listener2));
                add(new ClosureRunnable(listener1, null, -10));
            }});
            put(postFoo, new ArrayList<>() {{
                add(new ClosureRunnable(listener6, null, 10));
                add(new ClosureRunnable(listener5));
                add(new ClosureRunnable(listener4, null, -10));
            }});
        }};

        assertThat(this.dispatcher.getListeners())
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    public void testGetListenerPriority() {
        TestEventListener listener1 = new TestEventListener();
        TestEventListener listener2 = new TestEventListener();

        this.dispatcher.addListener(preFoo, new ClosureRunnable(listener1), -10);
        this.dispatcher.addListener(preFoo, new ClosureRunnable(listener2));
        assertSame(-10, this.dispatcher.getListenerPriority(preFoo, new ClosureRunnable(listener1)));
        assertSame(0, this.dispatcher.getListenerPriority(preFoo, new ClosureRunnable(listener2)));
        assertNull(this.dispatcher.getListenerPriority(preBar,  new ClosureRunnable(listener2)));
        assertNull(this.dispatcher.getListenerPriority(preFoo,  new ClosureRunnable(new Closure() {})));
    }

    @Test
    public void testDispatch() {
        this.dispatcher.addListener("pre.foo", new ClosureRunnable(this.listener, "preFoo"));
        this.dispatcher.addListener("post.foo", new ClosureRunnable(this.listener, "postFoo"));
        this.dispatcher.dispatch(new Event(), preFoo);
        assertTrue(this.listener.preFooInvoked);
        assertFalse(this.listener.postFooInvoked);
        assertInstanceOf(Event.class, this.dispatcher.dispatch(new Event(), "noevent"));
        assertInstanceOf(Event.class, this.dispatcher.dispatch(new Event(), preFoo));
        Event event = new Event();
        Event result = this.dispatcher.dispatch(event, preFoo);
        assertSame(event, result);
    }

    @Test
    public void testDispatchForClosure() {
        final int[] invoked = {0};
        ClosureInterface listener = new Closure() {
            public Event invoke(Event event) {
                invoked[0]++;
                return event;
            }
        };
        this.dispatcher.addListener("pre.foo", new ClosureRunnable(listener));
        this.dispatcher.addListener("post.foo", new ClosureRunnable(listener));
        this.dispatcher.dispatch(new Event(), preFoo);

        assertEquals(1, invoked[0]);
    }

    @Test
    public void testStopEventPropagation() {
        TestEventListener otherListener = new TestEventListener();
        this.dispatcher.addListener("post.foo", new ClosureRunnable(this.listener, "postFoo"), 10);
        this.dispatcher.addListener("post.foo", new ClosureRunnable(otherListener, "postFoo"));
        this.dispatcher.dispatch(new Event(), postFoo);
        assertTrue(this.listener.postFooInvoked);
        assertFalse(otherListener.postFooInvoked);
    }

    @Test
    public void testDispatchByPriority() {
        final ArrayList<Integer> invoked = new ArrayList<>();
        ClosureInterface listener1 = new Closure() {
            public Event invoke(Event event) {
                invoked.add(1);
                return event;
            }
        };
        ClosureInterface listener2 = new Closure() {
            public Event invoke(Event event) {
                invoked.add(2);
                return event;
            }
        };
        ClosureInterface listener3 = new Closure() {
            public Event invoke(Event event) {
                invoked.add(3);
                return event;
            }
        };

        this.dispatcher.addListener(preFoo, new ClosureRunnable(listener1), -10);
        this.dispatcher.addListener(preFoo, new ClosureRunnable(listener2));
        this.dispatcher.addListener(preFoo, new ClosureRunnable(listener3), 10);
        this.dispatcher.dispatch(new Event(), preFoo);
        assertThat(new int[]{3, 2, 1}).isEqualTo(invoked.stream().mapToInt(i -> i).toArray());
    }

    @Test
    public void testRemoveListener() throws Throwable {
        this.dispatcher.addListener(preBar, new ClosureRunnable(this.listener));
        assertTrue(this.dispatcher.hasListeners(preBar));
        this.dispatcher.removeListener(preBar, new ClosureRunnable(this.listener));
        assertFalse(this.dispatcher.hasListeners(preBar));
        this.dispatcher.removeListener("notExists", new ClosureRunnable(this.listener));
    }

    @Test
    public void testAddSubscriber() throws Throwable {
        TestEventSubscriber eventSubscriber = new TestEventSubscriber();
        this.dispatcher.addSubscriber(eventSubscriber);
        assertTrue(this.dispatcher.hasListeners(preFoo));
        assertTrue(this.dispatcher.hasListeners(postFoo));
    }

    @Test
    public void testAddSubscriberWithPriorities() throws Throwable {
        this.dispatcher.addSubscriber(new TestEventSubscriber());
        TestEventSubscriberWithPriorities subscriber2 = new TestEventSubscriberWithPriorities();
        this.dispatcher.addSubscriber(subscriber2);

        ArrayList<ClosureRunnable> listeners = this.dispatcher.getListeners(preFoo);
        assertTrue(this.dispatcher.hasListeners(preFoo));
        assertEquals(2, listeners.size());
        String className = listeners.get(1).getClosure().getClass().getTypeName();
        className = className.replace("$1", "");
        assertEquals(className, TestEventSubscriberWithPriorities.class.getName());
    }

    @Test
    public void testAddSubscriberWithMultipleListeners() {
        TestEventSubscriberWithMultipleListeners eventSubscriber = new TestEventSubscriberWithMultipleListeners();
        this.dispatcher.addSubscriber(eventSubscriber);

        ArrayList<ClosureRunnable> listeners = this.dispatcher.getListeners(preFoo);
        assertTrue(this.dispatcher.hasListeners(preFoo));
        assertEquals(2, listeners.size());
        assertEquals("preFoo2", listeners.get(1).getMethod());
    }

    @Test
    public void testRemoveSubscriber() throws Throwable {
        TestEventSubscriber eventSubscriber = new TestEventSubscriber();
        this.dispatcher.addSubscriber(eventSubscriber);
        assertTrue(this.dispatcher.hasListeners(preFoo));
        assertTrue(this.dispatcher.hasListeners(postFoo));
        this.dispatcher.removeSubscriber(eventSubscriber);
        assertFalse(this.dispatcher.hasListeners(preFoo));
        assertFalse(this.dispatcher.hasListeners(postFoo));
    }

    @Test
    public void testRemoveSubscriberWithPriorities() throws Throwable {
        TestEventSubscriberWithPriorities eventSubscriber = new TestEventSubscriberWithPriorities();
        this.dispatcher.addSubscriber(eventSubscriber);
        assertTrue(this.dispatcher.hasListeners(preFoo));
        this.dispatcher.removeSubscriber(eventSubscriber);
        assertFalse(this.dispatcher.hasListeners(preFoo));
    }

    @Test
    public void testRemoveSubscriberWithMultipleListeners() throws Throwable {
        TestEventSubscriberWithMultipleListeners eventSubscriber = new TestEventSubscriberWithMultipleListeners();
        this.dispatcher.addSubscriber(eventSubscriber);
        assertTrue(this.dispatcher.hasListeners(preFoo));
        assertEquals(2, this.dispatcher.getListeners(preFoo).size());
        this.dispatcher.removeSubscriber(eventSubscriber);
        assertFalse(this.dispatcher.hasListeners(preFoo));
    }

    @Test
    public void testEventReceivesTheDispatcherInstanceAsArgument() {
        TestWithDispatcher listener = new TestWithDispatcher();
        this.dispatcher.addListener("test", new ClosureRunnable(listener, "foo"));
        assertNull(listener.name);
        assertNull(listener.dispatcher);
        this.dispatcher.dispatch(new Dispatcher("test", this.dispatcher), "test");
        assertEquals("test", listener.name);
        assertSame(this.dispatcher, listener.dispatcher);
    }

    @Test
    public void testWorkaroundForPhpBug62976() throws Throwable {
        ChildEventDispatcher dispatcher = new ChildEventDispatcher();
        ClosureInterface listener = new Closure() {
            public Event invoke(Event event) {
                return event;
            }
        };
        ClosureInterface listener2 = new Closure() {
            public Event invoke(Event event) {
                return event;
            }
        };
        dispatcher.addListener("bug.62976", new ClosureRunnable(listener));
        dispatcher.removeListener("bug.62976", new ClosureRunnable(listener2));
        assertTrue(dispatcher.hasListeners("bug.62976"));
    }

    @Test
    public void testHasListenersWhenAddedCallbackListenerIsRemoved() throws Throwable {
        ClosureInterface listener = new Closure() {
            public Event invoke(Event event) {
                return event;
            }
        };
        this.dispatcher.addListener("foo", new ClosureRunnable(listener));
        this.dispatcher.removeListener("foo", new ClosureRunnable(listener));
        assertFalse(this.dispatcher.hasListeners());
    }

    @Test
    public void testGetListenersWhenAddedCallbackListenerIsRemoved() throws Throwable {
        ClosureInterface listener = new Closure() {
            public Event invoke(Event event) {
                return event;
            }
        };
        this.dispatcher.addListener("foo", new ClosureRunnable(listener));
        this.dispatcher.removeListener("foo", new ClosureRunnable(listener));
        assertThat(new HashMap<>()).isEqualTo(this.dispatcher.getListeners());
    }

    @Test
    public void testHasListenersWithoutEventsReturnsFalseAfterHasListenersWithEventHasBeenCalled()
    {
        assertFalse(this.dispatcher.hasListeners("foo"));
        assertFalse(this.dispatcher.hasListeners());
    }

    @Test
    public void testHasListenersIsLazy() {
        final int[] called = {0};
        ClosureInterface listener = new Closure() {
            public Event invoke(Event event) {
                ++called[0];
                return event;
            }
        };

        this.dispatcher.addListener("foo", new ClosureRunnable(listener));
        assertTrue(this.dispatcher.hasListeners());
        assertTrue(this.dispatcher.hasListeners("foo"));
        assertEquals(0, called[0]);
    }

    @Test
    public void testDispatchLazyListener() {
        final int[] called = {0};
        TestWithDispatcher dispatcher = new TestWithDispatcher();
        ClosureInterface factory = new Closure() {
            public TestWithDispatcher foo(Event event) {
                called[0] += 1;
                return dispatcher;
            }
        };

        this.dispatcher.addListener("foo", new ClosureRunnable(factory, "foo"));
        assertSame(0, called[0]);
        this.dispatcher.dispatch(new Event(), "foo");
        assertFalse(dispatcher.invoked);
        this.dispatcher.dispatch(new Event(), "foo");
        assertSame(2, called[0]);

        this.dispatcher.addListener("bar", new ClosureRunnable(factory));
        assertSame(2, called[0]);
        Dispatcher currentEvent = new Dispatcher(true);
        this.dispatcher.dispatch(currentEvent, "bar");
        System.out.println(currentEvent.getInvoked());
        assertTrue(currentEvent.getInvoked());
        this.dispatcher.dispatch(new Event(), "bar");
        assertSame(2, called[0]);
    }

    @Test
    public void testRemoveFindsLazyListeners() throws Throwable {
        TestWithDispatcher test = new TestWithDispatcher();
        this.dispatcher.addListener("foo", new ClosureRunnable(test, "foo"));
        assertTrue(this.dispatcher.hasListeners("foo"));
        this.dispatcher.removeListener("foo", new ClosureRunnable(test, "foo"));
        assertFalse(this.dispatcher.hasListeners("foo"));
        this.dispatcher.addListener("foo", new ClosureRunnable(test, "foo"));
        assertTrue(this.dispatcher.hasListeners("foo"));
        this.dispatcher.removeListener("foo", new ClosureRunnable(test, "foo"));
        assertFalse(this.dispatcher.hasListeners("foo"));
    }

    @Test
    public void testPriorityFindsLazyListeners() throws Throwable {
        TestWithDispatcher test = new TestWithDispatcher();
        ClosureInterface factory = new Closure() {
            public Event foo(Event event) {
                return event;
            }
        };

        this.dispatcher.addListener("foo", new ClosureRunnable(factory, "foo"), 3);
        assertSame(3, this.dispatcher.getListenerPriority("foo", new ClosureRunnable(factory, "foo")));
        this.dispatcher.removeListener("foo", new ClosureRunnable(factory, "foo"));

        this.dispatcher.addListener("foo", new ClosureRunnable(test, "foo"), 5);
        assertSame(5, this.dispatcher.getListenerPriority("foo", new ClosureRunnable(test, "foo")));
    }

    @Test
    public void testGetLazyListeners() throws Throwable {
        TestWithDispatcher test = new TestWithDispatcher();
        ClosureInterface factory = new Closure() {
            public Event foo(Event event) {
                return event;
            }
        };

        this.dispatcher.addListener("foo", new ClosureRunnable(factory, "foo"), 3);
        assertThat(this.dispatcher.getListeners("foo")).usingRecursiveComparison().isEqualTo(new ArrayList<>() {{
            add(new ClosureRunnable(factory, "foo", 3));
        }});

        this.dispatcher.removeListener("foo", new ClosureRunnable(test, "foo"));
        this.dispatcher.addListener("bar", new ClosureRunnable(factory, "foo"), 3);
        assertThat(this.dispatcher.getListeners()).usingRecursiveComparison().isEqualTo(new HashMap<String, Object>() {{
            put("foo", new ArrayList<>() {{
                add(new ClosureRunnable(factory, "foo", 3));
            }});
            put("bar", new ArrayList<>() {{
                add(new ClosureRunnable(factory, "foo", 3));
            }});
        }});
    }

    @Test
    public void testMutatingWhilePropagationIsStopped() {
        final Boolean[] testLoaded = {false};
        TestEventListener test = new TestEventListener();
        this.dispatcher.addListener("foo", new ClosureRunnable(test, "postFoo"));
        this.dispatcher.addListener("foo", new ClosureRunnable(new Closure() {
            public Event preFoo(TestLoadedEvent event) {
                testLoaded[0] = event.getTestLoaded();
                return event;
            }
        }, "preFoo"));

        this.dispatcher.dispatch(new TestLoadedEvent(true), "foo");

        assertTrue(test.postFooInvoked);
        assertFalse(test.preFooInvoked);

        assertSame(0, this.dispatcher.getListenerPriority("foo", new ClosureRunnable(test, "preFoo")));
        test.preFoo(new TestLoadedEvent(true));
        this.dispatcher.dispatch(new TestLoadedEvent(true), "foo");

        assertTrue(testLoaded[0]);
    }
}