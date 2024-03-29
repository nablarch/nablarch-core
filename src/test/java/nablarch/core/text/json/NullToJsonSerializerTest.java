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
 * {@link NullToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class NullToJsonSerializerTest {

    private JsonSerializer serializer;
    private StringWriter writer = new StringWriter();

    @Before
    public void setup() {
        serializer = new NullToJsonSerializer();
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

        assertThat(serializer.isTarget(Boolean.class), is(true));
        assertThat(serializer.isTarget(Integer.class), is(true));
        assertThat(serializer.isTarget(String.class), is(true));
        assertThat(serializer.isTarget(Object.class), is(true));
    }

    @Test
    public void nullがシリアライズできること() throws Exception {

        serializer.serialize(writer, null);
        assertThat(writer.toString(), is("null"));
    }

}
