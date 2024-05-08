package nablarch.core.text.json;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThrows;

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
        JsonSerializationManager manager = new BasicJsonSerializationManager();
        manager.initialize();

        serializer = new ObjectToJsonSerializer(manager);
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
    public void toStringの結果でstringとしてシリアライズできること() throws Exception {

        serializer.serialize(writer, new Object() {
            @Override
            public String toString() {
                return "TEST";
            }
        });
        assertThat(writer.toString(), is("\"TEST\""));
    }

    @Test
    public void nullはserializeで例外になること() throws Exception {

        Exception e = assertThrows(NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                serializer.serialize(writer, null);
            }
        });

        assertThat(e.getMessage(), is("value must not be null."));
    }

}
