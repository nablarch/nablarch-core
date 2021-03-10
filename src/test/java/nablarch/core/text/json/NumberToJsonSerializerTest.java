package nablarch.core.text.json;

import org.junit.Test;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * {@link NumberToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class NumberToJsonSerializerTest {

    @Test
    public void 対象オブジェクトの判定ができること() throws Exception {

        JsonSerializer serializer = new NumberToJsonSerializer();
        Map<String,String> map = new HashMap<String, String>();
        JsonSerializationSettings settings = new JsonSerializationSettings(map);
        serializer.initialize(settings);

        Object intValue = 0;
        assertThat(serializer.isTarget(intValue.getClass()), is(true));

        Object shortValue = (short)0;
        assertThat(serializer.isTarget(shortValue.getClass()), is(true));

        Object longValue = 0l;
        assertThat(serializer.isTarget(longValue.getClass()), is(true));

        Object byteValue = (byte)0;
        assertThat(serializer.isTarget(byteValue.getClass()), is(true));

        Object floatValue = 0.0f;
        assertThat(serializer.isTarget(floatValue.getClass()), is(true));

        Object doubleValue = 0.0d;
        assertThat(serializer.isTarget(doubleValue.getClass()), is(true));

        Object bigDecimalValue = new BigDecimal(0.0);
        assertThat(serializer.isTarget(bigDecimalValue.getClass()), is(true));

        Object bigIntegerValue = new BigInteger("0", 10);
        assertThat(serializer.isTarget(bigIntegerValue.getClass()), is(true));

        Object atomicIntegerValue = new AtomicInteger(0);
        assertThat(serializer.isTarget(atomicIntegerValue.getClass()), is(true));

        Object atomicLongValue = new AtomicLong(0);
        assertThat(serializer.isTarget(atomicLongValue.getClass()), is(true));

        Object booleanValue = true;
        assertThat(serializer.isTarget(booleanValue.getClass()), is(false));

    }

    @Test
    public void intがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            serializer.serialize(writer, 123);
            assertThat(writer.toString(), is("123"));
        } finally {
            writer.close();
        }
    }

    @Test
    public void shortがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            short value = 123;
            serializer.serialize(writer, value);
            assertThat(writer.toString(), is("123"));
        } finally {
            writer.close();
        }
    }

    @Test
    public void longがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            long value = 123l;
            serializer.serialize(writer, value);
            assertThat(writer.toString(), is("123"));
        } finally {
            writer.close();
        }
    }

    @Test
    public void floatがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            float value = 100.0f / 3;
            serializer.serialize(writer, value);
            assertThat(writer.toString(), is("33.333332"));
        } finally {
            writer.close();
        }
    }

    @Test
    public void floatのNaNがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            float value = Float.NaN;
            serializer.serialize(writer, value);
            assertThat(writer.toString(), is("\"NaN\""));
        } finally {
            writer.close();
        }
    }

    @Test
    public void floatの正の無限大がシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            float value = Float.POSITIVE_INFINITY;
            serializer.serialize(writer, value);
            assertThat(writer.toString(), is("\"Infinity\""));
        } finally {
            writer.close();
        }
    }

    @Test
    public void floatの負の無限大がシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            float value = Float.NEGATIVE_INFINITY;
            serializer.serialize(writer, value);
            assertThat(writer.toString(), is("\"-Infinity\""));
        } finally {
            writer.close();
        }
    }

    @Test
    public void doubleがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            double value = 100.0d / 3;
            serializer.serialize(writer, value);
            assertThat(writer.toString(), is("33.333333333333336"));
        } finally {
            writer.close();
        }
    }

    @Test
    public void doubleのNaNがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            double value = Double.NaN;
            serializer.serialize(writer, value);
            assertThat(writer.toString(), is("\"NaN\""));
        } finally {
            writer.close();
        }
    }

    @Test
    public void doubleの正の無限大がシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            double value = Double.POSITIVE_INFINITY;
            serializer.serialize(writer, value);
            assertThat(writer.toString(), is("\"Infinity\""));
        } finally {
            writer.close();
        }
    }

    @Test
    public void doubleの負の無限大がシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            double value = Double.NEGATIVE_INFINITY;
            serializer.serialize(writer, value);
            assertThat(writer.toString(), is("\"-Infinity\""));
        } finally {
            writer.close();
        }
    }

    @Test
    public void BigDecimalがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            // TODO 末尾のゼロは出力されるべき？
            BigDecimal value = new BigDecimal("123.4567890");
            serializer.serialize(writer, value);
            assertThat(writer.toString(), is("123.4567890"));
        } finally {
            writer.close();
        }
    }

    @Test
    public void BigIntegerがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            BigInteger value = new BigInteger("ffff", 16);
            serializer.serialize(writer, value);
            assertThat(writer.toString(), is("65535"));
        } finally {
            writer.close();
        }
    }

    @Test
    public void AtomicIntegerがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            AtomicInteger value = new AtomicInteger(123);
            serializer.serialize(writer, value);
            assertThat(writer.toString(), is("123"));
        } finally {
            writer.close();
        }
    }

    @Test
    public void AtomicLongがシリアライズできること() throws Exception {
        StringWriter writer = new StringWriter();

        try {
            JsonSerializer serializer = new NumberToJsonSerializer();
            Map<String,String> map = new HashMap<String, String>();
            JsonSerializationSettings settings = new JsonSerializationSettings(map);
            serializer.initialize(settings);

            AtomicLong value = new AtomicLong(123);
            serializer.serialize(writer, value);
            assertThat(writer.toString(), is("123"));
        } finally {
            writer.close();
        }
    }
}
