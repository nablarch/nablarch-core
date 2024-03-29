package nablarch.core.text.json;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * {@link DateToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class DateToJsonSerializerTest {

    private JsonSerializationManager manager;
    private JsonSerializer serializer;
    private StringWriter writer = new StringWriter();

    @Before
    public void setup() {
        manager = new BasicJsonSerializationManager();
        manager.initialize();

        serializer = new DateToJsonSerializer(manager);
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);
    }

    @After
    public void teardown() throws IOException {
        writer.close();
    }

    @Test
    public void 対象オブジェクトの判定ができること() throws Exception {

        assertThat(serializer.isTarget(Date.class), is(true));

        assertThat(serializer.isTarget(Integer.class), is(false));
        assertThat(serializer.isTarget(java.sql.Date.class), is(false));
        assertThat(serializer.isTarget(java.sql.Time.class), is(false));
        assertThat(serializer.isTarget(java.sql.Timestamp.class), is(false));
        assertThat(serializer.isTarget(Calendar.class), is(false));
    }

    @Test
    public void Dateがシリアライズできること() throws Exception {

        Calendar calendarValue = Calendar.getInstance();
        calendarValue.set(2021,Calendar.JANUARY,23,12,34,56);
        calendarValue.set(Calendar.MILLISECOND, 789);
        Date dateValue = calendarValue.getTime();

        serializer.serialize(writer, dateValue);
        assertThat(writer.toString(), is("\"2021-01-23 12:34:56.789\""));
    }

    @Test
    public void Dateが書式指定でシリアライズできること() throws Exception {

        JsonSerializer serializer = new DateToJsonSerializer(manager);
        Map<String,String> map = new HashMap<String, String>();
        map.put("datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        Calendar calendarValue = Calendar.getInstance();
        calendarValue.set(2021,Calendar.JANUARY,23,12,34,56);
        calendarValue.set(Calendar.MILLISECOND, 789);
        Date dateValue = calendarValue.getTime();

        serializer.serialize(writer, dateValue);
        assertThat(writer.toString(), is("\"2021-01-23T12:34:56.789Z\""));
    }
}
