package nablarch.core.util;


import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.hamcrest.object.IsCompatibleType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class BuilderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * 文字列を連結した形でアサートするMatcher
     */
    public static class StringConcatMatcher extends TypeSafeMatcher<String> {

        private final String expected;

        public StringConcatMatcher(String expected) {
            this.expected = expected;
        }

        private static StringConcatMatcher is(String separator, String... strs) {
            StringBuffer sb = new StringBuffer(256);
            for (String str : strs) {
                if (sb.length() != 0) {
                    sb.append(separator);
                }
                sb.append(str);
            }
            return new StringConcatMatcher(sb.toString());
        }

        @Override
        protected boolean matchesSafely(String s) {
            return s.equals(expected);
        }

        @Override
        public void describeTo(Description description) {
            super.describeMismatch(expected, description);
        }
    }

    @Test
    public void testLines() {
        String packages = Builder.lines(
                "nablarch.tool",
                "nablarch.fw.web.http",
                "nablarch.core.html",
                "nablarch.core.doctool",
                null,
                100);

        assertThat(packages, StringConcatMatcher.is(
                Builder.LS,
                "nablarch.tool",
                "nablarch.fw.web.http",
                "nablarch.core.html",
                "nablarch.core.doctool",
                "100"));
    }

    @Test
    public void testLinesf() {
        // テンプレート文字列を使用しない場合。
        String packages = Builder.linesf(
                "nablarch.tool",
                "nablarch.fw.web.http",
                "nablarch.core.html",
                "nablarch.core.doctool",
                null,
                100);

        assertThat(packages, StringConcatMatcher.is(
                Builder.LS,
                "nablarch.tool",
                "nablarch.fw.web.http",
                "nablarch.core.html",
                "nablarch.core.doctool",
                "100"));

        // テンプレート文字列を使用する場合。
        String nameSpace = "nablarch";
        String doctool = "doctool";

        packages = Builder.linesf(
                "%s.util", nameSpace
                , "%s.http", nameSpace
                , "%s.html", nameSpace
                , "%s.%s", nameSpace, doctool
        );

        assertThat(packages, StringConcatMatcher.is(
                Builder.LS,
                "nablarch.util",
                "nablarch.http",
                "nablarch.html",
                "nablarch.doctool"));
    }

    @Test
    public void testJoin() {
        List<String> packageList = Arrays.asList(
                "nablarch.tool",
                "nablarch.fw.web.http",
                "nablarch.core.html",
                "nablarch.core.doctool");

        String result = Builder.join(packageList, "|");

        assertThat(result, StringConcatMatcher.is(
                "|",
                "nablarch.tool",
                "nablarch.fw.web.http",
                "nablarch.core.html",
                "nablarch.core.doctool"));
    }

    @Test
    public void testList() {
        String leapDate = "02-29";

        List<Date> result = Builder.listf(
                Date.class,
                Date.valueOf("2004-02-28"),         // elementTypeのオブジェクトを設定。
                "2008-02-27",                       // java.sql.Date.valueOf(String) の結果が格納される。
                "2012-%s", leapDate                 // 埋め込みパラメータを使用。
        );

        assertThat(result, IsIterableContainingInOrder.contains(
                Date.valueOf("2004-02-28"),
                Date.valueOf("2008-02-27"),
                Date.valueOf("2012-02-29")
        ));

        result = Builder.list(Date.class,
                Date.valueOf("2004-02-29"), // elementTypeのオブジェクトを設定。
                "2008-02-29",                        // java.sql.Date.valueOf(String) の結果が格納される。
                "2012-02-29"
        );

        assertThat(result, IsIterableContainingInOrder.contains(
                Date.valueOf("2004-02-29"),
                Date.valueOf("2008-02-29"),
                Date.valueOf("2012-02-29")
        ));
    }


    @Test
    public void testConcat() {
        assertThat(Builder.concat("foo", "bar", "buz"), CoreMatchers.is("foobarbuz"));
        assertThat(Builder.concat("foo"), CoreMatchers.is("foo"));
        assertThat(Builder.concat(""), CoreMatchers.is(""));
        assertThat(Builder.concat("", ""), CoreMatchers.is(""));
        assertThat(Builder.concat(), CoreMatchers.is(""));
        assertThat(Builder.concat((Object[]) null), CoreMatchers.is("null"));
    }

    @Test
    public void testSplit() {
        List<String> result = Builder.split("1234\n5678");
        assertThat(result, IsIterableContainingInOrder.contains("1234", "5678"));

        result = Builder.split("1234$5678", "\\$");
        assertThat(result, IsIterableContainingInOrder.contains("1234", "5678"));

        result = Builder.split(null, "$");
        assertThat(result, IsIterableContainingInOrder.contains(""));
    }

    @Test
    public void testTypeOf() {
        assertThat(Builder.typeOf("abc"), IsCompatibleType.typeCompatibleWith(String.class));
        assertThat(Builder.typeOf("123456"), IsCompatibleType.typeCompatibleWith(Integer.class));
        assertThat(Builder.typeOf("123456.12345"), IsCompatibleType.typeCompatibleWith(Double.class));
    }

    @Test
    public void testValueOf() throws Exception {
        // 指定の方に変換される
        assertThat("Stringに変換される", Builder.valueOf(String.class, 100), CoreMatchers.is("100"));
        assertThat("Integerに変換される", Builder.valueOf(Integer.class, "100"), CoreMatchers.is(100));

        // 完全修飾名を指定してClassクラスに変換
        Class<?> clazz = Builder.valueOf(Class.class, "java.lang.Integer");
        assertThat(clazz, IsCompatibleType.typeCompatibleWith(Integer.class));
    }

    @Test
    public void valueOf_notFoundClass_throwException() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectCause(CoreMatchers.<Throwable>instanceOf(ClassNotFoundException.class));
        Builder.valueOf(Class.class, "java.NotFound");
    }

    @Test
    public void valueOf_notFoundValueOfMethod_throwException() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Static constructor 'valueOf(...)' wasn't found in ");
        class Hoge {

        }
        Builder.valueOf(Hoge.class, "100");
    }

    @Test
    public void valueOf_invalidValueOfType_throwException() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Static constructor 'valueOf(...)' wasn't found in ");
        Builder.valueOf(InvalidValueOfParam.class, "100");
    }

    @Test
    public void valueOf_invalidReturnType_throwException() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Static constructor 'valueOf(...)' wasn't found in ");
        Builder.valueOf(InvalidValueOfReturnType.class, "100");
    }

    @Test
    public void valueOf_invalidValueOfModifiers_throwException() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Static constructor 'valueOf(...)' wasn't found in ");
        Builder.valueOf(InvalidValueOfModifiers.class, "100");
    }

    @Test
    public void valueOf_failedInvokeValueOf_throwException() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectCause(CoreMatchers.<Throwable>instanceOf(InvocationTargetException.class));
        Builder.valueOf(Fuga.class, "100");
    }

    @Test
    public void valueOf_notPublic_throwException() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectCause(CoreMatchers.<Throwable>instanceOf(IllegalAccessException.class));
        Builder.valueOf(FugaFuga.class, "100");
    }

    static class Fuga {

        public static Fuga valueOf(Object o) {
            throw new NullPointerException();
        }
    }

    static class FugaFuga {

        private static FugaFuga valueOf(Object o) {
            return new FugaFuga();
        }
    }

    static class InvalidValueOfParam {

        public static InvalidValueOfParam valueOf(FugaFuga o) {
            return new InvalidValueOfParam();
        }
    }

    //
    static class InvalidValueOfReturnType {

        public static String valueOf(Object o) {
            return null;
        }
    }

    static class InvalidValueOfModifiers {

        public InvalidValueOfModifiers valueOf(Object o) {
            return null;
        }
    }
}
