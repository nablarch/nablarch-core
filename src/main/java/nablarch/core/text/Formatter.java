package nablarch.core.text;

/**
 * 値をフォーマットするインターフェース
 *
 * @param <T> フォーマット対象の型
 * @author Ryota Yoshinouchi
 */
public interface Formatter<T> {

    /**
     * フォーマッタの名前を取得する
     *
     * @return フォーマッタの名前
     */
    String getFormatterName();

    /**
     * デフォルトの書式でフォーマットする。
     *
     * @param input フォーマット対象
     * @return フォーマットされた文字列
     */
    String format(T input);

    /**
     * 指定された書式でフォーマットする。
     *
     * @param input   フォーマット対象
     * @param pattern フォーマットの書式
     * @return フォーマットされた文字列
     */
    String format(T input, String pattern);
}
