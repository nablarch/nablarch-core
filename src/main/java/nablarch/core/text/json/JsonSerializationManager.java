package nablarch.core.text.json;

/**
 * {@link JsonSerializer}を管理、提供するインターフェース。
 * @author Shuji Kitamura
 */
public interface JsonSerializationManager {

    /**
     * デフォルト設定でシリアライザを初期化する。
     */
    void initialize();

    /**
     * 設定を指定してシリアライザを初期化する。
     * @param settings シリアライズに関する設定
     */
    void initialize(JsonSerializationSettings settings);

    /**
     * オブジェクトに対応したシリアライザを取得する
     * @param value シリアライズする値
     * @return 引数に渡した値をシリアライズするためのシリアライザ
     */
    JsonSerializer getSerializer(Object value);

}