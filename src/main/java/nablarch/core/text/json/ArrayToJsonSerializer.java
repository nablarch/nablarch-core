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
        return valueClass.isArray();
    }

    /**
     * {@inheritDoc}
     */
    public void serialize(Writer writer, Object value) throws IOException {
        JsonSerializer nullSerializer = null;

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
