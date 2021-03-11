package nablarch.core.text.json;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * {@link ListToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class ListToJsonSerializerTest {

    private JsonSerializer serializer;
    private StringWriter writer = new StringWriter();

    @Before
    public void setup() {
        JsonSerializationManager manager = new JsonSerializationManager();
        manager.initialize();
        serializer = new ListToJsonSerializer(manager);
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);
    }

    @After
    public void teardown() throws IOException {
        writer.close();
    }

    @Test
    public void 対象オブジェクトの判定ができること() throws Exception {

        assertThat(serializer.isTarget(List.class), is(true));

        assertThat(serializer.isTarget(Integer.class), is(false));
        assertThat(serializer.isTarget(String.class), is(false));
        assertThat(serializer.isTarget(String[].class), is(false));
    }

    @Test
    public void Listがシリアライズできること() throws Exception {

        List<String> listValue = new ArrayList<String>();
        listValue.add("foo");
        listValue.add("bar");
        listValue.add("baz");

        serializer.serialize(writer, listValue);
        assertThat(writer.toString(), is("[\"foo\",\"bar\",\"baz\"]"));
    }

    @Test
    public void 空のListがシリアライズできること() throws Exception {

        List<String> listValue = new ArrayList<String>();

        serializer.serialize(writer, listValue);
        assertThat(writer.toString(), is("[]"));
    }

    @Test
    public void nullを含むListがシリアライズできること() throws Exception {

        List<String> listValue = new ArrayList<String>();
        listValue.add("foo");
        listValue.add(null);
        listValue.add("baz");
        listValue.add(null);

        serializer.serialize(writer, listValue);
        assertThat(writer.toString(), is("[\"foo\",null,\"baz\",null]"));
    }

    @Test
    public void Object型のListがシリアライズできること() throws Exception {

        List<Object> listValue = new ArrayList<Object>();
        listValue.add("foo");
        listValue.add(123);
        listValue.add("baz");

        serializer.serialize(writer, listValue);
        assertThat(writer.toString(), is("[\"foo\",123,\"baz\"]"));
    }

}
