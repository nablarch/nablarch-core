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
    private String formatterName = "number";

    /**
     * デフォルトのフォーマットパターン
     */
    private String defaultPattern = "";

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
     * 指定するフォーマットは{@link DecimalFormat}の仕様に準拠すること。
     *
     * @param input   フォーマット対象
     * @param pattern フォーマットの書式
     * @return フォーマットされた文字列
     */
    @Override
    public String format(Number input, String pattern) {
        if (input == null) {
            throw new IllegalArgumentException("input must not be null.");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("pattern must not bee null.");
        }

        Locale locale = Locale.getDefault();
        DecimalFormat decimalFormat;
        //Javadocにある以下の記載をもとにDecimalFormatのインスタンスを取得している。
        //https://docs.oracle.com/javase/jp/9/docs/api/java/text/NumberFormat.html より
        //> フォーマットや解析をさらに制御したい場合、あるいはこのような制御をユーザーが使えるようにしたい場合は、
        //> ファクトリ・メソッドから得られるNumberFormatをDecimalFormatにキャストすることもできます。
        //> これはほとんどのロケールで有効ですが、有効にならないロケールの場合に備えて、これはtryブロックに指定してください。
        try {
            decimalFormat = (DecimalFormat) NumberFormat.getInstance(locale);
        } catch (RuntimeException e) {
            //NumberFormat.getInstanceにthrowsの宣言がないためRuntimeExceptionをcatchしている
            throw new IllegalArgumentException("invalid locale for DecimalFormat, locale = " + locale, e);
        }

        try {
            if (!pattern.isEmpty()) {
                decimalFormat.applyPattern(pattern);
            }
            return decimalFormat.format(input);
        } catch (IllegalArgumentException e) {
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
