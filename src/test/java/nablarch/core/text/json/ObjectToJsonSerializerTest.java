package nablarch.core.text.json;

import org.junit.Test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * {@link ObjectToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class ObjectToJsonSerializerTest {

    @Test
    public void 対象オブジェクトに寄らず常にtrueを返すこと() throws Exception {

        JsonSerializer serializer = new ObjectToJsonSerializer();
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        Object booleanValue = true;
        assertThat(serializer.isTarget(booleanValue.getClass()), is(true));

        Object intValue = 0;
        assertThat(serializer.isTarget(intValue.getClass()), is(true));

    }

    @Test
    public void toStringの結果でstringとしてシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new ObjectToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            serializer.serialize(writer, true);
            assertThat(writer.toString(), is("\"true\""));
        } finally {
            writer.close();
        }
    }

}