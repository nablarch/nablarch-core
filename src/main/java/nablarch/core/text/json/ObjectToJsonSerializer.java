package nablarch.core.text.json;

/**
 * Objectを文字列としてシリアライズするクラス。<br>
 * 本クラスは全てのObjectを受入れ、デフォルトシリアライザとして機能する。<br>
 * toString()メソッドの戻り値をシリアライズ対象の文字列とする。<br>
 * シリアライズによりJsonのstringとして出力する。
 * @author Shuji Kitamura
 */
public class ObjectToJsonSerializer extends StringToJsonSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTarget(Class<?> valueClass) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String convertString(Object value) {
        return value.toString();
    }
}
