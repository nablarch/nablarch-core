package nablarch.core.text.json;

import org.junit.Test;

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

    @Test
    public void 対象オブジェクトの判定ができること() throws Exception {

        JsonSerializer serializer = new StringToJsonSerializer();
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        Object stringValue = "";
        assertThat(serializer.isTarget(stringValue.getClass()), is(true));

        Object intValue = 0;
        assertThat(serializer.isTarget(intValue.getClass()), is(false));

    }

    @Test
    public void 文字列がシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new StringToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            serializer.serialize(writer, "123abcABC");
            assertThat(writer.toString(), is("\"123abcABC\""));
        } finally {
            writer.close();
        }
    }

    @Test
    public void 空の文字列がシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new StringToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            serializer.serialize(writer, "");
            assertThat(writer.toString(), is("\"\""));
        } finally {
            writer.close();
        }
    }

    @Test
    public void Escape処理ができること() throws Exception {
        JsonSerializer serializer = new StringToJsonSerializer();
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        StringWriter writer = new StringWriter();
        try {
            serializer.serialize(writer, "\u001f");
            assertThat(writer.toString(), is("\"\\u001f\""));
        } finally {
            writer.close();
        }

        writer = new StringWriter();
        try {
            serializer.serialize(writer, "\"ABC\"");
            assertThat(writer.toString(), is("\"\\\"ABC\\\"\""));
        } finally {
            writer.close();
        }

        writer = new StringWriter();

        try {
            serializer.serialize(writer, "123\\\"a\b\f\tb\u001fc\r\nABC");
            assertThat(writer.toString(), is("\"123\\\\\\\"a\\b\\f\\tb\\u001fc\\r\\nABC\""));
        } finally {
            writer.close();
        }
    }

    @Test
    public void 先頭のEscape処理ができること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new StringToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            serializer.serialize(writer, "\\123abcABC");
            assertThat(writer.toString(), is("\"\\\\123abcABC\""));
        } finally {
            writer.close();
        }
    }

    @Test
    public void 末尾のEscape処理ができること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new StringToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            serializer.serialize(writer, "123abcABC\\");
            assertThat(writer.toString(), is("\"123abcABC\\\\\""));
        } finally {
            writer.close();
        }
    }
}
