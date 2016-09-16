package nablarch.core.util;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * {@link StringUtil}のテストクラス。
 *
 * @author Hisaaki Sioiri
 */
public class StringUtilTest {

    /** {@link StringUtil#lowerAndTrimUnderScore(String)}のテスト。 */
    @Test
    public void testLowerAndTrimUnderScore() throws Exception {
        assertThat(StringUtil.lowerAndTrimUnderScore("abc"), is("abc"));
        assertThat(StringUtil.lowerAndTrimUnderScore("abc"), is("abc"));
        assertThat(StringUtil.lowerAndTrimUnderScore("ABC"), is("abc"));
        assertThat(StringUtil.lowerAndTrimUnderScore("a_B_c"), is("abc"));
        assertThat(StringUtil.lowerAndTrimUnderScore("A-B-C"), is("a-b-c"));

        assertThat(StringUtil.lowerAndTrimUnderScore(
                "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"),
                is("!\"#$%&'()*+,-./0123456789:;<=>?@abcdefghijklmnopqrstuvwxyz[\\]^`abcdefghijklmnopqrstuvwxyz{|}~"));
    }

    /** {@link StringUtil#lpad(String, int, char)}のテスト。 */
    @Test
    public void lpad() {
        assertEquals("指定桁数まで、指定文字がパディングされること。", "0000000001", StringUtil.lpad("1", 10, '0'));
        assertEquals("指定桁数より1文字短い文字列を指定", "01", StringUtil.lpad("1", 2, '0'));
        assertEquals("指定桁数と同じ文字列を指定", "1111111111", StringUtil.lpad("1111111111", 10, 'a'));
        assertEquals("指定桁数より１文字長い文字列を指定", "123456", StringUtil.lpad("123456", 5, '0'));
        assertEquals("指定文字に全角文字を指定", "００００1", StringUtil.lpad("1", 5, '０'));
    }

    /**
     * {@link nablarch.core.util.StringUtil#lpad(String, int, char)}のテスト。
     *
     * 長さがマイナスの場合例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void lpadLengthMinus() {
        StringUtil.lpad("1", -1, '0');
    }

    /** {@link StringUtil#rpad(String, int, char)}のテスト。 */
    @Test
    public void rpad() {
        assertEquals("指定桁数まで、指定文字がパディングされること。", "1000000000", StringUtil.rpad("1", 10, '0'));
        assertEquals("指定桁数より1文字短い文字列を指定", "10", StringUtil.rpad("1", 2, '0'));
        assertEquals("指定桁数と同じ文字列を指定", "1111111111", StringUtil.rpad("1111111111", 10, 'a'));
        assertEquals("指定桁数より１文字長い文字列を指定", "123456", StringUtil.rpad("123456", 5, '0'));
        assertEquals("指定文字に全角文字を指定", "1００００", StringUtil.rpad("1", 5, '０'));
    }

    /**
     * {@link StringUtil#rpad(String, int, char)}のテスト。
     *
     * 長さがマイナスの場合例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void rpadLengthMinus() {
        StringUtil.rpad("1", -1, '0');
    }

    /** {@link StringUtil#isNullOrEmpty(String)} のテスト。 */
    @Test
    public void isNullOrEmpty() {
        assertTrue(StringUtil.isNullOrEmpty((String) null));
        assertTrue(StringUtil.isNullOrEmpty(""));
        assertFalse(StringUtil.isNullOrEmpty(" "));
        assertFalse(StringUtil.isNullOrEmpty("null"));
    }

    /** {@link StringUtil#isNullOrEmpty(String...)} のテスト。 */
    @Test
    public void isNullOrEmptyVararg() {
        assertTrue(StringUtil.isNullOrEmpty((String[]) null));
        assertTrue(StringUtil.isNullOrEmpty(new String[0]));
        assertTrue(StringUtil.isNullOrEmpty("", null));
        assertFalse(StringUtil.isNullOrEmpty("", null, "not empty"));
    }

    /** {@link StringUtil#isNullOrEmpty(Collection)} のテスト。 */
    @Test
    public void isNullOrEmptyCollection() {
        assertTrue(StringUtil.isNullOrEmpty((Collection) null));
        assertTrue(StringUtil.isNullOrEmpty(Collections.<String>emptyList()));
        assertTrue(StringUtil.isNullOrEmpty(Arrays.asList("", "")));
        assertFalse(StringUtil.isNullOrEmpty(Arrays.asList("", null, "not empty")));
    }

    /** {@link StringUtil#isNullOrEmpty(String)} のテスト。 */
    @Test
    public void hasValue() {
        assertFalse(StringUtil.hasValue((String) null));
        assertFalse(StringUtil.hasValue(""));
        assertTrue(StringUtil.hasValue(" "));
        assertTrue(StringUtil.hasValue("null"));
    }

    /** {@link StringUtil#isNullOrEmpty(String...)} のテスト。 */
    @Test
    public void testHasValueVararg() {
        assertTrue(StringUtil.hasValue("", null, "not empty"));
        assertFalse(StringUtil.hasValue("", null));
        assertFalse(StringUtil.hasValue(new String[0]));
        assertFalse(StringUtil.hasValue((String[]) null));
    }

    /** {@link StringUtil#isNullOrEmpty(Collection)} のテスト。 */
    @Test
    public void testHasValueCollection() {
        assertTrue(StringUtil.hasValue(Arrays.asList("", null, "not empty")));
        assertFalse(StringUtil.hasValue(Arrays.asList("", null)));
        assertFalse(StringUtil.hasValue(Collections.<String>emptyList()));
        assertFalse(StringUtil.hasValue((Collection) null));
    }


    private static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * {@link StringUtil#toString(byte[], java.nio.charset.Charset)} のテスト。<br/>
     *
     * @throws java.io.UnsupportedEncodingException
     *          発生しない
     */
    @Test
    public void testToString() throws UnsupportedEncodingException {

        byte[] bytes = "―～".getBytes(UTF8.name());

        for (String charsetName : new String[]{"UTF-8", "Shift_JIS"}) {
            Charset charset = Charset.forName(charsetName);
            String expected = new String(bytes, charsetName);
            assertEquals(expected, StringUtil.toString(bytes, charset));
        }

        // 対象バイト配列がnullの場合、nullが返却されること
        assertEquals(null, StringUtil.toString(null, UTF8));

        // 対象バイト配列が要素数0の場合、空文字が返却されること
        assertEquals("", StringUtil.toString(new byte[0], UTF8));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToStringNull() throws UnsupportedEncodingException {
        byte[] bytes = "―～".getBytes(UTF8.name());
        StringUtil.toString(bytes, null);
    }

    /**
     * Object -> Stringの変換ができること
     * @throws Exception
     */
    @Test
    public void testObjectToString() throws Exception {
        assertThat("文字列を指定", StringUtil.toString("あいうえお"), is("あいうえお"));
        assertThat("Integerを指定", StringUtil.toString(Integer.valueOf(100)), is("100"));
        assertThat("BigDecimalを指定", StringUtil.toString(new BigDecimal("0.0000000001")), is("0.0000000001"));
        
        class OverrideToString {

            @Override
            public String toString() {
                return "test class";
            }
        }
        assertThat("toStringをオーバライドしたクラスを指定", StringUtil.toString(new OverrideToString()), is("test class"));
    }

    /**
     * {@link StringUtil#getBytes(String, Charset)} のテスト。<br/>
     *
     * @throws java.io.UnsupportedEncodingException
     *          発生しない
     */
    @Test
    public void testGetBytes() throws UnsupportedEncodingException {

        String string = "―～";

        for (String charsetName : new String[]{"UTF-8", "Shift_JIS"}) {
            Charset charset = Charset.forName(charsetName);
            byte[] expected = string.getBytes(charsetName);
            assertArrayEquals(expected, StringUtil.getBytes(string, charset));
        }

        // 対象文字列がnullの場合、nullが返却されること
        assertEquals(null, StringUtil.getBytes(null, UTF8));

        // 対象文字列が空文字の場合、要素数0のバイト配列が返却されること
        assertArrayEquals(new byte[0], StringUtil.getBytes("", UTF8));
    }

    /**
     * {@link StringUtil#getBytes(String, Charset)} のテスト。<br/>
     * Charsetがnullの場合、例外が発生すること。
     *
     * @throws java.io.UnsupportedEncodingException
     *          発生しない
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetBytesNull() throws UnsupportedEncodingException {
        StringUtil.getBytes("hoge", null);
    }

    /** {@link StringUtil#insert(String, String, int...)}のテスト。 */
    @Test
    public void testInsert() {
        // 全ての位置に区切り文字が挿入されるケース
        assertThat(StringUtil.insert("0123456789", "-", 2, 3, 4), is("01-234-5678-9"));
        // 最後の位置の区切り文字が挿入されないケース
        assertThat(StringUtil.insert("012345678", "-", 2, 3, 4), is("01-234-5678"));
        // 全く区切り文字が挿入されないケース
        assertThat(StringUtil.insert("12", "-", 2, 3, 4), is("12"));
        // 空文字のケース
        assertThat(StringUtil.insert("", "-", 2, 3, 4), is(""));
    }

    /**
     * {@link StringUtil#insert(String, String, int...)}のテスト。<br/>
     * 対象文字列がnullの場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInsertTargetNull() {
        StringUtil.insert(null, "-", 4, 4, 4);
    }

    /**
     * {@link StringUtil#insert(String, String, int...)}のテスト。<br/>
     * 区切り文字がnullの場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInsertDelimiterNull() {
        StringUtil.insert("1234567890123456", null, 4, 4, 4);
    }

    /**
     * {@link StringUtil#insert(String, String, int...)}のテスト。<br/>
     * 挿入間隔がnullの場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInsertIntervalsNull() {
        StringUtil.insert("1234567890123456", "-", null);
    }

    /**
     * {@link StringUtil#insert(String, String, int...)}のテスト。<br/>
     * 挿入間隔の要素数が0の場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInsertIntervalsEmpty() {
        StringUtil.insert("1234567890123456", "-", new int[0]);
    }

    /**
     * {@link StringUtil#insert(String, String, int...)}のテスト。<br/>
     * 挿入間隔がマイナスの場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInsertIntervalsMinus() {
        StringUtil.insert("1234567890123456", "-", new int[] {1, -1});
    }

    /** {@link StringUtil#insertFromRight(String, String, int...)}のテスト。<br/> */
    @Test
    public void testInsertFromRight() {
        // 全ての位置に区切り文字が挿入されるケース
        assertThat(StringUtil.insertFromRight("1234567890", "-", 4, 3, 2), is("1-23-456-7890"));
        // 最後の位置の区切り文字が挿入されないケース
        assertThat(StringUtil.insertFromRight("123456789", "-", 4, 3, 2), is("12-345-6789"));
        // 全く区切り文字が挿入されないケース
        assertThat(StringUtil.insertFromRight("1234", "-", 4, 3, 2), is("1234"));
        // 空文字のケース
        assertThat(StringUtil.insertFromRight("", "-", 4, 3, 2), is(""));
    }

    /**
     * {@link StringUtil#insertFromRight(String, String, int...)}のテスト。<br/>
     * 対象文字列がnullの場合、例外が発生すること
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInsertFromRightTargetNull() {
        StringUtil.insertFromRight(null, "-", 4, 3, 2);
    }

    /**
     * {@link StringUtil#insertFromRight(String, String, int...)}のテスト。<br/>
     * 区切り文字がnullの場合、例外が発生すること
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInsertFromRightDelimiterNull() {
        StringUtil.insertFromRight("12345", null, 4, 3, 2);
    }


    /** {@link StringUtil#insertRepeatedly(String, String, int)}のテスト。<br/> */
    @Test
    public void testInsertRepeatedly() {
        // 対象文字列数が挿入間隔ちょうど（区切り文字の倍数）のケース
        assertThat(StringUtil.insertRepeatedly("1234567890123456", "-", 4), is("1234-5678-9012-3456"));
        // 対象文字列数が挿入間隔ちょうど（区切り文字の倍数）でないケース
        assertThat(StringUtil.insertRepeatedly("123456789012345", "-", 4), is("1234-5678-9012-345"));
        // 対象文字列数が挿入間隔以下のケース
        assertThat(StringUtil.insertRepeatedly("1234", "-", 4), is("1234"));
        // 空文字のケース
        assertThat(StringUtil.insertRepeatedly("", "-", 4), is(""));
    }

    /**
     * {@link StringUtil#insertRepeatedly(String, String, int)}のテスト。<br/>
     * 対象文字列がnullの場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInsertRepeatedlyStringNull() {
        StringUtil.insertRepeatedly(null, ",", 4);
    }

    /**
     * {@link StringUtil#insertRepeatedly(String, String, int)}のテスト。<br/>
     * 区切り文字がnullの場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInsertRepeatedlyDelimiterNull() {
        StringUtil.insertRepeatedly("12345", null, 4);
    }

    /** {@link StringUtil#insertRepeatedlyFromRight(String, String, int)} のテスト。<br/> */
    @Test
    public void testInsertRepeatedlyFromRight() {
        // 対象文字列数が挿入間隔ちょうど（区切り文字の倍数）のケース
        assertThat(StringUtil.insertRepeatedlyFromRight("123456789", ",", 3), is("123,456,789"));
        // 対象文字列数が挿入間隔ちょうど（区切り文字の倍数）でないケース
        assertThat(StringUtil.insertRepeatedlyFromRight("1234567890", ",", 3), is("1,234,567,890"));
        // 対象文字列数が挿入間隔以下のケース
        assertThat(StringUtil.insertRepeatedlyFromRight("123", ",", 3), is("123"));
        // 空文字のケース
        assertThat(StringUtil.insertRepeatedlyFromRight("", ",", 3), is(""));
    }

    /**
     * {@link StringUtil#insertRepeatedlyFromRight(String, String, int)} のテスト。<br/>
     * 対象文字列がnullの場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInsertRepeatedlyFromRightTargetNull() {
        StringUtil.insertRepeatedlyFromRight(null, ",", 3);
    }

    /**
     * {@link StringUtil#insertRepeatedlyFromRight(String, String, int)} のテスト。<br/>
     * 区切り文字がnullの場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInsertRepeatedlyFromRightDelimiterNull() {
        StringUtil.insertRepeatedlyFromRight("12345", null, 3);
    }

    /** {@link StringUtil#repeat(Object, int)}のテスト。<br/> */
    @Test
    public void testRepeat() {
        assertThat(StringUtil.repeat("1", 10), is("1111111111"));
        assertThat(StringUtil.repeat("1", 1), is("1"));
        assertThat(StringUtil.repeat("123", 2), is("123123"));
        assertThat(StringUtil.repeat("", 10), is(""));
        assertThat(StringUtil.repeat("1", 0), is(""));
    }

    /**
     * {@link StringUtil#repeat(Object, int)}のテスト。<br/>
     * 繰り返し対象文字列がnullの場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRepeatNull() {
        StringUtil.repeat(null, 1);
    }

    /**
     * {@link StringUtil#repeat(Object, int)}のテスト。<br/>
     * 繰り返し回数が負数の場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRepeatNegative() {
        StringUtil.repeat("1", -1);
    }

    /**
     * {@link StringUtil#toArray(java.util.Collection)}のテスト。<br/>
     * コレクションが文字列配列に変換されること。
     */
    @Test
    public void testToArray() {
        List<String> list = Arrays.asList("foo", "bar");
        assertArrayEquals(new String[]{"foo", "bar"}, StringUtil.toArray(list));
    }

    /** nullが許容されないこと */
    @Test(expected = IllegalArgumentException.class)
    public void testToArrayFail() {
        StringUtil.toArray(null);
    }

    /** 行末の文字列が切り落とされること。 */
    @Test
    public void testChomp() {
        assertThat(StringUtil.chomp("hoge\n", "\n"), is("hoge")); // 行末の文字が切り落とされる
        assertThat(StringUtil.chomp("fuga", "\n"), is("fuga"));   // マッチしないので切り落とされない
        assertThat(StringUtil.chomp("", "\n"), is(""));           // 空文字OK
        assertThat(StringUtil.chomp("hoge", ""), is("hoge"));     // 空文字OK
    }

    /** nullが許容されないこと */
    @Test(expected = IllegalArgumentException.class)
    public void testTChompTargetNull() {
        StringUtil.chomp(null, "aaa");
    }

    /** nullが許容されないこと */
    @Test(expected = IllegalArgumentException.class)
    public void testTChompEndNull() {
        StringUtil.chomp("end", null);
    }

    /** 配列が結合されること。 */
    @Test
    public void testMerge() {
        assertArrayEquals(
                new String[]{"1", "2", "3", "4", "5"},
                StringUtil.merge(
                        new String[]{"1", "2"},
                        new String[]{"3", "4"},
                        new String[]{"5"},
                        new String[0])
        );
        assertArrayEquals(new String[0], StringUtil.merge(new String[0]));
    }

    /** nullが許容されないこと。 */
    @Test(expected = IllegalArgumentException.class)
    public void testMergeFail() {
        StringUtil.merge((String[][]) null);
    }

    /** {@link StringUtil#nullToEmpty(String)}のテスト */
    @Test
    public void testNullToEmpty() {
        assertThat(StringUtil.nullToEmpty(null), is(""));
        assertThat(StringUtil.nullToEmpty(""), is(""));
        assertThat(StringUtil.nullToEmpty("hoge"), is("hoge"));
    }

    /**
     * {@link StringUtil#join(String, List)} のテスト
     */
    @Test
    public void testJoin() {
        {
            List<String> list = new ArrayList<String>();
            list.add("val1");
            list.add("val2");
            list.add("val3");
            assertThat(StringUtil.join(",", list), is("val1,val2,val3"));
            assertThat(StringUtil.join(":", list), is("val1:val2:val3"));
        }
        {
            List<String> list = new ArrayList<String>();
            assertThat(StringUtil.join(",", list), is(""));
        }
        {
            List<String> list = new ArrayList<String>();
            list.add("");
            assertThat(StringUtil.join(",", list), is(""));
        }
        {
            try {
                StringUtil.join(",", null);
                fail("例外が発生するはず。");
            } catch (NullPointerException ne) {
                // OK
            }
        }
    }

    /**
     * {@link StringUtil#split(String, String)}および{@link StringUtil#split(String, String, boolean)} のテスト
     */
    @Test
    public void testSplit() {
        {

            List<String> list = new ArrayList<String>();
            list.add(" val1 ");
            list.add(" val2 ");
            list.add(" val3 ");
            assertThat(StringUtil.split(" val1 , val2 , val3 ", ","), is(list));
        }
        {
            List<String> list = new ArrayList<String>();
            list.add("");
            assertThat(StringUtil.split("", ","), is(list));
        }
        {

            List<String> list = new ArrayList<String>();
            list.add("val1");
            list.add("val2");
            list.add("val3");
            assertThat(StringUtil.split(" val1 , val2 , val3 ", ",", true), is(list));
        }
        {

            List<String> list = new ArrayList<String>();
            list.add(" val1 ");
            list.add(" val2 ");
            list.add(" val3 ");
            assertThat(StringUtil.split(" val1 , val2 , val3 ", ",", false), is(list));
        }
        {
            List<String> list = new ArrayList<String>();
            list.add("");
            assertThat(StringUtil.split("", ","), is(list));
        }
    }
}

