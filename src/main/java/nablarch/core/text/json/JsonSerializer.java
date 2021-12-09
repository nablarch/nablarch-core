package nablarch.core.text.json;

import java.io.IOException;
import java.io.Writer;

/**
 * オブジェクトをJsonにシリアライズするインターフェース。<br>
 * 受入れ可能なオブジェクトを特定したクラスを実装する。<br>
 * @author Shuji Kitamura
 */
public interface JsonSerializer {

    /**
     * 初期処理を行う。
     * @param settings シリアライザの設定
     */
    void initialize(JsonSerializationSettings settings);

    /**
     * このシリアライザが受入れ可能なクラスか否かを判定します。
     * @param valueClass 判定対象のクラス
     * @return シリアライズ可能な場合はtrue、不可の場合はfalse
     */
    boolean isTarget(Class<?> valueClass);

    /**
     * シリアライズを行う。
     * @param writer シリアライズ結果を書き込むWriterオブジェクト
     * @param value シリアライズする値
     * @throws IOException Writerオブジェクトへの書き込みエラー
     */
    void serialize(Writer writer, Object value) throws IOException;
}
