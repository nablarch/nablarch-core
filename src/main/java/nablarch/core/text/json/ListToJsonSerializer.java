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
     * シリアライズ管理クラスを取得します。
     * @return シリアライズ管理クラス
     */
    protected JsonSerializationManager getJsonSerializationManager() {
        return manager;
    }

    /**
     * 値がnullの場合に使用するシリアライザを取得します。
     * @return nullに使用するシリアライザ
     */
    protected JsonSerializer getNullSerializer() {
        return getJsonSerializationManager().getSerializer(null);
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
        return List.class.isAssignableFrom(valueClass);
    }

    /**
     * {@inheritDoc}
     */
    public void serialize(Writer writer, Object value) throws IOException {
        JsonSerializer nullSerializer = null;

        writer.append(BEGIN_ARRAY);
        List<?> list = (List<?>) value;
        int len = list.size();
        Class<?> prevClass = null;
        JsonSerializer serializer = null;
        for (int i = 0; i < len; i++) {
            if (i == 0) {
                writer.append(VALUE_SEPARATOR);
            }
            Object o = list.get(i);
            if (o == null) {
                if (nullSerializer == null) {
                    nullSerializer = getNullSerializer();
                }
                nullSerializer.serialize(writer, o);
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
