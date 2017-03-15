package nablarch.core.util;


import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import nablarch.core.ThreadContext;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link DateUtil}のテストクラス
 *
 * @author Miki Habu
 */
public class DateUtilTest {

    @Before
    public void setUp() {
        ThreadContext.clear();
        // 強制的にロケールを変更
        Locale.setDefault(Locale.JAPANESE);
    }

    /** {@link DateUtil#getDate(String)}のテスト。 */
    @Test
    public void testGetDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String testDate = "20101123";
        Date expected = null;
        try {
            expected = sdf.parse(testDate);
        } catch (ParseException e) {
            fail();
        }
        assertEquals("正常系", expected, DateUtil.getDate(testDate));

        try {
            DateUtil.getDate("2010/11/10");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("例外発生",
                    "the string was not formatted yyyyMMdd. date = 2010/11/10.",
                    e.getMessage());
        }

    }

    /**
     * ThreadLocalに依存しない{@link DateUtil#formatDate(Date, String)}のテスト
     * @throws Exception
     */
    @Test
    public void testFormatDateNotDependThreadLocal() throws Exception {
        // -------------------------------------------------- clear the ThreadContext
        ThreadContext.clear();
        
        // -------------------------------------------------- execute
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 0, 2);
        final String result = DateUtil.formatDate(calendar.getTime(), "GGyyyy-MM-dd");
        
        // -------------------------------------------------- assert
        assertThat("デフォルトロケールは、jaなので西暦とフォーマットされること", result, is("西暦2016-01-02"));
    }

    /** {@link DateUtil#formatDate}のテスト。 */
    @Test
    public void testFormatDate() {
        assertEquals("正常系", "2010年01月01日", DateUtil.formatDate("20100101",
                "yyyy年MM月dd日"));

        try {
            DateUtil.formatDate("2010/11/10", "yyyyMMdd");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("例外発生",
                    "the string was not formatted yyyyMMdd. date = 2010/11/10.",
                    e.getMessage());
        }
    }
    
    /** {@link nablarch.core.util.DateUtil#addDay}のテスト。 */
    @Test
    public void testAddDay() {
        assertEquals("1日後", "20101111", DateUtil.addDay("20101110", 1));
        assertEquals("2日後", "20101112", DateUtil.addDay("20101110", 2));
        assertEquals("月跨ぎ(翌月)", "20101201", DateUtil.addDay("20101130", 1));
        assertEquals("年跨ぎ(翌年)", "20110101", DateUtil.addDay("20101231", 1));
        assertEquals("1日前", "20101109", DateUtil.addDay("20101110", -1));
        assertEquals("2日前", "20101108", DateUtil.addDay("20101110", -2));
        assertEquals("月跨ぎ(前月)", "20101031", DateUtil.addDay("20101101", -1));
        assertEquals("年跨ぎ(前年)", "20091231", DateUtil.addDay("20100101", -1));
        assertEquals("閏年", "20080229", DateUtil.addDay("20080228", 1));
        assertEquals("閏年", "20080301", DateUtil.addDay("20080229", 1));
    }

    /** {@link nablarch.core.util.DateUtil#addMonth(String, int)}のテスト。 */
    @Test
    public void testAddMonth() {
        // 年月日
        assertEquals("加算", "20101030", DateUtil.addMonth("20100930", 1));
        assertEquals("減算", "20100830", DateUtil.addMonth("20100930", -1));
        assertEquals("閏年", "20040229", DateUtil.addMonth("20030429", 10));

        // 年月
        assertEquals("加算", "201010", DateUtil.addMonth("201009", 1));
        assertEquals("減算", "201008", DateUtil.addMonth("201009", -1));
        assertEquals("閏年", "200402", DateUtil.addMonth("200304", 10));
    }

    /** {@link nablarch.core.util.DateUtil#getDays}のテスト */
    @Test
    public void testGetDays() {
        assertEquals("同日", 0, DateUtil.getDays("20101110", "20101110"));
        assertEquals("1日(from < to)", 1, DateUtil.getDays("20101110",
                "20101111"));
        assertEquals("2日(from < to)", 2, DateUtil.getDays("20101110",
                "20101112"));
        assertEquals("月跨ぎ(from < to)", 1, DateUtil.getDays("20101130",
                "20101201"));
        assertEquals("年跨ぎ(from < to)", 1, DateUtil.getDays("20101231",
                "20110101"));
        assertEquals("1日(from > to)", -1, DateUtil.getDays("20101110",
                "20101109"));
        assertEquals("2日(from > to)", -2, DateUtil.getDays("20101110",
                "20101108"));
        assertEquals("月跨ぎ(from > to)", -1, DateUtil.getDays("20101101",
                "20101031"));
        assertEquals("年跨ぎ(from > to)", -1, DateUtil.getDays("20100101",
                "20091231"));
    }

    /** {@link DateUtil#getMonths(String, String)}のテスト。 */
    @Test
    public void testGetMonths() {
        assertEquals("同じ月(年月)", 0, DateUtil.getMonths("201102", "201102"));
        assertEquals("同じ月(年月日:日付は同じ)", 0, DateUtil.getMonths("20110201",
                "20110201"));
        assertEquals("同じ月(年月日:日付は異なる)", 0, DateUtil.getMonths("20110201",
                "20110228"));

        assertEquals("1ヶ月後(年月)", 1, DateUtil.getMonths("201102", "201103"));
        assertEquals("1ヶ月後(年月日)", 1, DateUtil.getMonths("20110201",
                "20110301"));

        assertEquals("1ヶ月前(年月)", -1, DateUtil.getMonths("201102", "201101"));
        assertEquals("1ヶ月前(年月日)", -1, DateUtil.getMonths("20110201",
                "20110101"));

        assertEquals("１年後(年月)", 12, DateUtil.getMonths("201102", "201202"));
        assertEquals("１年後(年月日)", 12, DateUtil.getMonths("20110201",
                "20120228"));

        assertEquals("１年前(年月)", -12, DateUtil.getMonths("201102", "201002"));
        assertEquals("１年前(年月日)", -12, DateUtil.getMonths("20110201",
                "20100228"));

        assertEquals("１年＋1ヶ月後(年月)", 13, DateUtil.getMonths("201102", "201203"));
        assertEquals("１年＋1ヶ月後(年月日)", 13, DateUtil.getMonths("20110201",
                "20120315"));

        assertEquals("１年＋1ヶ月前(年月)", -13, DateUtil.getMonths("201102",
                "201001"));
        assertEquals("１年＋1ヶ月前(年月日)", -13, DateUtil.getMonths("20111231",
                "20101125"));

        assertEquals("フォーマットが異なる場合", 1, DateUtil.getMonths("20110331",
                "201104"));
        assertEquals("フォーマットが異なる場合", -12, DateUtil.getMonths("201103",
                "20100315"));
    }


    /** {@link DateUtil#getMonthEndDate(String)}のテスト。 */
    @Test
    public void testGetMonthEndDate() {
        assertEquals("年月指定", "20110331", DateUtil.getMonthEndDate("201103"));
        assertEquals("年月指定", "20110430", DateUtil.getMonthEndDate("201104"));
        assertEquals("年月指定(閏年2月)", "20080229", DateUtil.getMonthEndDate(
                "200802"));
        assertEquals("年月指定(閏年以外2月)", "20110228", DateUtil.getMonthEndDate(
                "201102"));

        assertEquals("年月日指定", "20110331", DateUtil.getMonthEndDate("20110303"));
        assertEquals("年月指定", "20110430", DateUtil.getMonthEndDate("20110429"));
        assertEquals("年月日指定(閏年2月)", "20080229", DateUtil.getMonthEndDate(
                "20080205"));
        assertEquals("年月日指定(閏年以外2月)", "20110228", DateUtil.getMonthEndDate(
                "20110201"));
    }

    /**
     * {@link DateUtil#isValid(String, String)}のテスト。<br/>
     * {@link DateUtil#getParsedDate(String, String)}のテストも兼ねています。
     */
    @Test
    public void testIsValid() {

        ThreadContext.setLanguage(Locale.JAPANESE);

        // null、空文字
        try {
            DateUtil.isValid(null, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("format mustn't be null or empty. format=null", e.getMessage());
        }
        try {
            DateUtil.isValid(null, "yyyy/MM/dd");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("date mustn't be null.", e.getMessage());
        }

        try {
            DateUtil.isValid("20110909", null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("format mustn't be null or empty. format=null", e.getMessage());
        }

        try {
            DateUtil.isValid("", "");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("format mustn't be null or empty. format=", e.getMessage());
        }

        assertFalse(DateUtil.isValid("", "yyyy/MM/dd"));

        try {
            DateUtil.isValid("20110909", "");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("format mustn't be null or empty. format=", e.getMessage());
        }

        // フォーマット通り
        assertTrue(DateUtil.isValid("20110909", "yyyyMMdd"));
        assertTrue(DateUtil.isValid("14 11 2012", "dd MMM yyyy"));
        assertTrue(DateUtil.isValid("14 Nov 2012", "dd MMM yyyy", Locale.ENGLISH));

        // フォーマット通りではない
        assertFalse(DateUtil.isValid("20110909", "yyyy/MM/dd"));
        assertFalse(DateUtil.isValid("2011/09/09a", "yyyy/MM/dd"));
        assertFalse(DateUtil.isValid("2011/09/09aaa", "yyyy/MM/dd"));
        assertFalse(DateUtil.isValid("2011/09/09 ", "yyyy/MM/dd"));
        assertFalse(DateUtil.isValid("2011/09/012", "yyyy/MM/dd"));
        assertFalse(DateUtil.isValid("14 Nov 2012", "dd MMM yyyy"));
        assertFalse(DateUtil.isValid("14 11 2012", "dd MMM yyyy", Locale.ENGLISH));

        // 実在しない日
        assertFalse(DateUtil.isValid("2011/09/32", "yyyy/MM/dd"));
    }

    /**
     * {@link DateUtil#formatDate(java.util.Date, java.lang.String)}と
     * {@link DateUtil#formatDate(java.util.Date, java.lang.String, java.util.Locale)}のテスト。
     */
    @Test
    public void testFormatDateObject() throws Exception {

        Date date = new SimpleDateFormat("yyyy/MM/dd").parse("2012/11/13");
        String format = "yyyy-MMM-dd";

        /*
         * タイムゾーンに依存せずにフォーマットされることを確認するための設定。
         * 
         * タイムゾーンに依存すると、ブラジル時間で計算されてフォーマット結果が「2012/11/12」になります。
         * フォーマットされるたびに日付が減っていくという不具合で、MULで実際に発生しました。
         * 
         */
        ThreadContext.setTimeZone(TimeZone.getTimeZone("Brasilia Time, Brazil/East"));

        ThreadContext.setLanguage(Locale.JAPANESE);
        Locale.setDefault(Locale.CHINESE);
        Locale locale = Locale.ENGLISH;

        // dateパラメータがnullの場合。
        try {
            DateUtil.formatDate((Date) null, format);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("date must not be null."));
        }

        try {
            DateUtil.formatDate(null, format, locale);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("date must not be null."));
        }

        // formatパラメータがnullの場合。
        try {
            DateUtil.formatDate(date, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("format must not be null."));
        }

        try {
            DateUtil.formatDate(date, null, locale);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("format must not be null."));
        }

        // ロケールを指定しない場合
        assertThat(DateUtil.formatDate(date, format), is("2012-11-13"));

        // ロケールを指定した場合。
        assertThat(DateUtil.formatDate(date, format, locale), is("2012-Nov-13"));

        // 不正なフォーマットの場合。
        String invalidFormat = "yyyyMMddA";
        try {
            DateUtil.formatDate(date, invalidFormat);
            fail("IllegalArgumentExceptionがスローされる。");
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            assertThat(msg, containsString("format failed."));
            assertThat(msg, containsString("format = [yyyyMMddA]"));
            assertThat(msg, containsString("locale = [ja]"));
        }
        try {
            DateUtil.formatDate(date, invalidFormat, locale);
            fail("IllegalArgumentExceptionがスローされる。");
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            assertThat(msg, containsString("format failed."));
            assertThat(msg, containsString("format = [yyyyMMddA]"));
            assertThat(msg, containsString("locale = [en]"));
        }

        try {
            DateUtil.formatDate(date, "yyyy/MM/dd", null);
            fail("IllegalArgumentExceptionがスローされる。");
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            assertThat(msg, is("locale must not be null."));
        }

        Locale.setDefault(Locale.JAPANESE);
    }
}

