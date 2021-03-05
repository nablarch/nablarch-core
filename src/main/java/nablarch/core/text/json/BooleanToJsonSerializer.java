package nablarch.core.text.json;

import java.io.IOException;
import java.io.Writer;

/**
 * Booleanの値をシリアライズするクラス。<br>
 * 受入れ可能なオブジェクトの型は java.lang.Boolean。<br>
 * シリアライズによりJsonの真理値(true / false)として出力する。
 * @author Shuji Kitamura
 */
public class BooleanToJsonSerializer implements JsonSerializer {

    /**
     * {@inheritDoc}
     */
    public void initialize(JsonSerializationSettings settings) {
        //NOOP
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTarget(Class<?> valueClass) {
        return Boolean.class.isAssignableFrom(valueClass);
    }

    /**
     * {@inheritDoc}
     */
    public void serialize(Writer writer, Object value) throws IOException {
        writer.append((Boolean) value ? "true" : "false");
    }
}
