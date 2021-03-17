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
    private static final char BEGIN_OBJECT = '{';

    /** objectの終了文字 */
    private static final char END_OBJECT = '}';

    /** nameのセパレータとなる文字 */
    private static final char NAME_SEPARATOR = ':';

    /** 値のセパレータとなる文字 */
    private static final char VALUE_SEPARATOR = ',';

    /** シリアライズ管理クラス */
    private final JsonSerializationManager manager;

    /** nameに使用するシリアライザ */
    private JsonSerializer memberNameSerializer;

    /**
     * コンストラクタ。
     * @param manager シリアライズ管理クラス
     */
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
    @Override
    public void initialize(JsonSerializationSettings settings) {
        this.memberNameSerializer = manager.getSerializer("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTarget(Class<?> valueClass) {
        return Map.class.isAssignableFrom(valueClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Writer writer, Object value) throws IOException {
        JsonSerializer nullSerializer = getNullSerializer();
        Map<?, ?> map = (Map<?, ?>) value;
        boolean isFirst = true;
        writer.append(BEGIN_OBJECT);
        for (Object memberName: map.keySet()) {
            if (memberName != null && memberNameSerializer.isTarget(memberName.getClass())) {
                Object memberValue = map.get(memberName);
                if (memberValue == null) {
                    if (nullSerializer != null) {
                        if (!isFirst) {
                            writer.append(VALUE_SEPARATOR);
                        } else {
                            isFirst = false;
                        }
                        memberNameSerializer.serialize(writer, memberName);
                        writer.append(NAME_SEPARATOR);
                        nullSerializer.serialize(writer, memberValue);
                    }
                } else if (memberValue instanceof RawJsonObjectMembers) {
                    RawJsonObjectMembers rawMembers = (RawJsonObjectMembers)memberValue;
                    if (!rawMembers.isJsonWhitespace()) {
                        if (!isFirst) {
                            writer.append(VALUE_SEPARATOR);
                        } else {
                            isFirst = false;
                        }
                    }
                    writer.append(rawMembers.getRawJsonText());
                } else {
                    if (!isFirst) {
                        writer.append(VALUE_SEPARATOR);
                    } else {
                        isFirst = false;
                    }
                    memberNameSerializer.serialize(writer, memberName);
                    writer.append(NAME_SEPARATOR);
                    manager.getSerializer(memberValue).serialize(writer, memberValue);
                }
            }
        }
        writer.append(END_OBJECT);
    }

}
