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
 * {@link ObjectToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class ObjectToJsonSerializerTest {

    private JsonSerializer serializer;
    private StringWriter writer = new StringWriter();

    @Before
    public void setup() {
        serializer = new ObjectToJsonSerializer();
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);
    }

    @After
    public void teardown() throws IOException {
        writer.close();
    }

    @Test
    public void 対象オブジェクトに寄らず常にtrueを返すこと() throws Exception {

        Object booleanValue = true;
        assertThat(serializer.isTarget(booleanValue.getClass()), is(true));

        Object intValue = 0;
        assertThat(serializer.isTarget(intValue.getClass()), is(true));

    }

    @Test
    public void toStringの結果でstringとしてシリアライズできること() throws Exception {

        serializer.serialize(writer, true);
        assertThat(writer.toString(), is("\"true\""));
    }

}
