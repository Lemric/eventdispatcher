package com.labudzinski.eventdispatcher;

import com.labudzinski.eventdispatcher.events.Dispatcher;
import com.labudzinski.eventdispatchercontracts.Event;
import com.labudzinski.eventdispatchercontracts.EventListenerInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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
    public void testAddListener() {
        Event event = new Event();
        this.dispatcher.addListener(preFoo, listener::preFoo);
        this.dispatcher.addListener(postFoo, (eee) -> listener.postFoo(event));

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


        this.dispatcher.addListener(preFoo, listener1::preFoo, -10);
        this.dispatcher.addListener(preFoo, listener2::preFoo, 10);
        this.dispatcher.addListener(preFoo, listener3::preFoo);

        ArrayList<Callable> expected = new ArrayList<>() {{
            add(listener2::preFoo);
            add(listener3::preFoo);
            add(listener1::preFoo);
        }};

        assertThat(this.dispatcher.getListeners(preFoo))
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

        this.dispatcher.addListener(preFoo, listener1::onEvent, -10);
        this.dispatcher.addListener(preFoo,listener2::onEvent);
        this.dispatcher.addListener(preFoo, listener3::onEvent, 10);
        this.dispatcher.addListener(postFoo, listener4::onEvent, -10);
        this.dispatcher.addListener(postFoo, listener5::onEvent);
        this.dispatcher.addListener(postFoo, listener6::onEvent, 10);

        List<Callable> expected = new ArrayList<>() {{
            add(listener3::onEvent);
            add(listener2::onEvent);
            add(listener1::onEvent);
            add(listener6::onEvent);
            add(listener5::onEvent);
            add(listener4::onEvent);
        }};

        assertThat(this.dispatcher.getListeners())
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    public void testGetListenerPriority() {
        TestEventListener listener1 = new TestEventListener();
        TestEventListener listener2 = new TestEventListener();

        EventListenerInterface listenerA = (event) -> listener1.onEvent();
        this.dispatcher.addListener(preFoo, listenerA, -10);
        EventListenerInterface listenerB = (event) -> listener2.onEvent();
        this.dispatcher.addListener(preFoo, listenerB);
        assertSame(-10, this.dispatcher.getListenerPriority(preFoo, listenerA));
        assertSame(0, this.dispatcher.getListenerPriority(preFoo, listenerB));
        assertNull(this.dispatcher.getListenerPriority(preBar, listener2::onEvent));
        assertNull(this.dispatcher.getListenerPriority(preFoo, (event) -> null));
    }

    @Test
    public void testDispatch() {
        Event event = new Event();
        this.dispatcher.addListener(preFoo, (eee) -> listener.preFoo());
        this.dispatcher.addListener(postFoo, (eee) -> listener.postFoo(event));
        this.dispatcher.dispatch(event, preFoo);
        assertTrue(this.listener.preFooInvoked);
        assertFalse(this.listener.postFooInvoked);
        assertInstanceOf(Event.class, this.dispatcher.dispatch(new Event(), "noevent"));
        assertInstanceOf(Event.class, this.dispatcher.dispatch(new Event(), preFoo));
        Event result = this.dispatcher.dispatch(event, preFoo);
        assertSame(event, result);
    }

    @Test
    public void testDispatchForClosure() {
        final int[] invoked = {0};
        EventListenerInterface listener = (event) -> {
            invoked[0]++;
            return null;
        };
        this.dispatcher.addListener(preFoo, listener);
        this.dispatcher.addListener(postFoo, listener);
        this.dispatcher.dispatch(new Event(), preFoo);

        assertEquals(1, invoked[0]);
    }

    @Test
    public void testStopEventPropagation() {
        Event event = new Event();
        TestEventListener otherListener = new TestEventListener();
        this.dispatcher.addListener(postFoo, (eee) -> listener.postFoo(event), 10);
        this.dispatcher.addListener(postFoo, otherListener::onEvent);
        this.dispatcher.dispatch(new Event(), postFoo);
        assertTrue(this.listener.postFooInvoked);
        assertFalse(otherListener.postFooInvoked);
    }

    @Test
    public void testDispatchByPriority() {
        final ArrayList<Integer> invoked = new ArrayList<>();
        EventListenerInterface listener1 = (EventListenerInterface<Event>) (event) -> {
            invoked.add(1);
            return null;
        };
        EventListenerInterface listener2 = (EventListenerInterface<Event>) (event) -> {
            invoked.add(2);
            return null;
        };
        EventListenerInterface listener3 = (EventListenerInterface<Event>) (event) -> {
            invoked.add(3);
            return null;
        };

        this.dispatcher.addListener(preFoo, listener1, -10);
        this.dispatcher.addListener(preFoo, listener2);
        this.dispatcher.addListener(preFoo, listener3, 10);
        this.dispatcher.dispatch(new Event(), preFoo);
        assertThat(new int[]{3, 2, 1}).isEqualTo(invoked.stream().mapToInt(i -> i).toArray());
    }

    @Test
    public void testRemoveListener() throws Throwable {
        EventListenerInterface listener = (event) -> EventDispatcherTest.this.listener.onEvent();
        this.dispatcher.addListener(preBar, listener);
        assertTrue(this.dispatcher.hasListeners(preBar));
        this.dispatcher.removeListener(preBar, listener);
        assertFalse(this.dispatcher.hasListeners(preBar));
        this.dispatcher.removeListener("notExists", listener);
    }


    @Test
    public void testEventReceivesTheDispatcherInstanceAsArgument() {
        TestWithDispatcher listener = new TestWithDispatcher();
        Dispatcher event = new Dispatcher("test", this.dispatcher);
        this.dispatcher.addListener("test", (eee) -> listener.foo(event));
        assertNull(listener.name);
        assertNull(listener.dispatcher);
        this.dispatcher.dispatch(event, "test");
        assertEquals("test", listener.name);
        assertSame(this.dispatcher, listener.dispatcher);
    }

    @Test
    public void testHasListenersWhenAddedCallbackListenerIsRemoved() throws Throwable {
        EventListenerInterface listener = (event) -> null;
        this.dispatcher.addListener("foo", listener);
        this.dispatcher.removeListener("foo", listener);
        assertFalse(this.dispatcher.hasListeners());
    }

    @Test
    public void testGetListenersWhenAddedCallbackListenerIsRemoved() throws Throwable {

        EventListenerInterface listener = (event) -> null;
        this.dispatcher.addListener("foo", listener);
        this.dispatcher.removeListener("foo", listener);
        assertThat(new ArrayList<>()).isEqualTo(this.dispatcher.getListeners());
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
        EventListenerInterface listener = (event) -> {
            ++called[0];
            return null;
        };

        this.dispatcher.addListener("foo", listener);
        assertTrue(this.dispatcher.hasListeners());
        assertTrue(this.dispatcher.hasListeners("foo"));
        assertEquals(0, called[0]);
    }

    @Test
    public void testDispatchLazyListener() {
        final int[] called = {0};
        TestWithDispatcher dispatcher = new TestWithDispatcher();
        EventListenerInterface factory = (event) -> {
            ++called[0];
            return dispatcher;
        };

        this.dispatcher.addListener("foo", factory);
        assertSame(0, called[0]);
        this.dispatcher.dispatch(new Event(), "foo");
        assertFalse(dispatcher.invoked);
        this.dispatcher.dispatch(new Event(), "foo");
        assertSame(2, called[0]);

        this.dispatcher.addListener("bar", factory);
        assertSame(2, called[0]);
        this.dispatcher.dispatch(new Dispatcher(true), "bar");
        assertTrue(dispatcher.invoked);
        this.dispatcher.dispatch(new Event(), "bar");
        assertSame(4, called[0]);
    }

    @Test
    public void testRemoveFindsLazyListeners() throws Throwable {
        TestWithDispatcher test = new TestWithDispatcher();
        Dispatcher event = new Dispatcher();
        EventListenerInterface factory = (eee) -> test.foo(event);
        this.dispatcher.addListener("foo", factory);
        assertTrue(this.dispatcher.hasListeners("foo"));
        this.dispatcher.removeListener("foo", factory);
        assertFalse(this.dispatcher.hasListeners("foo"));
        this.dispatcher.addListener("foo", factory);
        assertTrue(this.dispatcher.hasListeners("foo"));
        this.dispatcher.removeListener("foo", factory);
        assertFalse(this.dispatcher.hasListeners("foo"));
    }

    @Test
    public void testPriorityFindsLazyListeners() {
        TestWithDispatcher test = new TestWithDispatcher();
        EventListenerInterface factory = (event) -> null;

        this.dispatcher.addListener("foo", factory, 3);
        assertSame(3, this.dispatcher.getListenerPriority("foo", factory));
        this.dispatcher.removeListener("foo", factory);

        this.dispatcher.addListener("foo", factory, 5);
        assertSame(5, this.dispatcher.getListenerPriority("foo", factory));
    }

    @Test
    public void testGetLazyListeners() throws Throwable {
        TestWithDispatcher test = new TestWithDispatcher();
        EventListenerInterface factory = (event) -> null;

        this.dispatcher.addListener("foo", factory, 3);
        assertThat(this.dispatcher.getListeners("foo")).usingRecursiveComparison().isEqualTo(new ArrayList<Object>() {{
            add(factory);
        }});

        this.dispatcher.removeListener("foo", (event) -> null);
        this.dispatcher.addListener("bar", factory, 3);
        assertThat(this.dispatcher.getListeners()).usingRecursiveComparison().isEqualTo(new ArrayList<Object>() {{
            add(factory);
            add(factory);
        }});
    }

    @Test
    public void testMutatingWhilePropagationIsStopped() {
        final Boolean[] testLoaded = {false};
        TestEventListener test = new TestEventListener();
        EventListenerInterface factory = (event) -> test.postFoo((Event) event);
        EventListenerInterface factory2 = (event) -> {
            testLoaded[0] = true;
            return test.preFoo((Event) event);
        };
        this.dispatcher.addListener("foo", factory);
        this.dispatcher.addListener("foo", factory2);

        this.dispatcher.dispatch(new Event(), "foo");

        assertTrue(test.postFooInvoked);
        assertFalse(test.preFooInvoked);

        assertSame(0, this.dispatcher.getListenerPriority("foo", factory2));
        Event event2 = new Event();
        try {
            event2 = (Event) factory2.call(event2);
            this.dispatcher.dispatch(event2, "foo");
        } catch (Exception e) {
            e.printStackTrace();
        }


        assertTrue(testLoaded[0]);
    }
}