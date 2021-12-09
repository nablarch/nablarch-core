package nablarch.core.text.json;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThrows;
import static org.junit.Assume.assumeTrue;

/**
 * {@link JavaTimeToJsonSerializer}のテストクラス
 *
 * @author Shuji Kitamura
 */
public class JavaTimeToJsonSerializerTest {

    private String datePattern = "uuuu-MM-dd";
    private JsonSerializer serializer;
    private StringWriter writer = new StringWriter();

    @Before
    public void setup() {
        JsonSerializationManager manager = new BasicJsonSerializationManager();
        manager.initialize();

        serializer = new JavaTimeToJsonSerializer(manager) {

            @Override
            protected String getDatePattern(JsonSerializationSettings settings) {
                return JavaTimeToJsonSerializerTest.this.datePattern;
            }

            @Override
            protected String getValueClassName() {
                return "java.time.LocalDate";
            }

        };
    }

    @After
    public void teardown() throws IOException {
        writer.close();
    }

    private Object createLocalDate(int year, int month, int dayOfMonth) throws Exception {
        Class<?> monthClazz = Class.forName("java.time.Month");
        Method monthMethod = monthClazz.getDeclaredMethod("of", int.class);
        Object monthObject = monthMethod.invoke(null, month);

        Class<?> clazz = Class.forName("java.time.LocalDate");
        Method method = clazz.getDeclaredMethod("of", int.class, monthClazz, int.class);
        return method.invoke(null, year, monthObject, dayOfMonth);
    }

    private boolean isRunningOnJava8OrHigher() {
        try {
            Class.forName("java.time.LocalDateTime");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Test
    public void Java8以上のときDateTimeAPIの指定クラスが対象オブジェクトとして判定できること() throws Exception {
        assumeTrue(isRunningOnJava8OrHigher());

        serializer.initialize(new JsonSerializationSettings());

        assertThat(serializer.isTarget(Class.forName("java.time.LocalDate")), is(true));
    }

    @Test
    public void DateTimeAPIのクラス以外を対象オブジェクトではないと判定できること() throws Exception {
        // note この判定はJavaのバージョンに依存せずに実行できる必要あり

        serializer.initialize(new JsonSerializationSettings());

        assertThat(serializer.isTarget(Integer.class), is(false));
        assertThat(serializer.isTarget(Date.class), is(false));
        assertThat(serializer.isTarget(Calendar.class), is(false));
    }

    @Test
    public void Java8以降のときDateTimeAPIの指定クラスがシリアライズできること() throws Exception {
        assumeTrue(isRunningOnJava8OrHigher());

        serializer.initialize(new JsonSerializationSettings());

        Object dateValue = createLocalDate(2021,1,23);

        serializer.serialize(writer, dateValue);
        assertThat(writer.toString(), is("\"2021-01-23\""));
    }

    @Test
    public void Java8以降でDateTimeAPIの書式指定がエラーのとき例外がスローされること() throws Exception {
        assumeTrue(isRunningOnJava8OrHigher());

        Exception e = assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                datePattern = "ABCDEFG";
                serializer.initialize(new JsonSerializationSettings());
            }
        });

        assertThat(e.getMessage(), is("illegal date pattern. pattern = [ABCDEFG]"));
    }

    @Test
    public void Java8以降で不正なオブジェクトのとき例外がスローされること() throws Exception {
        assumeTrue(isRunningOnJava8OrHigher());

        Exception e = assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                serializer.initialize(new JsonSerializationSettings());
                serializer.serialize(writer, "dummy");
            }
        });

        assertThat(e.getMessage(), is("argument type mismatch"));
    }

    @Test
    public void Java8以降で書式指定がDateTimeAPIの指定クラスでエラーとなるとき例外がスローされること() throws Exception {
        assumeTrue(isRunningOnJava8OrHigher());

        Exception e = assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                datePattern = "yyyy-MM-dd HH:mm:ss.SSS";
                serializer.initialize(new JsonSerializationSettings());

                Object dateValue = createLocalDate(2021,1,23);

                serializer.serialize(writer, dateValue);
            }
        });

        assertThat(e.getMessage(), is("mismatched date pattern. pattern = [yyyy-MM-dd HH:mm:ss.SSS]"));
    }

    @Test
    public void Java8以降で値がnullのとき例外がスローされること() throws Exception {
        assumeTrue(isRunningOnJava8OrHigher());


        Exception e = assertThrows(NullPointerException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                serializer.initialize(new JsonSerializationSettings());
                serializer.serialize(writer, null);
            }
        });

        assertThat(e.getMessage(), is(nullValue()));
    }

}
