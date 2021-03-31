package nablarch.core.text.json;

import java.io.IOException;
import java.io.Writer;

/**
 * Booleanの値をシリアライズするクラス。
 * <p>
 * 受入れ可能なオブジェクトの型は java.lang.Boolean。
 * (オートボクシングによりbooleanも対象となる。)<br>
 * シリアライズによりJsonの真理値(true / false)として出力する。
 * </p>
 * @author Shuji Kitamura
 */
public class BooleanToJsonSerializer implements JsonSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(JsonSerializationSettings settings) {
        //NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTarget(Class<?> valueClass) {
        return Boolean.class.isAssignableFrom(valueClass)
                || boolean.class.equals(valueClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Writer writer, Object value) throws IOException {
        writer.append(value.toString());
    }
}
