package nablarch.core.text.json;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
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
        JsonSerializationManager manager = new BasicJsonSerializationManager();
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
        assertThat(serializer.isTarget(ArrayList.class), is(true));
        assertThat(serializer.isTarget(LinkedList.class), is(true));

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

    @Test
    public void ListのListがシリアライズできること() throws Exception {

        List<List<Integer>> arrayValue = Arrays.asList(
                Arrays.asList(0, 1, 2),
                Arrays.asList(10, 11, 12)
        );

        serializer.serialize(writer, arrayValue);
        assertThat(writer.toString(), is("[[0,1,2],[10,11,12]]"));
    }

    @Test
    public void 配列のListがシリアライズできること() throws Exception {

        List<Integer[]> arrayValue = Arrays.asList(
                new Integer[] {0, 1, 2},
                new Integer[] {10, 11, 12}
        );

        serializer.serialize(writer, arrayValue);
        assertThat(writer.toString(), is("[[0,1,2],[10,11,12]]"));
    }

    @Test
    public void MapのListがシリアライズできること() throws Exception {

        List<Map<String, Integer>> arrayValue = new ArrayList<Map<String, Integer>>();
        arrayValue.add(new HashMap<String, Integer>());
        arrayValue.get(0).put("key0-0", 0);
        arrayValue.get(0).put("key0-1", 1);
        arrayValue.get(0).put("key0-2", 2);
        arrayValue.add(new HashMap<String, Integer>());
        arrayValue.get(1).put("key1-0", 10);
        arrayValue.get(1).put("key1-1", 11);
        arrayValue.get(1).put("key1-2", 12);

        serializer.serialize(writer, arrayValue);
        assertThat(writer.toString(), isJson(allOf(
                withJsonPath("$[0]", hasEntry("key0-0", 0)),
                withJsonPath("$[0]", hasEntry("key0-1", 1)),
                withJsonPath("$[0]", hasEntry("key0-2", 2)),
                withJsonPath("$[1]", hasEntry("key1-0", 10)),
                withJsonPath("$[1]", hasEntry("key1-1", 11)),
                withJsonPath("$[1]", hasEntry("key1-2", 12)))));
    }
}
