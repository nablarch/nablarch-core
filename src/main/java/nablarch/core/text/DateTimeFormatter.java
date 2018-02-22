package nablarch.core.text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日付をフォーマットするクラス
 *
 * @author Ryota Yoshinouchi
 */
public class DateTimeFormatter implements Formatter<Date> {

    /**
     * フォーマッタの名前
     */
    private String formatterName = "dateTime";

    /**
     * デフォルトのフォーマットパターン
     */
    private String defaultPattern = "yyyy/MM/dd";

    @Override
    public Class<?> getFormatClass() {
        return Date.class;
    }

    @Override
    public String getFormatterName() {
        return formatterName;
    }

    /**
     * デフォルトの書式で日付をフォーマットする。
     *
     * @param input フォーマット対象
     * @return フォーマットされた文字列　フォーマット対象がnullの場合はnull
     */
    @Override
    public String format(Date input) {
        return format(input, defaultPattern);
    }

    /**
     * 指定された書式で日付をフォーマットする。
     * 指定するフォーマットは{@link SimpleDateFormat}の仕様に準拠すること。
     *
     * @param input   フォーマット対象
     * @param pattern フォーマットの書式
     * @return フォーマットされた文字列　フォーマット対象がnullの場合はnull
     */
    @Override
    public String format(Date input, String pattern) {
        if (input == null) {
            return null;
        }
        if (pattern == null) {
            return input.toString();
        }

        Locale locale = Locale.getDefault();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
            return dateFormat.format(input);
        } catch (IllegalArgumentException e) {
            return input.toString();
        }
    }

    /**
     * フォーマッタの名前を設定する。
     *
     * @param formatterName フォーマッタの名前
     */
    public void setFormatterName(String formatterName) {
        this.formatterName = formatterName;
    }

    /**
     * フォーマットのデフォルトの書式を設定する。
     *
     * @param defaultPattern デフォルトの書式
     */
    public void setDefaultPattern(String defaultPattern) {
        this.defaultPattern = defaultPattern;
    }
}
