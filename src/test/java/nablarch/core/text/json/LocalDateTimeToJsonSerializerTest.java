package nablarch.core.text.json;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeTrue;

/**
 * {@link LocalDateTimeToJsonSerializer}のテストクラス
 * <p>
 * 本テストクラスでは、LocalDateTimeを扱うため、Java8以上をテスト対象とする。
 * Javaのバージョンに関係なく判定が行えるかのテストは{@link JavaTimeToJsonSerializer}にて実施する。
 * </p>
 * @author Shuji Kitamura
 */
public class LocalDateTimeToJsonSerializerTest {

    private JsonSerializationManager manager;
    private JsonSerializer serializer;
    private StringWriter writer = new StringWriter();

    @Before
    public void setup() {
        manager = new BasicJsonSerializationManager();
        manager.initialize();

        serializer = new LocalDateTimeToJsonSerializer(manager);
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);
    }

    @After
    public void teardown() throws IOException {
        writer.close();
    }

    private Object createLocalDateTime(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) throws Exception {
        Class<?> monthClazz = Class.forName("java.time.Month");
        Method monthMethod = monthClazz.getDeclaredMethod("of", int.class);
        Object monthObject = monthMethod.invoke(null, month);

        Class<?> clazz = Class.forName("java.time.LocalDateTime");
        Method method = clazz.getDeclaredMethod("of", int.class, monthClazz, int.class, int.class, int.class, int.class, int.class);
        return method.invoke(null, year, monthObject, dayOfMonth, hour, minute, second, nanoOfSecond);
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
    public void 対象オブジェクトの判定ができること() throws Exception {
        assumeTrue(isRunningOnJava8OrHigher());

        assertThat(serializer.isTarget(Class.forName("java.time.LocalDateTime")), is(true));

        assertThat(serializer.isTarget(Integer.class), is(false));
        assertThat(serializer.isTarget(Class.forName("java.time.LocalDate")), is(false));
    }

    @Test
    public void LocalDateTimeがシリアライズできること() throws Exception {
        assumeTrue(isRunningOnJava8OrHigher());

        Object dateValue = createLocalDateTime(2021,1,23,12,34,56, 789012345);

        serializer.serialize(writer, dateValue);
        assertThat(writer.toString(), is("\"2021-01-23 12:34:56.789\""));
    }

    @Test
    public void LocalDateTimeが書式指定でシリアライズできること() throws Exception {
        assumeTrue(isRunningOnJava8OrHigher());

        JsonSerializer serializer = new LocalDateTimeToJsonSerializer(manager);
        Map<String,String> map = new HashMap<String, String>();
        map.put("datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        Object dateValue = createLocalDateTime(2021,1,23,12,34,56, 789012345);

        serializer.serialize(writer, dateValue);
        assertThat(writer.toString(), is("\"2021-01-23T12:34:56.789Z\""));
    }
}
