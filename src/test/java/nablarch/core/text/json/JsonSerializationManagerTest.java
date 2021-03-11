package nablarch.core.text.json;

import org.junit.Test;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeTrue;

/**
 * {@link JsonSerializationManager}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class JsonSerializationManagerTest {

    private static class MockJsonSerializer implements JsonSerializer {

        private JsonSerializationSettings settings;

        @Override
        public void initialize(JsonSerializationSettings settings) {
            this.settings = settings;
        }

        @Override
        public boolean isTarget(Class<?> valueClass) {
            return false;
        }

        @Override
        public void serialize(Writer writer, Object value) throws IOException {
        }
    }

    @Test
    public void オブジェクトに応じたシリアライザの取得ができること() throws Exception {
        JsonSerializationManager manager = new JsonSerializationManager();
        manager.initialize();

        Object value = "test";
        JsonSerializer serializer = manager.getSerializer(value);
        assertThat(serializer, is(instanceOf(StringToJsonSerializer.class)));

        value = new Date();
        serializer = manager.getSerializer(value);
        assertThat(serializer, is(instanceOf(DateToJsonSerializer.class)));

        value = new HashMap<String, Object>();
        serializer = manager.getSerializer(value);
        assertThat(serializer, is(instanceOf(MapToJsonSerializer.class)));

        value = new ArrayList<String>();
        serializer = manager.getSerializer(value);
        assertThat(serializer, is(instanceOf(ListToJsonSerializer.class)));

        value = new int[0];
        serializer = manager.getSerializer(value);
        assertThat(serializer, is(instanceOf(ArrayToJsonSerializer.class)));

        value = 123;
        serializer = manager.getSerializer(value);
        assertThat(serializer, is(instanceOf(NumberToJsonSerializer.class)));

        value = true;
        serializer = manager.getSerializer(value);
        assertThat(serializer, is(instanceOf(BooleanToJsonSerializer.class)));
    }

    @Test
    public void Java8以降でLocalDateTimeシリアライザの取得ができること() throws Exception {
        assumeTrue(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8);

        JsonSerializationManager manager = new JsonSerializationManager();
        manager.initialize();

        Class<?> clazz = Class.forName("java.time.LocalDateTime");
        Method method = clazz.getDeclaredMethod("now");
        Object value = method.invoke(null);

        JsonSerializer serializer = manager.getSerializer(value);
        assertThat(serializer, is(instanceOf(LocalDateTimeToJsonSerializer.class)));
    }

    @Test
    public void シリアライザの初期化が行われていること() {
        final MockJsonSerializer mockJsonSerializer = new MockJsonSerializer();

        JsonSerializationManager manager = new JsonSerializationManager() {
            @Override
            protected List<JsonSerializer> createSerializers(JsonSerializationSettings settings) {
                return Arrays.asList((JsonSerializer)mockJsonSerializer);
            }
        };

        JsonSerializationSettings settings = new JsonSerializationSettings(new HashMap<String, String>());
        manager.initialize(settings);

        assertThat(mockJsonSerializer.settings, is(sameInstance(settings)));
    }

    @Test
    public void nullのシリアライザの取得ができること() throws Exception {
        JsonSerializationManager manager = new JsonSerializationManager();
        manager.initialize();

        Object value = null;

        JsonSerializer serializer = manager.getSerializer(value);
        assertThat(serializer, is(instanceOf(NullToJsonSerializer.class)));
    }

    @Test
    public void デフォルトのシリアライザの取得ができること() throws Exception {
        JsonSerializationManager manager = new JsonSerializationManager();
        manager.initialize();

        Object value = this;

        JsonSerializer serializer = manager.getSerializer(value);
        assertThat(serializer, is(instanceOf(ObjectToJsonSerializer.class)));
    }

    @Test(expected = IllegalStateException.class)
    public void 初期化していない場合エラーになること() throws Exception {
        JsonSerializationManager manager = new JsonSerializationManager();

        Object value = "test";

        JsonSerializer serializer = manager.getSerializer(value);
    }
}
