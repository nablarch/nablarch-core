package nablarch.core.text.json;

import org.junit.Test;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assume.assumeTrue;

/**
 * {@link JsonSerializationManager}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class JsonSerializationManagerTest {

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
    public void シリアライザの初期化が行われていること() throws Exception {
        JsonSerializationManager manager = new JsonSerializationManager();
        Map<String,String> map = new HashMap<String, String>();
        map.put("datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        manager.initialize(settings);

        Calendar calendarValue = Calendar.getInstance();
        calendarValue.set(2021,0,23,12,34,56);
        calendarValue.set(Calendar.MILLISECOND, 789);
        Object value = calendarValue.getTime();

        JsonSerializer serializer = manager.getSerializer(value);
        assertThat(serializer, is(instanceOf(DateToJsonSerializer.class)));

        StringWriter writer = new StringWriter();
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("\"2021-01-23T12:34:56.789Z\""));
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
