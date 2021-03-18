package nablarch.core.text.json;

import nablarch.core.util.StringUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Mapオブジェクトをシリアライズするクラス。
 * <p>
 * 受入れ可能なオブジェクトの型は java.util.Map。<br>
 * シリアライズによりJsonのobjectとして出力する。<br>
 * 値がnullとなるmemberはデフォルト設定で出力しない。
 * 出力対象とする場合は、{@link JsonSerializationSettings}で
 * ignoreNullValueMemberプロパティにfalseを設定する。<br>
 * </p>
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

    /** 値がNULLのmemberを無視するか否かのプロパティ名 */
    private static final String IGNORE_NULL_VALUE_MEMBER_PROPERTY = "ignoreNullValueMember";

    /** デフォルトの値がNULLのmemberを無視するか否か */
    private static final boolean DEFAULT_IGNORE_NULL_VALUE_MEMBER = true;

    /** シリアライズ管理クラス */
    private final JsonSerializationManager manager;

    /** nameに使用するシリアライザ */
    private JsonSerializer memberNameSerializer;

    /** 値がNULLのmemberを無視するか否か */
    private boolean isIgnoreNullValueMember;

    /**
     * コンストラクタ。
     * @param manager シリアライズ管理クラス
     */
    public MapToJsonSerializer(JsonSerializationManager manager) {
        this.manager = manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(JsonSerializationSettings settings) {
        memberNameSerializer = manager.getMemberNameSerializer();
        isIgnoreNullValueMember = isIgnoreNullValueMember(settings);
    }

    /**
     * 値がNULLのmemberを無視するか否かを取得する。<br>
     * 取得元のプロパティ名は"ignoreNullValueMember"。
     * プロパティの値が設定されていない、もしくはnull、空の文字列の場合、デフォルト値としてtrueを返す。
     * @param settings シリアライザの設定
     * @return 値がNULLのmemberを無視するときtrue、出力対象ととするときfalse
     */
    private boolean isIgnoreNullValueMember(JsonSerializationSettings settings) {
        String ignore = settings.getProp(IGNORE_NULL_VALUE_MEMBER_PROPERTY);
        return !StringUtil.isNullOrEmpty(ignore) ?  Boolean.parseBoolean(ignore) : DEFAULT_IGNORE_NULL_VALUE_MEMBER;
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
        Map<?, ?> map = (Map<?, ?>) value;
        boolean isFirst = true;
        writer.append(BEGIN_OBJECT);
        for (Object memberName: map.keySet()) {
            if (memberName != null && memberNameSerializer.isTarget(memberName.getClass())) {
                Object memberValue = map.get(memberName);
                if (memberValue != null || !isIgnoreNullValueMember) {
                    if (memberValue instanceof RawJsonObjectMembers) {
                        RawJsonObjectMembers rawMembers = (RawJsonObjectMembers) memberValue;
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
        }
        writer.append(END_OBJECT);
    }

}
