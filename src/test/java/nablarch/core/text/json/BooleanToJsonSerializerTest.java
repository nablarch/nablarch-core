package nablarch.core.text.json;

import org.junit.Test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * {@link BooleanToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class BooleanToJsonSerializerTest {

    @Test
    public void 対象オブジェクトの判定ができること() throws Exception {

        JsonSerializer serializer = new BooleanToJsonSerializer();
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        Object booleanValue = true;
        assertThat(serializer.isTarget(booleanValue.getClass()), is(true));

        Object intValue = 0;
        assertThat(serializer.isTarget(intValue.getClass()), is(false));

    }

    @Test
    public void trueがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new BooleanToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            serializer.serialize(writer, true);
            assertThat(writer.toString(), is("true"));
        } finally {
            writer.close();
        }
    }

    @Test
    public void falseがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new BooleanToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            serializer.serialize(writer, false);
            assertThat(writer.toString(), is("false"));
        } finally {
            writer.close();
        }
    }


}
