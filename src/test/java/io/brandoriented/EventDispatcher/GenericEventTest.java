package io.brandoriented.EventDispatcher;

import io.brandoriented.EventDispatcher.exceptions.BadMethodCallException;
import io.brandoriented.EventDispatcher.exceptions.InvalidArgumentException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GenericEventTest {

    private Object subject;
    private GenericEvent event;

    @BeforeEach
    void setUp() {
        this.subject = new Object();
        this.event = new GenericEvent(this.subject, new HashMap<String, Object>() {{
            put("name", "Event");
        }});
    }

    @AfterEach
    void tearDown() {
        this.subject = null;
        this.event = null;
    }

    @Test
    public void testConstruct() {
        assertThat(this.event)
                .usingRecursiveComparison()
                .isEqualTo(
                        new GenericEvent(this.subject, new HashMap<String, Object>() {{
                            put("name", "Event");
                        }})
                );
    }

    @Test
    public void testGetArguments() {
        assertEquals(new HashMap<String, Object>() {{
            put("name", "Event");
        }}, this.event.getArguments());
    }

    @Test
    public void testSetArguments() {
        Event result = this.event.setArguments(new HashMap<String, Object>() {{
            put("foo", "bar");
        }});
        assertEquals(new HashMap<String, Object>() {{
            put("foo", "bar");
        }}, this.event.getArguments());
        assertEquals(this.event, result);
    }

    @Test
    public void testSetArgument()
    {
        Event result = this.event.setArgument("foo2", "bar2");
        assertEquals(new HashMap<String, Object>() {{
            put("name", "Event");
            put("foo2", "bar2");
        }}, this.event.getArguments());
        assertEquals(this.event, result);
    }

    @Test
    public void testGetArgument() throws InvalidArgumentException {
        // test getting key
        assertEquals("Event", this.event.getArgument("name"));
    }

    @Test
    public void testGetArgException() {

        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> {
            this.event.getArgument("nameNotExist");
        });
        assertEquals(exception.getClass(), InvalidArgumentException.class);
    }

    @Test
    public void testOffsetGet() throws InvalidArgumentException {
        // test getting key
        assertEquals("Event", this.event.getArgument("name"));

        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> {
            this.event.getArgument("nameNotExist");
        });
        assertEquals(exception.getClass(), InvalidArgumentException.class);
    }

    @Test
    public void testOffsetSet()
    {
        this.event.setArgument("foo2", "bar2");
        assertEquals(new HashMap<String, Object>() {{
            put("name", "Event");
            put("foo2", "bar2");
        }}, this.event.getArguments());
    }

    @Test
    public void testOffsetUnset()
    {
        this.event.offsetUnset("name");
        assertEquals(this.event.getArguments(), new HashMap<String, Object>());
    }

    @Test
    public void testOffsetIsset()
    {
        assertTrue(this.event.offsetExists("name"));
        assertFalse(this.event.offsetExists("nameNotExist"));
    }

    @Test
    public void testHasArgument()
    {
        assertTrue(this.event.hasArgument("name"));
        assertFalse(this.event.hasArgument("nameNotExist"));
    }

    @Test
    public void testGetSubject()
    {
        assertSame(this.subject, this.event.getSubject());
    }
}