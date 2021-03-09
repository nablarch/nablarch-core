package nablarch.core.text.json;

import nablarch.core.util.StringUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Booleanの値をシリアライズするクラス。<br>
 * 受入れ可能なオブジェクトの型は java.util.Date。<br>
 * シリアライズによりJsonのstringとして出力する。
 * @author Shuji Kitamura
 */
public class DateToJsonSerializer extends StringToJsonSerializer {

    /** 設定から取得する日時フォーマットのプロパティ名 */
    private static final String DATE_PATTERN_PROPERTY = "datePattern";

    /** デフォルトの日時フォーマット */
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /** 日時のフォーマッタ */
    private DateFormat dateFormat = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(JsonSerializationSettings settings) {
        dateFormat = getDateFormat(settings);
    }

    /**
     * 日時フォーマットを取得する。
     * @param settings シリアライズ設定
     * @return 日時フォーマット
     */
    protected DateFormat getDateFormat(JsonSerializationSettings settings) {
        String datePattern = settings.getProp(DATE_PATTERN_PROPERTY);
        return !StringUtil.isNullOrEmpty(datePattern) ?  new SimpleDateFormat(datePattern) : new SimpleDateFormat(DEFAULT_DATE_PATTERN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTarget(Class<?> valueClass) {
        return Date.class.isAssignableFrom(valueClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String convertString(Object value) {
        return dateFormat.format(convertDate(value));
    }

    /**
     * オブジェクトからシリアライズ対象のDateオブジェクトを取得する。
     */
    protected Date convertDate(Object value) {
        return (Date)value;
    }
}
