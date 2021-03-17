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

        // (note) c0 100%の為に、0x00～0x1f, ", \ のそれぞれが
        //        最初のエスケープとして現れるケースについてテストが必要

        // 単独のエスケープ処理の確認
        serializer.serialize(writer, "\u001f");
        assertThat(writer.toString(), is("\"\\u001f\""));

        // 連続したエスケープ処理の確認
        writer = new StringWriter();
        serializer.serialize(writer, "\"\"");
        assertThat(writer.toString(), is("\"\\\"\\\"\""));

        // その他エスケープ対象文字の確認
        writer = new StringWriter();
        serializer.serialize(writer, "123,\\,\",\b,\f,\t,\u001f,\r,\n,ABC");
        assertThat(writer.toString(), is("\"123,\\\\,\\\",\\b,\\f,\\t,\\u001f,\\r,\\n,ABC\""));
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
