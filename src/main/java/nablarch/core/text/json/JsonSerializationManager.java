package nablarch.core.text.json;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jsonのシリアライザを管理するクラス。
 * @author Shuji Kitamura
 */
public class JsonSerializationManager {

    /** 使用可能なシリアライザのリスト */
    private List<JsonSerializer> serializers;

    /** null用のシリアライザ */
    private JsonSerializer nullSerializer;

    /** デフォルトのシリアライザ */
    private JsonSerializer defaultSerializer;

    /** ClassごとのJsonSerializerのキャッシュ */
    private final Map<Class<?>, JsonSerializer> jsonSerializerCache
            = new HashMap<Class<?>, JsonSerializer>();

    /**
     * 初期化する。
     */
    public void initialize() {
        initialize(new JsonSerializationSettings(new HashMap<String, String>()));
    }

    /**
     * 初期化する。
     * @param settings シリアライズに関する設定
     */
    public void initialize(JsonSerializationSettings settings) {
        nullSerializer = createNullSerializer();
        defaultSerializer = createDefaultSerializer();
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
     * オブジェクトに対応したシリアライザを取得する
     * @param value シリアライズする値
     * @return 引数に渡した値をシリアライズするためのシリアライザ
     */
    public JsonSerializer getSerializer(Object value) {
        if (serializers == null) throw new IllegalStateException("JsonSerializationManager is not initialized.");
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
