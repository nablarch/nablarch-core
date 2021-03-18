package nablarch.core.text.json;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withoutJsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.Is.is;

/**
 * {@link MapToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class MapToJsonSerializerTest {

    private JsonSerializationManager manager;
    private JsonSerializer serializer;
    private StringWriter writer = new StringWriter();

    @Before
    public void setup() {
        manager = new BasicJsonSerializationManager();
        manager.initialize();

        serializer = new MapToJsonSerializer(manager);
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

        assertThat(serializer.isTarget(Map.class), is(true));
        assertThat(serializer.isTarget(HashMap.class), is(true));
        assertThat(serializer.isTarget(TreeMap.class), is(true));

        assertThat(serializer.isTarget(Integer.class), is(false));
    }

    @Test
    public void Mapがシリアライズできること() throws Exception {

        Map<String, Object> intMapValue = new HashMap<String, Object>();
        intMapValue.put("key1",123);
        intMapValue.put("key2","45");
        intMapValue.put("key3",678);

        serializer.serialize(writer, intMapValue);
        assertThat(writer.toString(), isJson(allOf(
                withJsonPath("$", hasEntry("key1", 123)),
                withJsonPath("$", hasEntry("key2", "45")),
                withJsonPath("$", hasEntry("key3", 678)))));
    }

    @Test
    public void 空のMapがシリアライズできること() throws Exception {

        Map<String, String> mapValue = new HashMap<String, String>();

        serializer.serialize(writer, mapValue);
        assertThat(writer.toString(), is("{}"));
    }

    @Test
    public void nullを無視してMapがシリアライズできること() throws Exception {

        Map<String, String> mapValue = new HashMap<String, String>();
        mapValue.put("key1",null);
        mapValue.put("key2","value2");
        mapValue.put("key3",null);

        serializer.serialize(writer, mapValue);
        assertThat(writer.toString(), isJson(allOf(
                withoutJsonPath("$.key1"),
                withJsonPath("$", hasEntry("key2", "value2")),
                withoutJsonPath("$.key3"))));
    }

    @Test
    public void 挿入処理を含むMapがシリアライズできること() throws Exception {

        Map<String, Object> mapValue = new LinkedHashMap<String, Object>();
        mapValue.put("key1","value1");
        mapValue.put("key2",new RawJsonObjectMembers("\"keyA\":1,\"keyB\":2"));
        mapValue.put("key3","value3");

        serializer.serialize(writer, mapValue);
        assertThat(writer.toString(), isJson(allOf(
                withJsonPath("$", hasEntry("key1", "value1")),
                withoutJsonPath("$.key2"),
                withJsonPath("$", hasEntry("key3", "value3")),
                withJsonPath("$", hasEntry("keyA", 1)),
                withJsonPath("$", hasEntry("keyB", 2)))));
    }

    @Test
    public void 先頭への挿入処理を含むMapがシリアライズできること() throws Exception {

        Map<String, Object> mapValue = new LinkedHashMap<String, Object>();
        mapValue.put("key1",new RawJsonObjectMembers("\"keyA\":1,\"keyB\":2"));
        mapValue.put("key2","value2");
        mapValue.put("key3","value3");

        serializer.serialize(writer, mapValue);
        assertThat(writer.toString(), isJson(allOf(
                withoutJsonPath("$.key1"),
                withJsonPath("$", hasEntry("key2", "value2")),
                withJsonPath("$", hasEntry("key3", "value3")),
                withJsonPath("$", hasEntry("keyA", 1)),
                withJsonPath("$", hasEntry("keyB", 2)))));
    }

    @Test
    public void 挿入部分がWSの場合に無視してシリアライズできること() throws Exception {

        Map<String, Object> mapValue = new HashMap<String, Object>();
        mapValue.put("key1","value1");
        mapValue.put("key2",new RawJsonObjectMembers(" \t\r\n"));
        mapValue.put("key3","value3");

        serializer.serialize(writer, mapValue);
        assertThat(writer.toString(), isJson(allOf(
                withJsonPath("$", hasEntry("key1", "value1")),
                withoutJsonPath("$.key2"),
                withJsonPath("$", hasEntry("key3", "value3")))));
    }

    @Test
    public void ignoreNullValueMemberの設定でnullを出力できること() throws Exception {

        JsonSerializer serializer = new MapToJsonSerializer(manager);
        Map<String,String> map = new HashMap<String, String>();
        map.put("ignoreNullValueMember", "false");
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        Map<String, String> mapValue = new HashMap<String, String>();
        mapValue.put("key1",null);
        mapValue.put("key2", "value2");
        mapValue.put("key3",null);

        serializer.serialize(writer, mapValue);
        assertThat(writer.toString(), isJson(allOf(
                withJsonPath("$", hasEntry("key1", null)),
                withJsonPath("$", hasEntry("key2", "value2")),
                withJsonPath("$", hasEntry("key3", null)))));
    }

    @Test
    public void nameがStringでない場合は項目ごとスキップすること() throws Exception {

        Map<Object, String> mapValue = new HashMap<Object, String>();
        mapValue.put(null,"value1");
        mapValue.put("key2","value2");
        mapValue.put(123,"value3");

        serializer.serialize(writer, mapValue);
        assertThat(writer.toString(), is("{\"key2\":\"value2\"}"));
    }

}
