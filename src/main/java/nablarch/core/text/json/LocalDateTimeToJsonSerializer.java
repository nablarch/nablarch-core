package nablarch.core.text.json;

/**
 * Java8以降のjava.time.LocalDateTimeをシリアライズするクラス。<br>
 * 受入れ可能なオブジェクトの型は java.time.LocalDateTime。<br>
 * シリアライズによりJsonのstringとして出力する。
 * @author Shuji Kitamura
 */
public class LocalDateTimeToJsonSerializer extends JavaTimeToJsonSerializer {

    /**
     * コンストラクタ。
     * @param manager シリアライズ管理クラス
     */
    public LocalDateTimeToJsonSerializer(JsonSerializationManager manager) {
        super(manager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDatePattern(JsonSerializationSettings settings) {
        return settings.getDatePattern();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueClassName() {
        return  "java.time.LocalDateTime";
    }

}
