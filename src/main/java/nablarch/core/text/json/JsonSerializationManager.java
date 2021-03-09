package nablarch.core.text.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Jsonのシリアライザを管理するクラス。
 */
public class JsonSerializationManager {

    /** 使用可能なシリアライザのリスト */
    private List<JsonSerializer> serializers = null;

    /** null用のシリアライザ */
    private JsonSerializer nullSerializer = null;

    /** デフォルトのシリアライザ */
    private JsonSerializer defaultSerializer = null;

    /**
     * null用のシリアライザを設定する。
     * @param nullSerializer null用のシリアライザ
     */
    protected void setNullSerializer(JsonSerializer nullSerializer) {
        this.nullSerializer = nullSerializer;
    }

    /**
     * デフォルトのシリアライザを設定する。
     * @param nullSerializer デフォルトのシリアライザ
     */
    protected void setDefaultSerializer(JsonSerializer nullSerializer) {
        this.defaultSerializer = nullSerializer;
    }

    /**
     * シリアライザを追加する。
     * @param serializer シリアライザ
     */
    public void addSerializer(JsonSerializer serializer) {
        serializers.add(serializer);
    }

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
        setNullSerializer(new NullToJsonSerializer());
        setDefaultSerializer(new ObjectToJsonSerializer());
        serializers = new ArrayList<JsonSerializer>();
        enlistSerializer(settings);

        for (JsonSerializer serializer : serializers) {
            serializer.initialize(settings);
        }
        nullSerializer.initialize(settings);
        defaultSerializer.initialize(settings);
    }

    /**
     * 使用するシリアライザを登録する
     * 登録には、{@link JsonSerializationManager#addSerializer(JsonSerializer)}メソッドを用いる。
     * オブジェクトに対応したシリアライザかの評価は追加順に行われる。
     * @param settings シリアライズに関する設定
     */
    protected void enlistSerializer(JsonSerializationSettings settings) {
        addSerializer(new StringToJsonSerializer());
        addSerializer(new DateToJsonSerializer());
        addSerializer(new MapToJsonSerializer(this));
        addSerializer(new ListToJsonSerializer(this));
        addSerializer(new ArrayToJsonSerializer(this));
        addSerializer(new NumberToJsonSerializer());
        addSerializer(new BooleanToJsonSerializer());
        addSerializer(new LocalDateTimeToJsonSerializer());
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
        return onNoSerializer(value);
    }

    /**
     * 該当するシリアライザが見つからない場合の処理
     * @param value シリアライズする値
     * @return 全てのオブジェクトをシリアライズ可能なシリアライザ
     */
    protected JsonSerializer onNoSerializer(Object value) {
        return defaultSerializer;
    }
}
