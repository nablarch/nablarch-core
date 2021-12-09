package nablarch.core.text.json;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link JsonSerializationManager}の基本実装クラス。
 * @author Shuji Kitamura
 */
public class BasicJsonSerializationManager implements JsonSerializationManager {

    /** 使用可能なシリアライザのリスト */
    private List<JsonSerializer> serializers;

    /** null用のシリアライザ */
    private JsonSerializer nullSerializer;

    /** デフォルトのシリアライザ */
    private JsonSerializer defaultSerializer;

    /** objectのmember name用のシリアライザ */
    private JsonSerializer memberNameSerializer;

    /** ClassごとのJsonSerializerのキャッシュ */
    private final Map<Class<?>, JsonSerializer> jsonSerializerCache
            = new HashMap<Class<?>, JsonSerializer>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        initialize(new JsonSerializationSettings(new HashMap<String, String>()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(JsonSerializationSettings settings) {
        nullSerializer = createNullSerializer();
        defaultSerializer = createDefaultSerializer();
        memberNameSerializer = createMemberNameSerializer();
        serializers = createSerializers(settings);

        for (JsonSerializer serializer : serializers) {
            serializer.initialize(settings);
        }
        nullSerializer.initialize(settings);
        defaultSerializer.initialize(settings);
    }

    /**
     * null用のシリアライザを生成する。
     * @return  null用のシリアライザ
     */
    protected JsonSerializer createNullSerializer() {
        return new NullToJsonSerializer();
    }

    /**
     * デフォルトのシリアライザを生成する。
     * @return デフォルトのシリアライザ
     */
    protected JsonSerializer createDefaultSerializer() {
        return new ObjectToJsonSerializer(this);
    }

    /**
     * objectのmember name用のシリアライザを生成する。
     * @return objectのmember name用のシリアライザ
     */
    protected JsonSerializer createMemberNameSerializer() {
        return new StringToJsonSerializer();
    }

    /**
     * 使用するシリアライザを生成する。
     * オブジェクトに対応したシリアライザかの評価は先頭から順に行われる。
     * デフォルトのシリアライザのみで使用する場合であっても、
     * @param settings シリアライズに関する設定
     * @return シリアライザのリスト
     */
    protected List<JsonSerializer> createSerializers(JsonSerializationSettings settings) {
        return Arrays.asList(
                new StringToJsonSerializer(),
                new DateToJsonSerializer(this),
                new MapToJsonSerializer(this),
                new ListToJsonSerializer(this),
                new ArrayToJsonSerializer(this),
                new NumberToJsonSerializer(this),
                new BooleanToJsonSerializer(),
                new CalendarToJsonSerializer(this),
                new LocalDateTimeToJsonSerializer(this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonSerializer getMemberNameSerializer() {
        return memberNameSerializer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonSerializer getStringSerializer() {
        return getSerializer("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonSerializer getSerializer(Object value) {
        if (serializers == null) {
            throw new IllegalStateException("JsonSerializationManager is not initialized.");
        }
        if (value == null) {
            return nullSerializer;
        } else {
            Class<?> cls = value.getClass();
            if (jsonSerializerCache.containsKey(cls)) {
                return jsonSerializerCache.get(cls);
            }
            for (JsonSerializer serializer : serializers) {
                if (serializer.isTarget(cls)) {
                    jsonSerializerCache.put(cls, serializer);
                    return serializer;
                }
            }
        }
        return defaultSerializer;
    }
}
