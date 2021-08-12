package com.labudzinski.EventDispatcher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

        assertTrue(this.dispatcher.getListeners(preFoo).size() == 1);
        assertTrue(this.dispatcher.getListeners(postFoo).size() == 1);
        assertTrue(this.dispatcher.getListeners().size() == 2);
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
                add(new ClosureRunnable(listener2, "preFoo", -10));
                add(new ClosureRunnable(listener3, "preFoo", 0));
                add(new ClosureRunnable(listener1, "preFoo", 10));
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
                add(new ClosureRunnable(listener1, null, -10));
                add(new ClosureRunnable(listener2));
                add(new ClosureRunnable(listener3, null, 10));
            }});
            put(postFoo, new ArrayList<>() {{
                add(new ClosureRunnable(listener4, null, -10));
                add(new ClosureRunnable(listener5));
                add(new ClosureRunnable(listener6, null, 10));
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

        assertSame(-10, this.dispatcher.getListenerPriority(preFoo, new ClosureRunnable(listener1, null, -10)));
        assertSame(0, this.dispatcher.getListenerPriority(preFoo,  new ClosureRunnable(listener2)));
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
        Closure listener = new Closure() {
            public Event invoke(Event event) {
                System.out.println("OK");
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
        System.out.println("this.listener: " + this.listener.key);
        System.out.println("otherListener: " + otherListener.key);
        this.dispatcher.addListener("post.foo", new ClosureRunnable(this.listener, "postFoo"), 10);
        this.dispatcher.addListener("post.foo", new ClosureRunnable(otherListener, "postFoo"));
        this.dispatcher.dispatch(new Event(), postFoo);
        assertTrue(this.listener.postFooInvoked);
        assertFalse(otherListener.postFooInvoked);
    }
/*
    @Test
    public void testDispatchByPriority() {
        final int[] invoked = {0, 0, 0};
        EventListenerImpl listener1 = new TestEventListener() {
            public TestWithDispatcher invoke() {
                invoked[0] = 1;
                return null;
            }
        };
        EventListenerImpl listener2 = new TestEventListener() {
            public TestWithDispatcher invoke() {
                invoked[1] = 2;
                return null;
            }
        };
        EventListenerImpl listener3 = new TestEventListener() {
            public TestWithDispatcher invoke() {
                invoked[2] = 3;
                return null;
            }
        };

        this.dispatcher.addListener(preFoo, new ClosureRunnable(listener1), -10);
        this.dispatcher.addListener(preFoo, new ClosureRunnable(listener2));
        this.dispatcher.addListener(preFoo, new ClosureRunnable(listener3), 10);
        this.dispatcher.dispatch(new Event(), preFoo);

        assertThat(new int[]{1, 2, 3}).isEqualTo(invoked);
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
        String className = listeners.get(1).getListener().getClass().getTypeName();
        className = className.replace("1", "");
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
        System.out.println( this.dispatcher.getListeners(preFoo));
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
        this.dispatcher.dispatch(new Event(), "test");
        assertEquals("test", listener.name);
        assertSame(this.dispatcher, listener.dispatcher);
    }

    @Test
    public void testWorkaroundForPhpBug62976() throws Throwable {
        ChildEventDispatcher dispatcher = new ChildEventDispatcher();
        EventListener listener = new ClosureRunnable(new TestEventListener() {
            public TestWithDispatcher invoke() {
                return null;
            }
        });
        EventListener listener2 = new ClosureRunnable(new TestEventListener() {
            public TestWithDispatcher invoke() {
                return null;
            }
        });
        dispatcher.addListener("bug.62976", listener);
        dispatcher.removeListener("bug.62976",listener2);
        System.out.println(dispatcher.getListeners());
        assertTrue(dispatcher.hasListeners("bug.62976"));
    }

    @Test
    public void testHasListenersWhenAddedCallbackListenerIsRemoved() throws Throwable {
        EventListener listener = new ClosureRunnable(new TestEventListener() {
            public TestWithDispatcher invoke() {
                return null;
            }
        });
        this.dispatcher.addListener("foo", listener);
        this.dispatcher.removeListener("foo", listener);
        assertFalse(this.dispatcher.hasListeners());
    }

    @Test
    public void testGetListenersWhenAddedCallbackListenerIsRemoved() throws Throwable {
        EventListener listener = new ClosureRunnable(new TestEventListener() {
            public TestWithDispatcher invoke() {
                return null;
            }
        });
        this.dispatcher.addListener("foo", listener);
        this.dispatcher.removeListener("foo", listener);
        assertThat(new HashMap<>()).isEqualTo(this.dispatcher.getListeners());
    }

    @Test
    public void testHasListenersWithoutEventsReturnsFalseAfterHasListenersWithEventHasBeenCalled()
    {
        assertFalse(this.dispatcher.hasListeners("foo"));
        assertFalse(this.dispatcher.hasListeners());
    }

    @Test
    public void testHasListenersIsLazy()
    {
        final int[] called = {0};
        EventListenerImpl listener = new TestEventListener() {
            public TestWithDispatcher invoke() {
                ++called[0];
                return null;
            }
        };

        this.dispatcher.addListener("foo", new ClosureRunnable(listener));
        assertTrue(this.dispatcher.hasListeners());
        assertTrue(this.dispatcher.hasListeners("foo"));
        assertSame(0, called);
    }

    @Test
    public void testDispatchLazyListener()
    {
        final int[] called = {0};
        TestWithDispatcher dispatcher = new TestWithDispatcher();
        EventListenerImpl factory = new TestEventListener() {
            public TestWithDispatcher invoke() {
                ++called[0];

                return dispatcher;
            }
        };

        this.dispatcher.addListener("foo", new ClosureRunnable(factory, "foo"));
        assertSame(0, called);
        this.dispatcher.dispatch(new Event(), "foo");
        assertFalse(dispatcher.invoked);
        this.dispatcher.dispatch(new Event(), "foo");
        assertSame(1, called);

        this.dispatcher.addListener("bar",  new ClosureRunnable(factory));
        assertSame(1, called);
        this.dispatcher.dispatch(new Event(), "bar");
        assertTrue(dispatcher.invoked);
        this.dispatcher.dispatch(new Event(), "bar");
        assertSame(2, called);
    }

    public void testRemoveFindsLazyListeners() throws Throwable {
        TestWithDispatcher test = new TestWithDispatcher();
        EventListenerImpl factory = new TestEventListener() {
            public TestWithDispatcher invoke() {
                return test;
            }
        };

        this.dispatcher.addListener("foo", new ClosureRunnable(factory, "foo"));
        assertTrue(this.dispatcher.hasListeners("foo"));
        this.dispatcher.removeListener("foo", new ClosureRunnable(test, "foo"));
        assertFalse(this.dispatcher.hasListeners("foo"));
        this.dispatcher.addListener("foo", new ClosureRunnable(test, "foo"));
        assertTrue(this.dispatcher.hasListeners("foo"));
        this.dispatcher.removeListener("foo", new ClosureRunnable(factory, "foo"));
        assertFalse(this.dispatcher.hasListeners("foo"));
    }

    @Test
    public void testPriorityFindsLazyListeners() throws Throwable {
        TestWithDispatcher test = new TestWithDispatcher();
        EventListenerObject factory = new ClosureRunnableObject() {
            @Override
            public String concat(String a, String b) {
                return "Hello, GFGians!";
            }
        };

        this.dispatcher.addListener("foo", new ClosureRunnable(factory, "foo"), 3);
        assertSame(3, this.dispatcher.getListenerPriority("foo", new ClosureRunnable(test, "foo")));
        this.dispatcher.removeListener("foo", new ClosureRunnable(factory, "foo"));

        this.dispatcher.addListener("foo", new ClosureRunnable(test, "foo"), 5);
        System.out.println(this.dispatcher.getListenerPriority("foo", new ClosureRunnable(factory, "foo")));
        //assertSame(5, this.dispatcher.getListenerPriority("foo", new ClosureRunnable(factory, "foo")));
    }

    @Test
    public void testGetLazyListeners() throws Throwable {
        TestWithDispatcher test = new TestWithDispatcher();
        EventListenerImpl factory = new TestEventListener() {
            public TestWithDispatcher invoke() {
                return test;
            }
        };

        this.dispatcher.addListener("foo",  new ClosureRunnable(factory, "foo"), 3);
        assertThat(this.dispatcher.getListeners("foo")).usingRecursiveComparison().isEqualTo(new HashMap<String, Object>() {{
            put("foo", new ArrayList<>() {{
                add(new ClosureRunnable(factory, "foo"));
            }});
        }});

        this.dispatcher.removeListener("foo",  new ClosureRunnable(test, "foo"));
        this.dispatcher.addListener("bar",  new ClosureRunnable(factory, "foo"), 3);
        assertThat(this.dispatcher.getListeners()).usingRecursiveComparison().isEqualTo(new HashMap<String, Object>() {{
            put("bar", new ArrayList<>() {{
                add(new ClosureRunnable(factory, "foo"));
            }});
            put("foo", new ArrayList<>() {{
                add(new ClosureRunnable(factory, "foo"));
            }});
        }});
    }*/

    /*public void testMutatingWhilePropagationIsStopped()
    {
        testLoaded = false;
        test = new TestEventListener();
        this.dispatcher.addListener("foo", [test, "postFoo"]);
        this.dispatcher.addListener("foo", [void () use (test, &testLoaded) {
            testLoaded = true;

            return test;
        }, "preFoo"]);

        this.dispatcher.dispatch(new Event(), "foo");

        this.assertTrue(test.postFooInvoked);
        this.assertFalse(test.preFooInvoked);

        this.assertsame(0, this.dispatcher.getListenerPriority("foo", [test, "preFoo"]));

        test.preFoo(new Event());
        this.dispatcher.dispatch(new Event(), "foo");

        this.assertTrue(testLoaded);
    }*/
}