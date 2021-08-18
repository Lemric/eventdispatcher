package com.labudzinski.EventDispatcher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        assertEquals(new ArrayList<>(), this.dispatcher.getListeners());
        assertFalse(this.dispatcher.hasListeners(preFoo));
        assertFalse(this.dispatcher.hasListeners(postFoo));
    }

    @Test
    public void testAddListener() throws NoSuchMethodException {
        this.dispatcher.addListener(preFoo, new EventListener<>(this.listener.preFoo()));
        this.dispatcher.addListener(postFoo, new EventListener<>(this.listener.postFoo()));

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


        this.dispatcher.addListener(preFoo, new EventListener<>(listener1.preFoo()), -10);
        this.dispatcher.addListener(preFoo, new EventListener<>(listener2.preFoo()), 10);
        this.dispatcher.addListener(preFoo, new EventListener<>(listener3.preFoo()));

        HashMap<String, List<EventListener<? extends Event>>> expected = new HashMap<String,  List<EventListener<? extends Event>>>() {{
            put(preFoo, new ArrayList<EventListener<? extends Event>>() {{
                add(new EventListener<>(listener2.preFoo(), 10));
                add(new EventListener<>(listener3.preFoo(), 0));
                add(new EventListener<>(listener1.preFoo(), -10));
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

        this.dispatcher.addListener(preFoo, new EventListener<>(listener1.onEvent()), -10);
        this.dispatcher.addListener(preFoo, new EventListener<>(listener2.onEvent()));
        this.dispatcher.addListener(preFoo, new EventListener<>(listener3.onEvent()), 10);
        this.dispatcher.addListener(postFoo, new EventListener<>(listener4.onEvent()), -10);
        this.dispatcher.addListener(postFoo, new EventListener<>(listener5.onEvent()));
        this.dispatcher.addListener(postFoo, new EventListener<>(listener6.onEvent()), 10);

        List<EventListener<? extends Event>> expected = new ArrayList<EventListener<? extends Event>>() {{
            add(new EventListener<>(listener3.onEvent(), 10));
            add(new EventListener<>(listener2.onEvent()));
            add(new EventListener<>(listener1.onEvent(), -10));
            add(new EventListener<>(listener6.onEvent(), 10));
            add(new EventListener<>(listener5.onEvent()));
            add(new EventListener<>(listener4.onEvent(), -10));
        }};

        assertThat(this.dispatcher.getListeners())
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    public void testGetListenerPriority() {
        TestEventListener listener1 = new TestEventListener();
        TestEventListener listener2 = new TestEventListener();

        this.dispatcher.addListener(preFoo, listener1, -10);
        this.dispatcher.addListener(preFoo, listener2);
        assertSame(-10, this.dispatcher.getListenerPriority(preFoo, listener1));
        assertSame(0, this.dispatcher.getListenerPriority(preFoo, listener2));
        assertNull(this.dispatcher.getListenerPriority(preBar, listener2));
        assertNull(this.dispatcher.getListenerPriority(preFoo,  new EventListener<>(((event) -> {}))));
    }

    @Test
    public void testDispatch() {
        this.dispatcher.addListener(preFoo, new EventListener<>(this.listener.preFoo()));
        this.dispatcher.addListener(postFoo, new EventListener<>(this.listener.postFoo()));
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
        EventListener<Event> listener = new EventListener<>((event) -> {
            invoked[0]++;
        });
        this.dispatcher.addListener("pre.foo", listener);
        this.dispatcher.addListener("post.foo", listener);
        this.dispatcher.dispatch(new Event(), preFoo);

        assertEquals(1, invoked[0]);
    }

    @Test
    public void testStopEventPropagation() {
        TestEventListener otherListener = new TestEventListener();
        this.dispatcher.addListener(postFoo, new EventListener<>(this.listener.postFoo()), 10);
        this.dispatcher.addListener(postFoo, otherListener);
        this.dispatcher.dispatch(new Event(), postFoo);
        assertTrue(this.listener.postFooInvoked);
        assertFalse(otherListener.postFooInvoked);
    }

    @Test
    public void testDispatchByPriority() {
        final ArrayList<Integer> invoked = new ArrayList<>();
        EventListener<Event> listener1 = new EventListener<>((event) -> {
            invoked.add(1);
        });
        EventListener<Event> listener2 = new EventListener<>((event) -> {
            invoked.add(2);
        });
        EventListener<Event> listener3 = new EventListener<>((event) -> {
            invoked.add(3);
        });

        this.dispatcher.addListener(preFoo, listener1, -10);
        this.dispatcher.addListener(preFoo, listener2);
        this.dispatcher.addListener(preFoo, listener3, 10);
        this.dispatcher.dispatch(new Event(), preFoo);
        assertThat(new int[]{3, 2, 1}).isEqualTo(invoked.stream().mapToInt(i -> i).toArray());
    }

    @Test
    public void testRemoveListener() throws Throwable {
        this.dispatcher.addListener(preBar, this.listener);
        assertTrue(this.dispatcher.hasListeners(preBar));
        this.dispatcher.removeListener(preBar, this.listener);
        assertFalse(this.dispatcher.hasListeners(preBar));
        this.dispatcher.removeListener("notExists", this.listener);
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

        List<EventListener<? extends Event>> listeners = this.dispatcher.getListeners(preFoo);
        assertTrue(this.dispatcher.hasListeners(preFoo));
        assertEquals(2, listeners.size());

    }

    @Test
    public void testAddSubscriberWithMultipleListeners() throws Throwable {
        TestEventSubscriberWithMultipleListeners eventSubscriber = new TestEventSubscriberWithMultipleListeners();
        this.dispatcher.addSubscriber(eventSubscriber);

        List<EventListener<? extends Event>> listeners = this.dispatcher.getListeners(preFoo);
        assertTrue(this.dispatcher.hasListeners(preFoo));
        assertEquals(2, listeners.size());
    }

    @Test
    public void testRemoveSubscriber() {
        TestEventSubscriber eventSubscriber = new TestEventSubscriber();
        this.dispatcher.addSubscriber(eventSubscriber);
        assertTrue(this.dispatcher.hasListeners(preFoo));
        assertTrue(this.dispatcher.hasListeners(postFoo));
        this.dispatcher.removeSubscriber(eventSubscriber);
        assertFalse(this.dispatcher.hasListeners(preFoo));
        assertFalse(this.dispatcher.hasListeners(postFoo));
    }
/*
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
        this.dispatcher.addListener("test", new EventListener<>(listener, "foo"));
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
        dispatcher.addListener("bug.62976", new EventListener<>(listener));
        dispatcher.removeListener("bug.62976", new EventListener<>(listener2));
        assertTrue(dispatcher.hasListeners("bug.62976"));
    }

    @Test
    public void testHasListenersWhenAddedCallbackListenerIsRemoved() throws Throwable {
        ClosureInterface listener = new Closure() {
            public Event invoke(Event event) {
                return event;
            }
        };
        this.dispatcher.addListener("foo", new EventListener<>(listener));
        this.dispatcher.removeListener("foo", new EventListener<>(listener));
        assertFalse(this.dispatcher.hasListeners());
    }

    @Test
    public void testGetListenersWhenAddedCallbackListenerIsRemoved() throws Throwable {
        ClosureInterface listener = new Closure() {
            public Event invoke(Event event) {
                return event;
            }
        };
        this.dispatcher.addListener("foo", new EventListener<>(listener));
        this.dispatcher.removeListener("foo", new EventListener<>(listener));
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

        this.dispatcher.addListener("foo", new EventListener<>(listener));
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

        this.dispatcher.addListener("foo", new EventListener<>(factory, "foo"));
        assertSame(0, called[0]);
        this.dispatcher.dispatch(new Event(), "foo");
        assertFalse(dispatcher.invoked);
        this.dispatcher.dispatch(new Event(), "foo");
        assertSame(2, called[0]);

        this.dispatcher.addListener("bar", new EventListener<>(factory));
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
        this.dispatcher.addListener("foo", new EventListener<>(test, "foo"));
        assertTrue(this.dispatcher.hasListeners("foo"));
        this.dispatcher.removeListener("foo", new EventListener<>(test, "foo"));
        assertFalse(this.dispatcher.hasListeners("foo"));
        this.dispatcher.addListener("foo", new EventListener<>(test, "foo"));
        assertTrue(this.dispatcher.hasListeners("foo"));
        this.dispatcher.removeListener("foo", new EventListener<>(test, "foo"));
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

        this.dispatcher.addListener("foo", new EventListener<>(factory, "foo"), 3);
        assertSame(3, this.dispatcher.getListenerPriority("foo", new EventListener<>(factory, "foo")));
        this.dispatcher.removeListener("foo", new EventListener<>(factory, "foo"));

        this.dispatcher.addListener("foo", new EventListener<>(test, "foo"), 5);
        assertSame(5, this.dispatcher.getListenerPriority("foo", new EventListener<>(test, "foo")));
    }

    @Test
    public void testGetLazyListeners() throws Throwable {
        TestWithDispatcher test = new TestWithDispatcher();
        ClosureInterface factory = new Closure() {
            public Event foo(Event event) {
                return event;
            }
        };

        this.dispatcher.addListener("foo", new EventListener<>(factory, "foo"), 3);
        assertThat(this.dispatcher.getListeners("foo")).usingRecursiveComparison().isEqualTo(new ArrayList<Object>() {{
            add(new EventListener<>(factory, "foo", 3));
        }});

        this.dispatcher.removeListener("foo", new EventListener<>(test, "foo"));
        this.dispatcher.addListener("bar", new EventListener<>(factory, "foo"), 3);
        assertThat(this.dispatcher.getListeners()).usingRecursiveComparison().isEqualTo(new HashMap<String, Object>() {{
            put("foo", new ArrayList<Object>() {{
                add(new EventListener<>(factory, "foo", 3));
            }});
            put("bar", new ArrayList<Object>() {{
                add(new EventListener<>(factory, "foo", 3));
            }});
        }});
    }

    @Test
    public void testMutatingWhilePropagationIsStopped() {
        final Boolean[] testLoaded = {false};
        TestEventListener test = new TestEventListener();
        this.dispatcher.addListener("foo", new EventListener<>(test, "postFoo"));
        this.dispatcher.addListener("foo", new EventListener<>(new Closure() {
            public Event preFoo(TestLoadedEvent event) {
                testLoaded[0] = event.getTestLoaded();
                return event;
            }
        }, "preFoo"));

        this.dispatcher.dispatch(new TestLoadedEvent(true), "foo");

        assertTrue(test.postFooInvoked);
        assertFalse(test.preFooInvoked);

        assertSame(0, this.dispatcher.getListenerPriority("foo", new EventListener<>(test, "preFoo")));
        test.preFoo(new TestLoadedEvent(true));
        this.dispatcher.dispatch(new TestLoadedEvent(true), "foo");

        assertTrue(testLoaded[0]);
    }*/
}