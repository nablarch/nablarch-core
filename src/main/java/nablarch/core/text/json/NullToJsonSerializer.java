package nablarch.core.text.json;

import java.io.IOException;
import java.io.Writer;

/**
 * nullをシリアライズするクラス。<br>
 * nullを変換するための特殊なシリアライザとなり、
 * {@link NullToJsonSerializer#isTarget(Class<?>)}による判定は常にtrueを返す。<br>
 * シリアライズによりJsonのnullとして出力する。
 * @author Shuji Kitamura
 */
public class NullToJsonSerializer implements JsonSerializer {

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
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void serialize(Writer writer, Object value) throws IOException {
        writer.append("null");
    }
}
