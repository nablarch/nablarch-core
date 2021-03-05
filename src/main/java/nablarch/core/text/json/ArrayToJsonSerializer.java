package nablarch.core.text.json;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;

/**
 * 配列をシリアライズするクラス。<br>
 * 受入れ可能なオブジェクトの型は 配列オブジェクト。<br>
 * シリアライズによりJsonのarrayとして出力する。
 * @author Shuji Kitamura
 */
public class ArrayToJsonSerializer implements JsonSerializer {

    /** 配列の開始文字 */
    public static final char BEGIN_ARRAY = '[';

    /** 配列の終了文字 */
    public static final char END_ARRAY = ']';

    /** 値のセパレータとなる文字 */
    public static final char VALUE_SEPARATOR = ',';

    /** シリアライズ管理クラス */
    private JsonSerializationManager manager;

    /** コンストラクタ。 */
    public ArrayToJsonSerializer(JsonSerializationManager manager) {
        this.manager = manager;
    }

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
        return valueClass.isArray();
    }

    /**
     * {@inheritDoc}
     */
    public void serialize(Writer writer, Object value) throws IOException {
        writer.append(BEGIN_ARRAY);
        int len = Array.getLength(value);
        boolean isFirst = true;
        Class<?> prevClass = null;
        JsonSerializer serializer = null;
        for (int i = 0; i < len; i++) {
            if (!isFirst) writer.append(VALUE_SEPARATOR);
            else isFirst = false;
            Object o = Array.get(value, i);
            if (o == null) {
                writer.append("null");
            } else {
                if (prevClass == null || prevClass != o.getClass()) {
                    serializer = manager.getSerializer(o);
                    prevClass = o.getClass();
                }
                serializer.serialize(writer, o);
            }
        }
        writer.append(END_ARRAY);
    }
}
