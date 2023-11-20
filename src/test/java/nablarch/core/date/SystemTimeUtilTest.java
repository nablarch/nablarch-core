package nablarch.core.date;

import static org.hamcrest.CoreMatchers.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import nablarch.core.ThreadContext;
import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;
import nablarch.util.FixedSystemTimeProvider;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link SystemTimeUtil}のテストクラス
 * @author Miki Habu
 */
public class SystemTimeUtilTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private TimeZone defaultTimeZone;

    /**
     * テスト実施前準備
     * 
     */
    @Before
    public void setUp() {
        defaultTimeZone = TimeZone.getDefault();
        // リポジトリの初期化
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                HashMap<String, Object> result = new HashMap<String, Object>();
                FixedSystemTimeProvider provider = new FixedSystemTimeProvider();
                provider.setFixedDate("20110107123456");
                result.put("systemTimeProvider", provider);
                return result;
            }
        });
    }

    /**
     * テスト実施後処理
     *
     */
    @After
    public void tearDown() {
        TimeZone.setDefault(defaultTimeZone);
    }

    /**
     * {@link SystemTimeUtil#getDate()}のテスト
     * 
     * @throws Exception 例外
     */
    @Test
    public void testGetDate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date expected = sdf.parse("20110107123456000");
        Assert.assertThat(SystemTimeUtil.getDate(), is(expected));
    }

    /**
     * {@link SystemTimeUtil#getDate()} のテスト。
     * <p/>
     * リポジトリに値がない場合、例外を送出するかどうか。
     * @throws Exception
     */
    @Test
    public void testGetDateErr() throws Exception {
        SystemRepository.clear();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("specified systemTimeProvider is not registered in SystemRepository.");

        SystemTimeUtil.getDate();
    }
    
    /**
     * {@link SystemTimeUtil#getTimestamp()}のテスト
     */
    @Test
    public void testGetTimestamp() {
        Timestamp expected = Timestamp.valueOf("2011-01-07 12:34:56.000000000");
        Assert.assertThat(SystemTimeUtil.getTimestamp(), is(expected));
    }

    /**
     * {@link SystemTimeUtil#getLocalDateTime()}のテスト
     * <p/>
     * スレッドコンテキストのTimeZoneがnullの場合、システムデフォルトのタイムゾーンを取得するかどうか。
     */
    @Test
    public void testGetLocalDateTimeDefault() {
        ThreadContext.setTimeZone(null);
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));

        LocalDateTime expected = ZonedDateTime
            .parse(
                "2011-01-07T12:34:56",
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.of(String.valueOf(defaultTimeZone.toZoneId())))
            )// 文字列を、システムデフォルトのタイムゾーンの日時と考えるようにしたうえで変換
            .withZoneSameInstant(ZoneId.of("America/Los_Angeles"))// タイムゾーンをアメリカに変更
            .toLocalDateTime();

        Assert.assertNull(ThreadContext.getTimeZone());
        Assert.assertEquals(expected, SystemTimeUtil.getLocalDateTime());
    }

    /**
     * {@link SystemTimeUtil#getLocalDateTime()} のテスト。
     * <p/>
     * スレッドコンテキストのTimeZoneがnullではない場合、システムデフォルトのタイムゾーンを取得しないかどうか。
     */
    @Test
    public void testGetLocalDateTime() {
        ThreadContext.setTimeZone(TimeZone.getTimeZone(String.valueOf(defaultTimeZone.toZoneId())));
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuuMMddHHmmss");
        LocalDateTime expected = LocalDateTime.parse("20110107123456", dtf);

        Assert.assertEquals(expected, SystemTimeUtil.getLocalDateTime());
    }

    /**
     * {@link SystemTimeUtil#getLocalDate()}のテスト
     */
    @Test
    public void testGetLocalDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuuMMdd");
        LocalDate expected = LocalDate.parse("20110107", dtf);
        Assert.assertEquals(expected, SystemTimeUtil.getLocalDate());
    }

    /**
     * {@link SystemTimeUtil#getDateString()}のテスト。
     */
    @Test
    public void testGetCurrentDateString() {
        Assert.assertThat(SystemTimeUtil.getDateString(), is("20110107"));
    }

    /**
     * {@link SystemTimeUtil#getDateTimeString()}のテスト
     */
    @Test
    public void testGetDateTimeString() {
       Assert.assertThat(SystemTimeUtil.getDateTimeString(), is("20110107123456")); 
    }
    
    /**
     * {@link SystemTimeUtil#getDateTimeMillisString()}のテスト
     */
    @Test
    public void testGetDateTimeMillisString() {
        Assert.assertThat(SystemTimeUtil.getDateTimeMillisString(), is("20110107123456000"));         
    }
    
}
