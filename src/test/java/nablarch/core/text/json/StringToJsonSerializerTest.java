package nablarch.core.text.json;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * {@link StringToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class StringToJsonSerializerTest {

    private JsonSerializer serializer;
    private StringWriter writer = new StringWriter();

    @Before
    public void setup() {
        serializer = new StringToJsonSerializer();
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

        assertThat(serializer.isTarget(String.class), is(true));

        assertThat(serializer.isTarget(Integer.class), is(false));
    }

    @Test
    public void 文字列がシリアライズできること() throws Exception {

        serializer.serialize(writer, "123abcABC");
        assertThat(writer.toString(), is("\"123abcABC\""));
    }

    @Test
    public void 空の文字列がシリアライズできること() throws Exception {

        serializer.serialize(writer, "");
        assertThat(writer.toString(), is("\"\""));
    }

    @Test
    public void Escape処理ができること() throws Exception {

        serializer.serialize(writer, "\u001f");
        assertThat(writer.toString(), is("\"\\u001f\""));

        writer = new StringWriter();

        serializer.serialize(writer, "\"ABC\"");
        assertThat(writer.toString(), is("\"\\\"ABC\\\"\""));

        writer = new StringWriter();

        serializer.serialize(writer, "123\\\"a\b\f\tb\u001fc\r\nABC");
        assertThat(writer.toString(), is("\"123\\\\\\\"a\\b\\f\\tb\\u001fc\\r\\nABC\""));
    }

    @Test
    public void 先頭のEscape処理ができること() throws Exception {

        serializer.serialize(writer, "\\123abcABC");
        assertThat(writer.toString(), is("\"\\\\123abcABC\""));
    }

    @Test
    public void 末尾のEscape処理ができること() throws Exception {

        serializer.serialize(writer, "123abcABC\\");
        assertThat(writer.toString(), is("\"123abcABC\\\\\""));
    }
}
