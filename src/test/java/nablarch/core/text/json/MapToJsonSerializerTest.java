package nablarch.core.text.json;

import org.junit.Test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * {@link MapToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class MapToJsonSerializerTest {

    @Test
    public void 対象オブジェクトの判定ができること() throws Exception {

        JsonSerializationManager manager = new JsonSerializationManager();
        JsonSerializer serializer = new MapToJsonSerializer(manager);
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        Map<String, String> mapValue = new HashMap<String, String>();
        assertThat(serializer.isTarget(mapValue.getClass()), is(true));

        Object booleanValue = true;
        assertThat(serializer.isTarget(booleanValue.getClass()), is(false));
    }

    @Test
    public void Mapがシリアライズできること() throws Exception {
        JsonSerializationManager manager = new JsonSerializationManager();
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        manager.initialize(settings);

        StringWriter writer = new StringWriter();
        try {
            Map<String, String> mapValue = new HashMap<String, String>();
            mapValue.put("key1","value1");
            mapValue.put("key2","value2");
            mapValue.put("key3","value3");

            JsonSerializer serializer = manager.getSerializer(mapValue);
            assertThat(serializer.getClass() == MapToJsonSerializer.class, is(true));

            serializer.serialize(writer, mapValue);
            assertThat(writer.toString(), isJson(allOf(
                    withJsonPath("$", hasEntry("key1", "value1")),
                    withJsonPath("$", hasEntry("key2", "value2")),
                    withJsonPath("$", hasEntry("key3", "value3")))));
        } finally {
            writer.close();
        }

        writer = new StringWriter();
        try {
            Map<String, Integer> mapValue = new HashMap<String, Integer>();
            mapValue.put("key1",123);
            mapValue.put("key2",45);
            mapValue.put("key3",678);

            JsonSerializer serializer = manager.getSerializer(mapValue);
            assertThat(serializer.getClass() == MapToJsonSerializer.class, is(true));

            serializer.serialize(writer, mapValue);
            assertThat(writer.toString(), isJson(allOf(
                    withJsonPath("$", hasEntry("key1", 123)),
                    withJsonPath("$", hasEntry("key2", 45)),
                    withJsonPath("$", hasEntry("key3", 678)))));
        } finally {
            writer.close();
        }
    }

    @Test
    public void 空のMapがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializationManager manager = new JsonSerializationManager();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            manager.initialize(settings);

            Map<String, String> mapValue = new HashMap<String, String>();

            JsonSerializer serializer = manager.getSerializer(mapValue);
            assertThat(serializer.getClass() == MapToJsonSerializer.class, is(true));

            serializer.serialize(writer, mapValue);
            assertThat(writer.toString(), is("{}"));
        } finally {
            writer.close();
        }

    }

    @Test
    public void nullを無視してMapがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializationManager manager = new JsonSerializationManager();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            manager.initialize(settings);

            Map<String, String> mapValue = new HashMap<String, String>();
            mapValue.put("key1",null);
            mapValue.put("key2","value2");
            mapValue.put("key3",null);

            JsonSerializer serializer = manager.getSerializer(mapValue);
            assertThat(serializer.getClass() == MapToJsonSerializer.class, is(true));

            serializer.serialize(writer, mapValue);
            assertThat(writer.toString(), isJson(allOf(
                    withoutJsonPath("$.key1"),
                    withJsonPath("$", hasEntry("key2", "value2")),
                    withoutJsonPath("$.key3"))));
        } finally {
            writer.close();
        }
    }

    @Test
    public void 挿入処理を含むMapがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializationManager manager = new JsonSerializationManager();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            manager.initialize(settings);

            Map<String, Object> mapValue = new LinkedHashMap<String, Object>();
            mapValue.put("key1","value1");
            mapValue.put("key2",new InplaceMapEntries("\"keyA\":1,\"keyB\":2"));
            mapValue.put("key3","value3");

            JsonSerializer serializer = manager.getSerializer(mapValue);
            assertThat(serializer.getClass() == MapToJsonSerializer.class, is(true));

            serializer.serialize(writer, mapValue);
            assertThat(writer.toString(), isJson(allOf(
                    withJsonPath("$", hasEntry("key1", "value1")),
                    withoutJsonPath("$.key2"),
                    withJsonPath("$", hasEntry("key3", "value3")),
                    withJsonPath("$", hasEntry("keyA", 1)),
                    withJsonPath("$", hasEntry("keyB", 2)))));
        } finally {
            writer.close();
        }
    }

    @Test
    public void 先頭への挿入処理を含むMapがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializationManager manager = new JsonSerializationManager();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            manager.initialize(settings);

            Map<String, Object> mapValue = new LinkedHashMap<String, Object>();
            mapValue.put("key1",new InplaceMapEntries("\"keyA\":1,\"keyB\":2"));
            mapValue.put("key2","value2");
            mapValue.put("key3","value3");

            JsonSerializer serializer = manager.getSerializer(mapValue);
            assertThat(serializer.getClass() == MapToJsonSerializer.class, is(true));

            serializer.serialize(writer, mapValue);
            assertThat(writer.toString(), isJson(allOf(
                    withoutJsonPath("$.key1"),
                    withJsonPath("$", hasEntry("key2", "value2")),
                    withJsonPath("$", hasEntry("key3", "value3")),
                    withJsonPath("$", hasEntry("keyA", 1)),
                    withJsonPath("$", hasEntry("keyB", 2)))));
        } finally {
            writer.close();
        }
    }

    @Test
    public void 挿入部分がWSの場合に無視してシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializationManager manager = new JsonSerializationManager();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            manager.initialize(settings);

            Map<String, Object> mapValue = new HashMap<String, Object>();
            mapValue.put("key1","value1");
            mapValue.put("key2",new InplaceMapEntries(" \t\r\n"));
            mapValue.put("key3","value3");

            JsonSerializer serializer = manager.getSerializer(mapValue);
            assertThat(serializer.getClass() == MapToJsonSerializer.class, is(true));

            serializer.serialize(writer, mapValue);
            assertThat(writer.toString(), isJson(allOf(
                    withJsonPath("$", hasEntry("key1", "value1")),
                    withoutJsonPath("$.key2"),
                    withJsonPath("$", hasEntry("key3", "value3")))));
        } finally {
            writer.close();
        }
    }

    @Test
    public void Nullシリアライザを使用したシリアライズができること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializationManager manager = new JsonSerializationManager() {
                protected void enlistSerializer(JsonSerializationSettings settings) {
                    addSerializer(new StringToJsonSerializer());
                    addSerializer(new DateToJsonSerializer());
                    addSerializer(new CustomMapToJsonSerializer(this));
                    addSerializer(new ListToJsonSerializer(this));
                    addSerializer(new ArrayToJsonSerializer(this));
                    addSerializer(new NumberToJsonSerializer());
                    addSerializer(new BooleanToJsonSerializer());
                    addSerializer(new ObjectToJsonSerializer());
                }
            };
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            manager.initialize(settings);

            Map<String, String> mapValue = new HashMap<String, String>();
            mapValue.put("key1",null);
            mapValue.put("key2", "value2");
            mapValue.put("key3",null);

            JsonSerializer serializer = manager.getSerializer(mapValue);

            serializer.serialize(writer, mapValue);
            assertThat(writer.toString(), isJson(allOf(
                    withJsonPath("$", hasEntry("key1", null)),
                    withJsonPath("$", hasEntry("key2", "value2")),
                    withJsonPath("$", hasEntry("key3", null)))));
        } finally {
            writer.close();
        }
    }

    @Test
    public void nameが不正な場合は項目ごとスキップすること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializationManager manager = new JsonSerializationManager();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            manager.initialize(settings);

            Map<Object, String> mapValue = new HashMap<Object, String>();
            mapValue.put(null,"value1");
            mapValue.put("key2","value2");
            mapValue.put(123,"value3");

            JsonSerializer serializer = manager.getSerializer(mapValue);
            assertThat(serializer.getClass() == MapToJsonSerializer.class, is(true));

            serializer.serialize(writer, mapValue);
            assertThat(writer.toString(), is("{\"key2\":\"value2\"}"));
        } finally {
            writer.close();
        }
    }

    /**
     * nullを出力するMapシリアライザ。
     */
    public class CustomMapToJsonSerializer extends MapToJsonSerializer {

        private JsonSerializer nullSerializer = null;

        /** コンストラクタ。 */
        public CustomMapToJsonSerializer(JsonSerializationManager manager) {
            super(manager);
        }

        /** {@inheritDoc} */
        protected JsonSerializer getNullSerializer() {
            if (nullSerializer == null) {
                nullSerializer = getJsonSerializationManager().getSerializer(null);
            }
            return nullSerializer;
        }
    }

}
