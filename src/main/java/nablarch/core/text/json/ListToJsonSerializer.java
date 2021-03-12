package nablarch.core.text.json;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Listオブジェクトをシリアライズするクラス。<br>
 * 受入れ可能なオブジェクトの型は java.util.List。<br>
 * シリアライズによりJsonのarrayとして出力する。
 * @author Shuji Kitamura
 */
public class ListToJsonSerializer implements JsonSerializer {

    /** arrayの開始文字 */
    private static final char BEGIN_ARRAY = '[';

    /** arrayの終了文字 */
    private static final char END_ARRAY = ']';

    /** 値のセパレータとなる文字 */
    private static final char VALUE_SEPARATOR = ',';

    /** シリアライズ管理クラス */
    private JsonSerializationManager manager;

    /**
     * コンストラクタ。
     * @param manager シリアライズ管理クラス
     */
    public ListToJsonSerializer(JsonSerializationManager manager) {
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
        return List.class.isAssignableFrom(valueClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Writer writer, Object value) throws IOException {
        writer.append(BEGIN_ARRAY);
        List<?> list = (List<?>) value;
        int len = list.size();
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                writer.append(VALUE_SEPARATOR);
            }
            Object o = list.get(i);
            manager.getSerializer(o).serialize(writer, o);
        }
        writer.append(END_ARRAY);
    }
}
