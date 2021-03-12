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
    private static final char BEGIN_ARRAY = '[';

    /** 配列の終了文字 */
    private static final char END_ARRAY = ']';

    /** 値のセパレータとなる文字 */
    private static final char VALUE_SEPARATOR = ',';

    /** シリアライズ管理クラス */
    private JsonSerializationManager manager;

    /**
     * コンストラクタ。
     * @param manager シリアライズ管理クラス
     */
    public ArrayToJsonSerializer(JsonSerializationManager manager) {
        this.manager = manager;
    }

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
        return valueClass.isArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Writer writer, Object value) throws IOException {
        writer.append(BEGIN_ARRAY);
        int len = Array.getLength(value);
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                writer.append(VALUE_SEPARATOR);
            }
            Object o = Array.get(value, i);
            manager.getSerializer(o).serialize(writer, o);
        }
        writer.append(END_ARRAY);
    }
}
