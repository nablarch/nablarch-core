package nablarch.core.text.json;

import org.junit.Test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * {@link ListToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class ListToJsonSerializerTest {

    @Test
    public void 対象オブジェクトの判定ができること() throws Exception {

        JsonSerializationManager manager = new JsonSerializationManager();
        JsonSerializer serializer = new ListToJsonSerializer(manager);
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        List<String> listValue = new ArrayList<String>();
        listValue.add("foo");
        listValue.add("bar");
        listValue.add("baz");
        assertThat(serializer.isTarget(listValue.getClass()), is(true));

        Object booleanValue = true;
        assertThat(serializer.isTarget(booleanValue.getClass()), is(false));
    }

    @Test
    public void Listがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializationManager manager = new JsonSerializationManager();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            manager.initialize(settings);

            List<String> listValue = new ArrayList<String>();
            listValue.add("foo");
            listValue.add("bar");
            listValue.add("baz");

            JsonSerializer serializer = manager.getSerializer(listValue);
            assertThat(serializer.getClass() == ListToJsonSerializer.class, is(true));

            serializer.serialize(writer, listValue);
            assertThat(writer.toString(), is("[\"foo\",\"bar\",\"baz\"]"));
        } finally {
            writer.close();
        }
    }

    @Test
    public void 空のListがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializationManager manager = new JsonSerializationManager();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            manager.initialize(settings);

            List<String> listValue = new ArrayList<String>();

            JsonSerializer serializer = manager.getSerializer(listValue);
            assertThat(serializer.getClass() == ListToJsonSerializer.class, is(true));

            serializer.serialize(writer, listValue);
            assertThat(writer.toString(), is("[]"));
        } finally {
            writer.close();
        }
    }

    @Test
    public void nullを含むListがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializationManager manager = new JsonSerializationManager();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            manager.initialize(settings);

            List<String> listValue = new ArrayList<String>();
            listValue.add("foo");
            listValue.add(null);
            listValue.add("baz");
            listValue.add(null);

            JsonSerializer serializer = manager.getSerializer(listValue);
            assertThat(serializer.getClass() == ListToJsonSerializer.class, is(true));

            serializer.serialize(writer, listValue);
            assertThat(writer.toString(), is("[\"foo\",null,\"baz\",null]"));
        } finally {
            writer.close();
        }
    }

    @Test
    public void Object型のListがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializationManager manager = new JsonSerializationManager();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            manager.initialize(settings);

            List<Object> listValue = new ArrayList<Object>();
            listValue.add("foo");
            listValue.add(123);
            listValue.add("baz");

            JsonSerializer serializer = manager.getSerializer(listValue);
            assertThat(serializer.getClass() == ListToJsonSerializer.class, is(true));

            serializer.serialize(writer, listValue);
            assertThat(writer.toString(), is("[\"foo\",123,\"baz\"]"));
        } finally {
            writer.close();
        }
    }

}
