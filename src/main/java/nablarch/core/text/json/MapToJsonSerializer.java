package nablarch.core.text.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Mapオブジェクトをシリアライズするクラス。<br>
 * 受入れ可能なオブジェクトの型は java.util.Map。<br>
 * シリアライズによりJsonのobjectとして出力する。
 * @author Shuji Kitamura
 */
public class MapToJsonSerializer implements JsonSerializer {

    /** objectの開始文字 */
    public static final char BEGIN_OBJECT = '{';

    /** objectの終了文字 */
    public static final char END_OBJECT = '}';

    /** nameのセパレータとなる文字 */
    public static final char NAME_SEPARATOR = ':';

    /** 値のセパレータとなる文字 */
    public static final char VALUE_SEPARATOR = ',';

    /** シリアライズ管理クラス */
    private final JsonSerializationManager manager;

    /** nameに使用するシリアライザ */
    private JsonSerializer nameSerializer = null;

    /** コンストラクタ。 */
    public MapToJsonSerializer(JsonSerializationManager manager) {
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
     * nameに使用するシリアライザを取得します。
     * @return nameに使用するシリアライザ
     */
    protected JsonSerializer getNameSerializer() {
        if (nameSerializer == null) {
            nameSerializer = getJsonSerializationManager().getSerializer("");
        }
        return nameSerializer;
    }

    /**
     * 値がnullの場合に使用するシリアライザを取得します。<br>
     * このメソッドがnullを返す時、値がnullの項目をスキップします。
     * @return nullに使用するシリアライザ
     */
    protected JsonSerializer getNullSerializer() {
        return null;
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
        return Map.class.isAssignableFrom(valueClass);
    }

    /**
     * {@inheritDoc}
     */
    public void serialize(Writer writer, Object value) throws IOException {
        JsonSerializer nameSerializer = getNameSerializer();
        JsonSerializer nullSerializer = getNullSerializer();
        Map<?, ?> map = (Map<?, ?>) value;
        boolean isFirst = true;
        writer.append(BEGIN_OBJECT);
        for (Object name: map.keySet()) {
            if (name != null && nameSerializer.isTarget(name.getClass())) {
                Object o = map.get(name);
                if (o == null) {
                    if (nullSerializer != null) {
                        if (!isFirst) writer.append(VALUE_SEPARATOR);
                        else isFirst = false;
                        nameSerializer.serialize(writer, name);
                        writer.append(NAME_SEPARATOR);
                        nullSerializer.serialize(writer, o);
                    }
                } else if (o instanceof InplaceMapEntries) {
                    String inplaceString = o.toString();
                    if (!isJsonWs(inplaceString)) {
                        if (!isFirst) writer.append(VALUE_SEPARATOR);
                        else isFirst = false;
                    }
                    writer.append(inplaceString);
                } else {
                    if (!isFirst) writer.append(VALUE_SEPARATOR);
                    else isFirst = false;
                    nameSerializer.serialize(writer, name);
                    writer.append(NAME_SEPARATOR);
                    manager.getSerializer(o).serialize(writer, o);
                }
            }
        }
        writer.append(END_OBJECT);
    }

    /**
     * 文字列がJsonのwsのみで構成されるか判定します。
     * @param s 判定する文字列
     * @return 半角スペース, 水平タブ, 改行(Line feed), 復帰改行(Carriage return)のみで構成されるときtrue
     */
    private boolean isJsonWs(String s) {
        int len = s.length();
        char c;
        for (int i = 0; i < len; i++) {
            c = s.charAt(i);
            if (c != 0x20 && c != 0x09 && c != 0x0A && c!= 0x0d) {
                return false;
            }
        }
        return true;
    }

}
