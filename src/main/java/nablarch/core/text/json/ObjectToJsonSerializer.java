package nablarch.core.text.json;

import java.io.IOException;
import java.io.Writer;

/**
 * 任意のオブジェクトを文字列としてシリアライズするクラス。<br>
 * 本クラスは全てのObjectを受け入れてシリアライズする。<br>
 * toString()メソッドの戻り値をシリアライズ対象の文字列とする。<br>
 * シリアライズによりJsonのstringとして出力する。
 * @author Shuji Kitamura
 */
public class ObjectToJsonSerializer implements JsonSerializer {

    /** シリアライズ管理クラス */
    protected final JsonSerializationManager manager;

    /** stringシリアライザ */
    protected JsonSerializer stringSerializer;

    /**
     * コンストラクタ。
     * @param manager シリアライズ管理クラス
     */
    public ObjectToJsonSerializer(JsonSerializationManager manager) {
        this.manager = manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(JsonSerializationSettings settings) {
        stringSerializer = manager.getStringSerializer();
    }

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
    public void serialize(Writer writer, Object value) throws IOException {
        if (value == null) {
            throw new NullPointerException("value must not be null.");
        }
        stringSerializer.serialize(writer, value.toString());
    }

}
