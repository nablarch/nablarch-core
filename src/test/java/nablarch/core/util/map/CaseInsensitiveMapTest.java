package nablarch.core.util.map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

public class CaseInsensitiveMapTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorParamNull() {
        new CaseInsensitiveMap<String>(null);
    }

    @Test
    public void testGetAndPutWorksProperly() {
        Map<String, String> map = new CaseInsensitiveMap<String>(new HashMap<String, String>()) {{
            put("key1", "val1");
            put("KEY2", "val2");
            put("KeY3", "val3");
            put(null, "nullKey");
        }};

        assertEquals("val1", map.get("key1"));
        assertEquals("val1", map.get("KEY1"));
        assertEquals("val1", map.get("keY1"));

        assertEquals("val2", map.get("key2"));
        assertEquals("val2", map.get("KEY2"));
        assertEquals("val2", map.get("keY2"));

        assertEquals("val3", map.put("KEY3", "val3_updated"));
        assertEquals(4, map.size());
        assertEquals("val3_updated", map.get("key3"));

        assertEquals("nullKey", map.get(null));
    }

    @Test
    public void testPutAll() {
        Map<String, String> map = new CaseInsensitiveMap<String>() {{
            put("key1", "val1");
            put("KEY2", "val2");
            put("KeY3", "val3");
        }};

        map.putAll(map);
        assertTrue( map.equals(map) );

        Map<String, String> map2 = new CaseInsensitiveMap<String>() {{
            put("key5", "val5");
            put("KEY6", "val6");
            put("KEY1", "val1");
        }};

        map.putAll(map2);
        assertEquals(5, map.size());
        assertEquals("val5", map.get("key5"));
        assertEquals("val1", map.get("key1"));
    }

    @Test
    public void testContainsValue() {
        Map<String, String> map = new CaseInsensitiveMap<String>() {{
            put("key", "value");
        }};

        assertThat(map.containsValue("value"), is(true));
        assertThat(map.containsValue("VALUE"), is(false));
    }

    @Test
    public void testGetDelegateMapOfType() {
        CaseInsensitiveMap<String> map = new CaseInsensitiveMap<String>() {{
            put("key", "value");
        }};
        assertThat(map.getDelegateMapOfType(LinkedHashMap.class), is(nullValue()));
        assertThat((CaseInsensitiveMap<String>) map.getDelegateMapOfType(CaseInsensitiveMap .class), is(map));
        assertThat(map.getDelegateMapOfType(ConcurrentHashMap.class), is(new ConcurrentHashMap(map)));
    }

    @Test
    public void testEquals() {
        Map<String, String> map1 = new CaseInsensitiveMap<String>() {{
            put("key", "value");
        }};
        Map<String, String> map2 = new CaseInsensitiveMap<String>() {{
            put("key", "value");
        }};

        assertThat(map1.hashCode(), is(map2.hashCode()));
    }

    @Test
    public void testToString() {
        Map<String, String> map = new CaseInsensitiveMap<String>() {{
            put("key1", "val1");
            put("KEY2", "val2");
            put("KeY3", "val3");
        }};

        assertTrue(map.toString().startsWith("{"));
        assertTrue(map.toString().contains("key1=val1"));
        assertTrue(map.toString().contains("key2=val2"));
        assertTrue(map.toString().contains("key3=val3"));
        assertTrue(map.toString().endsWith("}"));

        Map<String, String> map2 = new CaseInsensitiveMap<String>() {{
            put("key1", "val1");
            put("key2", "val2");
            put("key3", "val3");
        }};

        assertEquals(map, map2);
    }
}