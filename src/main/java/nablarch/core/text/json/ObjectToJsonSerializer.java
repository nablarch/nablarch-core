package nablarch.core.text.json;

/**
 * 文字列をシリアライズするクラス。<br>
 * 本クラスは全てのObjectを受入れ、のserializeメソッドはデフォルトシリアライザとして機能する。<br>
 * toString()メソッドの戻り値をシリアライズ対象の文字列とする。<br>
 * シリアライズによりJsonのstringとして出力する。
 * @author Shuji Kitamura
 */
public class ObjectToJsonSerializer extends StringToJsonSerializer {

    /**
     * {@inheritDoc}
     */
    public boolean isTarget(Class<?> valueClass) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected String convertString(Object value) {
        return value.toString();
    }
}
