package nablarch.core.date;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThrows;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import nablarch.core.ThreadContext;
import nablarch.core.repository.SystemRepository;
import nablarch.util.FixedSystemTimeProvider;

import org.junit.After;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

/**
 * {@link SystemTimeUtil}のテストクラス
 * @author Miki Habu
 */
public class SystemTimeUtilTest {
    
    private TimeZone defaultTimeZone;

    /**
     * テスト実施前準備
     * 
     */
    @Before
    public void setUp() {
        defaultTimeZone = TimeZone.getDefault();
        // リポジトリの初期化
        SystemRepository.load(() -> {
            HashMap<String, Object> result = new HashMap<>();
            FixedSystemTimeProvider provider = new FixedSystemTimeProvider();
            provider.setFixedDate("20110107123456");
            result.put("systemTimeProvider", provider);
            return result;
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
        MatcherAssert.assertThat(SystemTimeUtil.getDate(), is(expected));
    }

    /**
     * {@link SystemTimeUtil#getDate()} のテスト。
     * <p/>
     * リポジトリに値がない場合、例外を送出するかどうか。
     */
    @Test
    public void testGetDateErr() {
        SystemRepository.clear();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, SystemTimeUtil::getDate);
        MatcherAssert.assertThat(exception.getMessage(), is("specified systemTimeProvider is not registered in SystemRepository."));
    }
    
    /**
     * {@link SystemTimeUtil#getTimestamp()}のテスト
     */
    @Test
    public void testGetTimestamp() {
        Timestamp expected = Timestamp.valueOf("2011-01-07 12:34:56.000000000");
        MatcherAssert.assertThat(SystemTimeUtil.getTimestamp(), is(expected));
    }

    /**
     * {@link SystemTimeUtil#getLocalDateTime()}のテスト
     * <p/>
     * スレッドコンテキストにタイムゾーンが設定されていない場合、システムデフォルトのタイムゾーンを使用して{@link LocalDateTime}を取得できること。
     */
    @Test
    public void testGetLocalDateTimeDefault() {
        ThreadContext.setTimeZone(null);
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));

        LocalDateTime expected = ZonedDateTime
            .parse(
                "2011-01-07T12:34:56",
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(defaultTimeZone.toZoneId())
            )// 文字列を、システムデフォルトのタイムゾーンの日時と考えるようにしたうえで変換
            .withZoneSameInstant(ZoneId.of("America/Los_Angeles"))// タイムゾーンをアメリカに変更
            .toLocalDateTime();

        MatcherAssert.assertThat(SystemTimeUtil.getLocalDateTime(), is(expected));
    }

    /**
     * {@link SystemTimeUtil#getLocalDateTime()} のテスト。
     * <p/>
     * スレッドコンテキストにタイムゾーンが設定されている場合、スレッドコンテキストのタイムゾーンを使用する。
     */
    @Test
    public void testGetLocalDateTime() {
        ThreadContext.setTimeZone(defaultTimeZone);
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuuMMddHHmmss");
        LocalDateTime expected = LocalDateTime.parse("20110107123456", dtf);

        MatcherAssert.assertThat(SystemTimeUtil.getLocalDateTime(), is(expected));
    }

    /**
     * {@link SystemTimeUtil#getDateString()}のテスト。
     */
    @Test
    public void testGetCurrentDateString() {
        MatcherAssert.assertThat(SystemTimeUtil.getDateString(), is("20110107"));
    }

    /**
     * {@link SystemTimeUtil#getDateTimeString()}のテスト
     */
    @Test
    public void testGetDateTimeString() {
       MatcherAssert.assertThat(SystemTimeUtil.getDateTimeString(), is("20110107123456")); 
    }
    
    /**
     * {@link SystemTimeUtil#getDateTimeMillisString()}のテスト
     */
    @Test
    public void testGetDateTimeMillisString() {
        MatcherAssert.assertThat(SystemTimeUtil.getDateTimeMillisString(), is("20110107123456000"));         
    }
    
}
