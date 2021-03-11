package nablarch.core.text.json;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
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

    private JsonSerializer serializer;
    private StringWriter writer = new StringWriter();

    @Before
    public void setup() {
        JsonSerializationManager manager = new JsonSerializationManager();
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

        int[] arrayValue = { 1, 23, 456 };
        assertThat(serializer.isTarget(arrayValue.getClass()), is(true));

        Object booleanValue = true;
        assertThat(serializer.isTarget(booleanValue.getClass()), is(false));
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
}
