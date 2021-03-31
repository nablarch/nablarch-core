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

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.Is.is;

/**
 * {@link ArrayToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class ArrayToJsonSerializerTest {

    private JsonSerializer serializer;
    private StringWriter writer = new StringWriter();

    @Before
    public void setup() {
        JsonSerializationManager manager = new BasicJsonSerializationManager();
        manager.initialize();

        serializer = new ArrayToJsonSerializer(manager);
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

        assertThat(serializer.isTarget(int[].class), is(true));
        assertThat(serializer.isTarget(int[][].class), is(true));
        assertThat(serializer.isTarget(String[].class), is(true));

        assertThat(serializer.isTarget(int.class), is(false));
        assertThat(serializer.isTarget(String.class), is(false));
        assertThat(serializer.isTarget(List.class), is(false));
    }

    @Test
    public void intの配列がシリアライズできること() throws Exception {

        int[] arrayValue = { 1, 23, 456 };

        serializer.serialize(writer, arrayValue);
        assertThat(writer.toString(), is("[1,23,456]"));
    }

    @Test
    public void 空の配列がシリアライズできること() throws Exception {

        int[] arrayValue = { };

        serializer.serialize(writer, arrayValue);
        assertThat(writer.toString(), is("[]"));
    }

    @Test
    public void nullを含む配列がシリアライズできること() throws Exception {

        Object[] arrayValue = {null, "foo", 123, null};

        serializer.serialize(writer, arrayValue);
        assertThat(writer.toString(), is("[null,\"foo\",123,null]"));
    }

    @Test
    public void 二次元配列がシリアライズできること() throws Exception {

        int[][] arrayValue = { {0, 1, 2}, {10, 11, 12}};

        serializer.serialize(writer, arrayValue);
        assertThat(writer.toString(), is("[[0,1,2],[10,11,12]]"));
    }

    @Test
    public void Listの配列がシリアライズできること() throws Exception {

        List<Integer>[] arrayValue = new List[2];
        arrayValue[0] = new ArrayList<Integer>();
        arrayValue[0].add(0);
        arrayValue[0].add(1);
        arrayValue[0].add(2);
        arrayValue[1] = new ArrayList<Integer>();
        arrayValue[1].add(10);
        arrayValue[1].add(11);
        arrayValue[1].add(12);

        serializer.serialize(writer, arrayValue);
        assertThat(writer.toString(), is("[[0,1,2],[10,11,12]]"));
    }

    @Test
    public void Mapの配列がシリアライズできること() throws Exception {

        Map<String, Integer>[] arrayValue = new Map[2];
        arrayValue[0] = new HashMap<String, Integer>();
        arrayValue[0].put("key0-0", 0);
        arrayValue[0].put("key0-1", 1);
        arrayValue[0].put("key0-2", 2);
        arrayValue[1] = new HashMap<String, Integer>();
        arrayValue[1].put("key1-0", 10);
        arrayValue[1].put("key1-1", 11);
        arrayValue[1].put("key1-2", 12);

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
