package nablarch.core.text.json;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
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

    private JsonSerializer serializer;
    private StringWriter writer = new StringWriter();

    @Before
    public void setup() {
        JsonSerializationManager manager = new JsonSerializationManager();
        manager.initialize();

        serializer = new NumberToJsonSerializer(manager);
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

        assertThat(serializer.isTarget(Number.class), is(true));
        assertThat(serializer.isTarget(Integer.class), is(true));
        assertThat(serializer.isTarget(Short.class), is(true));
        assertThat(serializer.isTarget(Long.class), is(true));
        assertThat(serializer.isTarget(Byte.class), is(true));
        assertThat(serializer.isTarget(Float.class), is(true));
        assertThat(serializer.isTarget(Double.class), is(true));
        assertThat(serializer.isTarget(BigDecimal.class), is(true));
        assertThat(serializer.isTarget(BigInteger.class), is(true));
        assertThat(serializer.isTarget(AtomicInteger.class), is(true));
        assertThat(serializer.isTarget(AtomicLong.class), is(true));

        assertThat(serializer.isTarget(int.class), is(false));
        assertThat(serializer.isTarget(short.class), is(false));
        assertThat(serializer.isTarget(long.class), is(false));
        assertThat(serializer.isTarget(byte.class), is(false));
        assertThat(serializer.isTarget(float.class), is(false));
        assertThat(serializer.isTarget(double.class), is(false));

        assertThat(serializer.isTarget(Boolean.class), is(false));
    }

    @Test
    public void intがシリアライズできること() throws Exception {

        serializer.serialize(writer, 123);
        assertThat(writer.toString(), is("123"));
    }

    @Test
    public void shortがシリアライズできること() throws Exception {

        short value = 123;
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("123"));
    }

    @Test
    public void longがシリアライズできること() throws Exception {

        long value = 123l;
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("123"));
    }

    @Test
    public void floatがシリアライズできること() throws Exception {

        float value = 0.12345678f;
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("0.12345678"));
    }

    @Test
    public void floatのNaNがシリアライズできること() throws Exception {

        float value = Float.NaN;
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("\"NaN\""));
    }

    @Test
    public void floatの正の無限大がシリアライズできること() throws Exception {

        float value = Float.POSITIVE_INFINITY;
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("\"Infinity\""));
    }

    @Test
    public void floatの負の無限大がシリアライズできること() throws Exception {

        float value = Float.NEGATIVE_INFINITY;
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("\"-Infinity\""));
    }

    @Test
    public void doubleがシリアライズできること() throws Exception {

        double value = 0.1234567890123456;
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("0.1234567890123456"));
    }

    @Test
    public void doubleのNaNがシリアライズできること() throws Exception {

        double value = Double.NaN;
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("\"NaN\""));
    }

    @Test
    public void doubleの正の無限大がシリアライズできること() throws Exception {

        double value = Double.POSITIVE_INFINITY;
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("\"Infinity\""));
    }

    @Test
    public void doubleの負の無限大がシリアライズできること() throws Exception {

        double value = Double.NEGATIVE_INFINITY;
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("\"-Infinity\""));
    }

    @Test
    public void BigDecimalがシリアライズできること() throws Exception {

        BigDecimal value = new BigDecimal("123.4567890");
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("123.4567890"));
    }

    @Test
    public void BigIntegerがシリアライズできること() throws Exception {

        BigInteger value = new BigInteger("123456789012345678901234567890");
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("123456789012345678901234567890"));
    }

    @Test
    public void AtomicIntegerがシリアライズできること() throws Exception {

        AtomicInteger value = new AtomicInteger(123);
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("123"));
    }

    @Test
    public void AtomicLongがシリアライズできること() throws Exception {

        AtomicLong value = new AtomicLong(123);
        serializer.serialize(writer, value);
        assertThat(writer.toString(), is("123"));
    }
}
