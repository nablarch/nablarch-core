package nablarch.core.text.json;

import nablarch.core.util.annotation.Published;

/**
 * {@link JsonSerializer}を管理、提供するインターフェース。
 * @author Shuji Kitamura
 */
@Published(tag = "architect")
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
     * このメソッドの返り値となるシリアライザの出力は常にJSONのstringでなければならない。
     * </p>
     * <p>
     * JSONの仕様上、objectのmember nameは、必ずstringでなければならない。
     * 一方で、{@link #getStringSerializer()}で取得されるシリアライザは
     * {@code getSerializer("")}のショートカットであり、
     * 文字列用のシリアライザを独自の実装に差し替えることができる。
     * もし差し替え後の実装が常にstringにシリアライズする実装になっていない場合、
     * {@link #getStringSerializer()}で取得したシリアライザでmember nameを
     * 処理すると、JSONの仕様に沿わない結果となりうる。
     * そのため、メンバー名用のシリアライザを取得するメソッドは明示的に
     * {@link #getStringSerializer()}と分けて定義している。
     * </p>
     * @return objectのmember nameを処理するためのシリアライザ
     */
    JsonSerializer getMemberNameSerializer();

    /**
     * 文字列をJSONのオブジェクトに処理するためのシリアライザを取得する。
     * <p>
     * {@code getSerializer("")}のショートカット。
     * 各種シリアライザにて、文字列からJSONのオブジェクトにシリアライズする際に使用する。
     * </p>
     * @return 文字列をシリアライズするためのシリアライザ
     */
    JsonSerializer getStringSerializer();

}