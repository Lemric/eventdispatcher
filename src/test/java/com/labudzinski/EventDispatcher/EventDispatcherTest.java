package com.labudzinski.EventDispatcher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
        this.listener.name = "listener";
    }

    @AfterEach
    void tearDown() {
        this.dispatcher = null;
        this.listener = null;
    }

    @Test
    public void testInitialState() {
        assertEquals(new ArrayList(), this.dispatcher.getListeners());
        assertFalse(this.dispatcher.hasListeners(preFoo));
        assertFalse(this.dispatcher.hasListeners(postFoo));
    }

    @Test
    public void testAddListener() {
        this.dispatcher.addListener(preFoo, new EventListener(this.listener, "preFoo"));
        this.dispatcher.addListener(postFoo, new EventListener(this.listener, "postFoo"));

        assertTrue(this.dispatcher.hasListeners());
        assertTrue(this.dispatcher.hasListeners(preFoo));
        assertTrue(this.dispatcher.hasListeners(postFoo));

        assertTrue(this.dispatcher.getListeners(preFoo).size() == 1);
        assertTrue(this.dispatcher.getListeners(postFoo).size() == 1);
        assertTrue(this.dispatcher.getListeners().size() == 2);
    }

    @Test
    public void testGetListenersSortsByPriority() {
        TestEventListener listener1 = new TestEventListener();
        TestEventListener listener2 = new TestEventListener();
        TestEventListener listener3 = new TestEventListener();
        listener1.name = "1";
        listener2.name = "2";
        listener3.name = "3";

        this.dispatcher.addListener("pre.foo", new EventListener(listener1, "preFoo"), -10);
        this.dispatcher.addListener("pre.foo", new EventListener(listener2, "preFoo"), 10);
        this.dispatcher.addListener("pre.foo", new EventListener(listener3, "preFoo"));

        ArrayList<ArrayList<EventListenerInterface>> expected = new ArrayList<ArrayList<EventListenerInterface>>() {{
            add(new ArrayList<>() {{
                add(new EventListener(listener1, "preFoo"));
            }});
            add(new ArrayList<>() {{
                add(new EventListener(listener3, "preFoo"));
            }});
            add(new ArrayList<>() {{
                add(new EventListener(listener2, "preFoo"));
            }});
        }};

        assertThat(this.dispatcher.getListeners("pre.foo"))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    public void testGetAllListenersSortsByPriority() {
        TestEventListener listener1 = new TestEventListener();
        TestEventListener listener2 = new TestEventListener();
        TestEventListener listener3 = new TestEventListener();
        TestEventListener listener4 = new TestEventListener();
        TestEventListener listener5 = new TestEventListener();
        TestEventListener listener6 = new TestEventListener();

        this.dispatcher.addListener(preFoo, new EventListener(listener1), -10);
        this.dispatcher.addListener(preFoo, new EventListener(listener2));
        this.dispatcher.addListener(preFoo, new EventListener(listener3), 10);
        this.dispatcher.addListener(postFoo, new EventListener(listener4), -10);
        this.dispatcher.addListener(postFoo, new EventListener(listener5));
        this.dispatcher.addListener(postFoo, new EventListener(listener6), 10);

        ArrayList<ArrayList<EventListenerInterface>> expected = new ArrayList<>() {{
            add(new ArrayList<>() {{
                add(new EventListener(listener1));
                add(new EventListener(listener2));
                add(new EventListener(listener3));
            }});
            add(new ArrayList<>() {{
                add(new EventListener(listener4));
                add(new EventListener(listener5));
                add(new EventListener(listener6));
            }});
        }};

        ArrayList<String> expectedList = new ArrayList<>();
        for (ArrayList<EventListenerInterface> eventListenerInterfaces : expected) {
            for (EventListenerInterface eventListenerInterface : eventListenerInterfaces) {
                expectedList.add(eventListenerInterface.getListener().getUuid().toString());
            }
        }
        assertThat(this.dispatcher.getListenersAsArrayList())
                .usingRecursiveComparison()
                .isEqualTo(expectedList);
    }

    @Test
    public void testGetListenerPriority() {
        TestEventListener listener1 = new TestEventListener();
        TestEventListener listener2 = new TestEventListener();

        this.dispatcher.addListener(preFoo, new EventListener(listener1), -10);
        this.dispatcher.addListener(preFoo, new EventListener(listener2));

        assertSame(-10, this.dispatcher.getListenerPriority(preFoo, listener1));
        assertSame(0, this.dispatcher.getListenerPriority(preFoo, listener2));
        assertNull(this.dispatcher.getListenerPriority(preBar, listener2));
        assertNull(this.dispatcher.getListenerPriority(preFoo, new Object()));
    }

    @Test
    public void testDispatch() {
        this.dispatcher.addListener("pre.foo", new EventListener(this.listener, "preFoo"));
        this.dispatcher.addListener("post.foo", new EventListener(this.listener, "postFoo"));
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
        EventListenerImpl listener = new TestEventListener() {
            public void __invoke() {
                invoked[0] += 1;
            }
        };
        this.dispatcher.addListener("pre.foo", new EventListener(listener));
        this.dispatcher.addListener("post.foo", new EventListener(listener));
        this.dispatcher.dispatch(new Event(), preFoo);

        assertEquals(1, invoked[0]);
    }

    @Test
    public void testStopEventPropagation() {
        TestEventListener otherListener = new TestEventListener();
        otherListener.name = "otherListener";
        this.dispatcher.addListener("post.foo", new EventListener(this.listener, "postFoo"), -10);
        this.dispatcher.addListener("post.foo", new EventListener(otherListener, "postFoo"));
        this.dispatcher.dispatch(new Event(), postFoo);
        assertTrue(this.listener.postFooInvoked);
        assertFalse(otherListener.postFooInvoked);
    }

    @Test
    public void testDispatchByPriority() {
        final int[] invoked = {0, 0, 0};
        EventListenerImpl listener1 = new TestEventListener() {
            public void __invoke() {
                invoked[0] = 1;
            }
        };
        EventListenerImpl listener2 = new TestEventListener() {
            public void __invoke() {
                invoked[1] = 2;
            }
        };
        EventListenerImpl listener3 = new TestEventListener() {
            public void __invoke() {
                invoked[2] = 3;
            }
        };

        this.dispatcher.addListener(preFoo, new EventListener(listener1), -10);
        this.dispatcher.addListener(preFoo, new EventListener(listener2));
        this.dispatcher.addListener(preFoo, new EventListener(listener3), 10);
        this.dispatcher.dispatch(new Event(), preFoo);

        assertThat(new int[]{1, 2, 3}).isEqualTo(invoked);
    }

    @Test
    public void testRemoveListener() throws Throwable {
        this.dispatcher.addListener(preBar, new EventListener(this.listener));
        assertTrue(this.dispatcher.hasListeners(preBar));
        this.dispatcher.removeListener(preBar, new EventListener(this.listener));
        assertFalse(this.dispatcher.hasListeners(preBar));
        this.dispatcher.removeListener("notExists", new EventListener(this.listener));
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

        ArrayList<ArrayList<EventListenerInterface>> listeners = this.dispatcher.getListeners(preFoo);
        assertTrue(this.dispatcher.hasListeners(preFoo));
        assertEquals(2, listeners.size());
        String className = listeners.get(1).get(0).getListener().getClass().getTypeName();
        className = className.replace("$1", "");
        assertEquals(className, TestEventSubscriberWithPriorities.class.getName());
    }

    @Test
    public void testAddSubscriberWithMultipleListeners() {
        TestEventSubscriberWithMultipleListeners eventSubscriber = new TestEventSubscriberWithMultipleListeners();
        this.dispatcher.addSubscriber(eventSubscriber);

        ArrayList<ArrayList<EventListenerInterface>> listeners = this.dispatcher.getListeners(preFoo);
        assertTrue(this.dispatcher.hasListeners(preFoo));
        assertEquals(2, listeners.size());
        assertEquals("preFoo2", listeners.get(1).get(0).getMethod());
    }

    @Test
    public void testRemoveSubscriber() throws Throwable {
        TestEventSubscriber eventSubscriber = new TestEventSubscriber();
        this.dispatcher.addSubscriber(eventSubscriber);
//        assertTrue(this.dispatcher.hasListeners(preFoo));
//        assertTrue(this.dispatcher.hasListeners(postFoo));
        this.dispatcher.removeSubscriber(eventSubscriber);
        assertFalse(this.dispatcher.hasListeners(preFoo));
        assertFalse(this.dispatcher.hasListeners(postFoo));
    }


}