package nablarch.core.text;

import java.util.List;

/**
 * フォーマッタを保持するクラス。
 *
 * @author Ryota Yoshinouchi
 */
@SuppressWarnings("rawtypes")
public class FormatterConfig {

    /**
     * フォーマッタのリスト
     */
    private List<Formatter> formatters;

    /**
     * フォーマッタのリストを取得する。
     *
     * @return フォーマッタのリスト
     */
    public List<Formatter> getFormatters() {
        return formatters;
    }

    /**
     * フォーマッタのリストを設定する。
     *
     * @param formatters フォーマッタのリスト
     */
    public void setFormatters(List<Formatter> formatters) {
        this.formatters = formatters;
    }
}
