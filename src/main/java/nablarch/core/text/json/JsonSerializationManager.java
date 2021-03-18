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
     * オブジェクトに対応したシリアライザを取得する。
     * @param value シリアライズする値
     * @return 引数に渡した値をシリアライズするためのシリアライザ
     */
    JsonSerializer getSerializer(Object value);

    /**
     * objectのmember nameを処理するためのシリアライザを取得する。
     * <p>
     * このシリアライザのシリアライズ結果は常にJSONのstringでなければならない。
     * {@link #getStringSerializer()}で取得されるシリアライザは
     * シリアライズ後のJSONの型を保証していない為、別途シリアライザを準備することを推奨とする。
     * </p>
     * @return objectのmember nameを処理するためのシリアライザ
     */
    JsonSerializer getMemberNameSerializer();

    /**
     * 文字列をJSONのオブジェクトに処理するためのシリアライザを取得する。
     * <p>
     * {code getSerializer("")}のラッパーメソッド。
     * 各種シリアライザにて、文字列からJSONのオブジェクトにシリアライズする際に使用する。
     * </p>
     * @return 引数に渡した値をシリアライズするためのシリアライザ
     */
    JsonSerializer getStringSerializer();

}