package nablarch.core.text.json;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Dateの値をシリアライズするクラス。<br>
 * 受入れ可能なオブジェクトの型は java.util.Date。<br>
 * java.sql.Date, java.sql.Time, java.sql.Timestamp などの
 * Dateのサブクラスは対象にならない。
 * シリアライズによりJsonのstringとして出力する。
 * @author Shuji Kitamura
 */
public class DateToJsonSerializer implements JsonSerializer {

    /** 日時のフォーマッタ */
    private DateFormat dateFormat;

    /** シリアライズ管理クラス */
    private final JsonSerializationManager manager;

    /** stringシリアライザ */
    private JsonSerializer stringSerializer;

    /**
     * コンストラクタ。
     * @param manager シリアライズ管理クラス
     */
    public DateToJsonSerializer(JsonSerializationManager manager) {
        this.manager = manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(JsonSerializationSettings settings) {
        dateFormat = new SimpleDateFormat(settings.getDatePattern());
        stringSerializer = manager.getStringSerializer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTarget(Class<?> valueClass) {
        return Date.class.equals(valueClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Writer writer, Object value) throws IOException {
        stringSerializer.serialize(writer, dateFormat.format((Date)value));
    }
}
