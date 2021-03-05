package nablarch.core.text.json;

import java.io.IOException;
import java.io.Writer;

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
