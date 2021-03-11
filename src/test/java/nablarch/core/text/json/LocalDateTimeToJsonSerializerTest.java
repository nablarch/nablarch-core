package nablarch.core.text.json;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThrows;
import static org.junit.Assume.assumeTrue;

/**
 * {@link LocalDateTimeToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class LocalDateTimeToJsonSerializerTest {

    private JsonSerializer serializer;
    private StringWriter writer = new StringWriter();

    @Before
    public void setup() {
        serializer = new LocalDateTimeToJsonSerializer();
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);
    }

    @After
    public void teardown() throws IOException {
        writer.close();
    }

    private Object createLocalDateTime(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        try {
            Class<?> monthClazz = Class.forName("java.time.Month");
            Method monthMethod = monthClazz.getDeclaredMethod("of", int.class);
            Object monthObject = monthMethod.invoke(null, month);

            Class<?> clazz = Class.forName("java.time.LocalDateTime");
            Method method = clazz.getDeclaredMethod("of", int.class, monthClazz, int.class, int.class, int.class, int.class, int.class);
            return method.invoke(null, year, monthObject, dayOfMonth, hour, minute, second, nanoOfSecond);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void Java8以降のとき対象オブジェクトの判定ができること() throws Exception {
        assumeTrue(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8);

        assertThat(serializer.isTarget(Class.forName("java.time.LocalDateTime")), is(true));

        assertThat(serializer.isTarget(Integer.class), is(false));
    }

    @Test
    public void Java8以降のときLocalDateTimeがシリアライズできること() throws Exception {
        assumeTrue(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8);

        Object dateValue = createLocalDateTime(2021,1,23,12,34,56, 789012345);

        serializer.serialize(writer, dateValue);
        assertThat(writer.toString(), is("\"2021-01-23 12:34:56.789\""));
    }

    @Test
    public void Java8以降のときLocalDateTimeが書式指定でシリアライズできること() throws Exception {
        assumeTrue(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8);

        JsonSerializer serializer = new LocalDateTimeToJsonSerializer();
        Map<String,String> map = new HashMap<String, String>();
        map.put("datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        Object dateValue = createLocalDateTime(2021,1,23,12,34,56, 789012345);

        serializer.serialize(writer, dateValue);
        assertThat(writer.toString(), is("\"2021-01-23T12:34:56.789Z\""));
    }

    @Test
    public void Java8以降でLocalDateTimeが書式指定がエラーのとき例外がスローされること() throws Exception {
        assumeTrue(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8);

        Exception e = assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                JsonSerializer serializer = new LocalDateTimeToJsonSerializer();
                Map<String,String> map = new HashMap<String, String>();
                map.put("datePattern", "ABCDEFG");
                JsonSerializationSettings settings = new JsonSerializationSettings(map);
                serializer.initialize(settings);
            }
        });

        assertThat(e.getMessage(), is("illegal date pattern. pattern = [ABCDEFG], property name = [datePattern]"));
    }

    @Test
    public void Java8以降で不正なオブジェクトのとき例外がスローされること() throws Exception {
        assumeTrue(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8);

        Exception e = assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                serializer.serialize(writer, "dummy");
            }
        });

        assertThat(e.getMessage(), is("argument type mismatch"));
    }

    @Test
    public void Java8以降で書式指定がLocalDateTimeでエラーとなるとき例外がスローされること() throws Exception {
        assumeTrue(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8);

        Exception e = assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                JsonSerializer serializer = new LocalDateTimeToJsonSerializer();
                Map<String,String> map = new HashMap<String, String>();
                map.put("datePattern", "Z");
                JsonSerializationSettings settings = new JsonSerializationSettings(map);
                serializer.initialize(settings);

                Object dateValue = createLocalDateTime(2021,1,23,12,34,56, 789012345);

                serializer.serialize(writer, dateValue);
            }
        });

        assertThat(e.getMessage(), is("mismatched date pattern. pattern = [Z]"));
    }

    @Test
    public void Java8以降で値がnullのとき例外がスローされること() throws Exception {
        assumeTrue(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8);

        Exception e = assertThrows(NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                serializer.serialize(writer, null);
            }
        });

        assertThat(e.getMessage(), is(nullValue()));
    }

    @Test
    public void Java8未満でもオブジェクトの判定に影響しないこと() throws Exception {

        Object intValue = 0;
        assertThat(serializer.isTarget(intValue.getClass()), is(false));
    }
}
