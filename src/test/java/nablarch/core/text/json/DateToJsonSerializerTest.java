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

    private JsonSerializer serializer;
    private StringWriter writer = new StringWriter();

    @Before
    public void setup() {
        serializer = new DateToJsonSerializer();
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

        Object dateValue = new Date();
        assertThat(serializer.isTarget(dateValue.getClass()), is(true));

        Object intValue = 0;
        assertThat(serializer.isTarget(intValue.getClass()), is(false));

    }

    @Test
    public void Dateがシリアライズできること() throws Exception {

        Calendar calendarValue = Calendar.getInstance();
        calendarValue.set(2021,0,23,12,34,56);
        calendarValue.set(Calendar.MILLISECOND, 789);
        Date dateValue = calendarValue.getTime();

        serializer.serialize(writer, dateValue);
        assertThat(writer.toString(), is("\"2021-01-23 12:34:56.789\""));
    }

    @Test
    public void Dateが書式指定でシリアライズできること() throws Exception {

        JsonSerializer serializer = new DateToJsonSerializer();
        Map<String,String> map = new HashMap<String, String>();
        map.put("datePattern", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        Calendar calendarValue = Calendar.getInstance();
        calendarValue.set(2021,0,23,12,34,56);
        calendarValue.set(Calendar.MILLISECOND, 789);
        Date dateValue = calendarValue.getTime();

        serializer.serialize(writer, dateValue);
        assertThat(writer.toString(), is("\"2021-01-23T12:34:56.789Z\""));
    }
}
