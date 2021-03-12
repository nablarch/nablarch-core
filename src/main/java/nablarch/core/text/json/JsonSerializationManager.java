package nablarch.core.text.json;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
            for (JsonSerializer serializer : serializers) {
                if (serializer.isTarget(cls)) return serializer;
            }
        }
        return defaultSerializer;
    }
}
