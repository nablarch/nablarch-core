package nablarch.core.text.json;

import org.junit.Test;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * {@link CalendarToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class CalendarToJsonSerializerTest {

    @Test
    public void 対象オブジェクトの判定ができること() throws Exception {

        JsonSerializer serializer = new CalendarToJsonSerializer();
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        Calendar calendarValue = Calendar.getInstance();
        assertThat(serializer.isTarget(calendarValue.getClass()), is(true));

        Object intValue = 0;
        assertThat(serializer.isTarget(intValue.getClass()), is(false));

    }

    @Test
    public void Dateがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new CalendarToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            Calendar calendarValue = Calendar.getInstance();
            calendarValue.set(2021,0,23,12,34,56);
            calendarValue.set(Calendar.MILLISECOND, 789);

            serializer.serialize(writer, calendarValue);
            assertThat(writer.toString(), is("\"2021-01-23 12:34:56.789\""));
        } finally {
            writer.close();
        }
    }

    @Test
    public void Dateが書式指定でシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new CalendarToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            map.put("datePattern", "yyyy/MM/dd HH:mm:ss");
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            Calendar calendarValue = Calendar.getInstance();
            calendarValue.set(2021,0,23,12,34,56);
            calendarValue.set(Calendar.MILLISECOND, 789);

            serializer.serialize(writer, calendarValue);
            assertThat(writer.toString(), is("\"2021/01/23 12:34:56\""));
        } finally {
            writer.close();
        }
    }
}
