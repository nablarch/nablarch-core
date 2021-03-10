package nablarch.core.text.json;

/**
 * 任意のオブジェクトを文字列としてシリアライズするクラス。<br>
 * 本クラスは全てのObjectを受け入れてシリアライズする。<br>
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
