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
    private String formatterName;

    /**
     * デフォルトのフォーマットパターン
     */
    private String defaultPattern;

    @Override
    public String getFormatterName() {
        return formatterName;
    }

    /**
     * デフォルトの書式で日付をフォーマットする。
     *
     * @param input フォーマット対象
     * @return フォーマットされた文字列
     */
    @Override
    public String format(Date input) {
        return format(input, defaultPattern);
    }

    /**
     * 指定された書式で日付をフォーマットする。
     *
     * @param input   フォーマット対象
     * @param pattern フォーマットの書式
     * @return フォーマットされた文字列
     */
    @Override
    public String format(Date input, String pattern) {
        Locale locale = Locale.getDefault();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
            return dateFormat.format(input);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(
                    String.format("format failed. input = [%s] pattern = [%s] locale = [%s]",
                            input, pattern, locale), e);
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
