package nablarch.core.text.json;

import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThrows;
import static org.junit.Assume.assumeTrue;

/**
 * {@link BasicJsonSerializationManager}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class BasicJsonSerializationManagerTest {

    BasicJsonSerializationManager manager;

    @Before
    public void setup() {
        manager = new BasicJsonSerializationManager();
    }

    private boolean isRunningOnJava8OrHigher() {
        try {
            Class.forName("java.time.LocalDateTime");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Test
    public void オブジェクトに応じたシリアライザの取得ができること() throws Exception {
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
        assumeTrue(isRunningOnJava8OrHigher());

        BasicJsonSerializationManager manager = new BasicJsonSerializationManager();
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

        BasicJsonSerializationManager manager = new BasicJsonSerializationManager() {
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
        BasicJsonSerializationManager manager = new BasicJsonSerializationManager();
        manager.initialize();

        Object value = null;

        JsonSerializer serializer = manager.getSerializer(value);
        assertThat(serializer, is(instanceOf(NullToJsonSerializer.class)));
    }

    @Test
    public void デフォルトのシリアライザの取得ができること() throws Exception {
        BasicJsonSerializationManager manager = new BasicJsonSerializationManager();
        manager.initialize();

        Object value = this;

        JsonSerializer serializer = manager.getSerializer(value);
        assertThat(serializer, is(instanceOf(ObjectToJsonSerializer.class)));
    }

    @Test
    public void 初期化していない場合エラーになること() throws Exception {

        Exception e = assertThrows(IllegalStateException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                Object value = "test";
                manager.getSerializer(value);
            }
        });

        assertThat(e.getMessage(), is("JsonSerializationManager is not initialized."));
    }

    /**
     * initializeメソッドの実行確認用Mockクラス。
     */
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

}
