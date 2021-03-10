package nablarch.core.text.json;

import org.junit.Test;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

/**
 * {@link LocalDateTimeToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class LocalDateTimeToJsonSerializerTest {

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

        JsonSerializer serializer = new LocalDateTimeToJsonSerializer();
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        Object dateValue = createLocalDateTime(2021,1,23,12,34,56, 789);
        assertThat(serializer.isTarget(dateValue.getClass()), is(true));

        Object intValue = 0;
        assertThat(serializer.isTarget(intValue.getClass()), is(false));

    }

    @Test
    public void Java8以降のときDateがシリアライズできること() throws Exception {
        assumeTrue(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8);

        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new LocalDateTimeToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            Object dateValue = createLocalDateTime(2021,1,23,12,34,56, 789012345);

            serializer.serialize(writer, dateValue);
            assertThat(writer.toString(), is("\"2021-01-23 12:34:56.789\""));
        } finally {
            writer.close();
        }
    }

    @Test
    public void Java8以降のときDateが書式指定でシリアライズできること() throws Exception {
        assumeTrue(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8);

        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new LocalDateTimeToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            map.put("datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            Object dateValue = createLocalDateTime(2021,1,23,12,34,56, 789012345);

            serializer.serialize(writer, dateValue);
            assertThat(writer.toString(), is("\"2021-01-23T12:34:56.789Z\""));
        } finally {
            writer.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void Java8以降でDateが書式指定がエラーのとき例外がスローされること() throws Exception {
        assumeTrue(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8);

        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new LocalDateTimeToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            map.put("datePattern", "ABCDEFG'");
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);
        } finally {
            writer.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void Java8以降で不正なオブジェクトのとき例外がスローされること() throws Exception {
        assumeTrue(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8);

        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new LocalDateTimeToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            serializer.serialize(writer, "dummy");
        } finally {
            writer.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void Java8以降で書式指定がLocalDateTimeでエラーとなるとき例外がスローされること() throws Exception {
        assumeTrue(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8);

        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new LocalDateTimeToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            map.put("datePattern", "Z");
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            Object dateValue = createLocalDateTime(2021,1,23,12,34,56, 789012345);

            serializer.serialize(writer, dateValue);
        } finally {
            writer.close();
        }
    }

    @Test(expected = NullPointerException.class)
    public void Java8以降で値がnullのとき例外がスローされること() throws Exception {
        assumeTrue(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.8);

        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new LocalDateTimeToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            map.put("datePattern", "Z");
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            serializer.serialize(writer, null);
        } finally {
            writer.close();
        }
    }

    @Test
    public void Java8未満でもオブジェクトの判定に影響しないこと() throws Exception {

        JsonSerializer serializer = new LocalDateTimeToJsonSerializer();
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        Object intValue = 0;
        assertThat(serializer.isTarget(intValue.getClass()), is(false));
    }
}
