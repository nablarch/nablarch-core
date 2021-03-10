package nablarch.core.text.json;

import org.junit.Test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * {@link ArrayToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class ArrayToJsonSerializerTest {

    @Test
    public void 対象オブジェクトの判定ができること() throws Exception {

        JsonSerializationManager manager = new JsonSerializationManager();
        JsonSerializer serializer = new ArrayToJsonSerializer(manager);
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        int[] arrayValue = { 1, 23, 456 };
        assertThat(serializer.isTarget(arrayValue.getClass()), is(true));

        Object booleanValue = true;
        assertThat(serializer.isTarget(booleanValue.getClass()), is(false));
    }

    @Test
    public void intの配列がシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializationManager manager = new JsonSerializationManager();
            manager.initialize();

            int[] arrayValue = { 1, 23, 456 };

            JsonSerializer serializer = manager.getSerializer(arrayValue);
            assertThat(serializer.getClass() == ArrayToJsonSerializer.class, is(true));

            serializer.serialize(writer, arrayValue);
            assertThat(writer.toString(), is("[1,23,456]"));
        } finally {
            writer.close();
        }
    }

    @Test
    public void 空の配列がシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializationManager manager = new JsonSerializationManager();
            manager.initialize();

            int[] arrayValue = { };

            JsonSerializer serializer = manager.getSerializer(arrayValue);
            assertThat(serializer.getClass() == ArrayToJsonSerializer.class, is(true));

            serializer.serialize(writer, arrayValue);
            assertThat(writer.toString(), is("[]"));
        } finally {
            writer.close();
        }
    }

    @Test
    public void nullを含む配列がシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializationManager manager = new JsonSerializationManager();
            manager.initialize();

            Object[] arrayValue = {null, "foo", 123, null};

            JsonSerializer serializer = manager.getSerializer(arrayValue);
            assertThat(serializer.getClass() == ArrayToJsonSerializer.class, is(true));

            serializer.serialize(writer, arrayValue);
            assertThat(writer.toString(), is("[null,\"foo\",123,null]"));
        } finally {
            writer.close();
        }
    }
}
