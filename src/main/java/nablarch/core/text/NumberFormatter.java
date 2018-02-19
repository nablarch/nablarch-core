package nablarch.core.text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * 数値をフォーマットするクラス。
 *
 * @author Ryota Yoshinouchi
 */
public class NumberFormatter implements Formatter<Number> {

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
     * デフォルトの書式で数値をフォーマットする。
     *
     * @param input フォーマット対象
     * @return フォーマットされた文字列
     */
    @Override
    public String format(Number input) {
        return format(input, defaultPattern);
    }

    /**
     * 指定された書式で数値をフォーマットする。
     *
     * @param input   フォーマット対象
     * @param pattern フォーマットの書式
     * @return フォーマットされた文字列
     */
    @Override
    public String format(Number input, String pattern) {
        Locale locale = Locale.getDefault();
        try {
            DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(locale);
            decimalFormat.applyPattern(pattern);
            return decimalFormat.format(input);
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
