package nablarch.core.text.json;

import org.junit.Test;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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
