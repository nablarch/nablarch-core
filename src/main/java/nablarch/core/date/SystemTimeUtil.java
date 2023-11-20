package nablarch.core.date;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

import nablarch.core.ThreadContext;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.annotation.Published;

/**
 * システム日付を取得するユーティリティ。
 * <p/>
 * 日時、及び日付の取得処理は{@link SystemTimeProvider}によって提供される。
 * {@link SystemTimeProvider}の実装は、{@link SystemRepository}からコンポーネント名 systemTimeProvider で取得される。
 *
 * @see SystemTimeProvider
 * @author Miki Habu
 */
@Published
public final class SystemTimeUtil {

    /** システム日時取得コンポーネント名。 */
    private static final String TIME_PROVIDER = "systemTimeProvider";

    /** 日付フォーマット */
    private static final String DATE_FORMAT = "yyyyMMdd";

    /** 日時フォーマット(秒まで) */
    private static final String SHORT_FORMAT = "yyyyMMddHHmmss";

    /** 日時フォーマット(ミリ秒まで) */
    private static final String LONG_FORMAT = SHORT_FORMAT + "SSS";

    /** 隠蔽コンストラクタ */
    private SystemTimeUtil() {
    }

    /**
     * システム日時を取得する。
     *
     * @return システム日時
     */
    public static Date getDate() {
        return getProvider().getDate();
    }

    /**
     * システム日時を取得する。
     *
     * @return システム日時
     */
    public static Timestamp getTimestamp() {
        return getProvider().getTimestamp();
    }

    /**
     * システム日時を取得する。
     *
     * @return システム日時
     */
    public static LocalDateTime getLocalDateTime(){
        TimeZone tz = getTimeZone();

        return getTimestamp().toInstant().atZone(tz.toZoneId()).toLocalDateTime();
    }

    /**
     * システム日時を取得する。
     *
     * @return システム日時
     */
    public static LocalDate getLocalDate(){
        TimeZone tz = getTimeZone();

        return getDate().toInstant().atZone(tz.toZoneId()).toLocalDate();
    }

    /**
     * システム日付を yyyyMMdd 形式の文字列で取得する。
     *
     * @return システム日付
     */
    public static String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

        return sdf.format(getProvider().getDate());
    }

    /**
     * システム日時を yyyyMMddHHmmss 形式の文字列で取得する。
     *
     * @return システム日時
     */
    public static String getDateTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat(SHORT_FORMAT);

        return sdf.format(getProvider().getDate());
    }

    /**
     * システム日時を yyyyMMddHHmmssSSS 形式の文字列で取得する。
     *
     * @return システム日時
     */
    public static String getDateTimeMillisString() {
        SimpleDateFormat sdf = new SimpleDateFormat(LONG_FORMAT);

        return sdf.format(getProvider().getDate());
    }

    /**
     * システム日時取得コンポーネントを取得する。
     *
     * @return システム日時取得コンポーネント
     */
    private static SystemTimeProvider getProvider() {
        SystemTimeProvider provider = (SystemTimeProvider) SystemRepository.getObject(TIME_PROVIDER);
        if(provider == null){
            throw new IllegalArgumentException(
                    "specified " + TIME_PROVIDER + " is not registered in SystemRepository.");
        }
        return provider;
    }

    /**
     * タイムゾーンを取得する。
     * タイムゾーンはスレッドコンテキストから取得する。
     * それがNULLの場合はシステムデフォルトのタイムゾーンを取得する。
     *
     * @return タイムゾーン
     */
    private static TimeZone getTimeZone(){
        TimeZone tz = ThreadContext.getTimeZone();
        if (tz == null) {
            tz = TimeZone.getDefault();
        }

        return tz;
    }
}
