package nablarch.core.text.json;

import java.io.IOException;
import java.io.Writer;

/**
 * Numberの値をシリアライズするクラス。
 * <p>
 * 受入れ可能なオブジェクトの型は {@link Number} 及び、そのサブクラスとして下記などが対象となる。
 * </p>
 * <ul>
 *   <li>{@link Integer}</li>
 *   <li>{@link Long}</li>
 *   <li>{@link Short}</li>
 *   <li>{@link Byte}</li>
 *   <li>{@link Float}</li>
 *   <li>{@link Double}</li>
 *   <li>{@link java.util.concurrent.atomic.AtomicInteger AtomicInteger}</li>
 *   <li>{@link java.util.concurrent.atomic.AtomicLong AtomicLong}</li>
 *   <li>{@link java.math.BigInteger BigInteger}</li>
 *   <li>{@link java.math.BigDecimal BigDecimal}</li>
 * </ul>
 * <p>
 * (オートボクシングにより int, long, short, byte, float, double も対象となる。)<br>
 * シリアライズによりJsonのnumberとして出力するが、
 * NaNおよび無限量については、stringとして出力する。
 * </p>
 * @author Shuji Kitamura
 */
public class NumberToJsonSerializer implements JsonSerializer {

    /** シリアライズ管理クラス */
    protected final JsonSerializationManager manager;

    /** stringシリアライザ */
    protected JsonSerializer stringSerializer;

    /**
     * コンストラクタ。
     * @param manager シリアライズ管理クラス
     */
    public NumberToJsonSerializer(JsonSerializationManager manager) {
        this.manager = manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(JsonSerializationSettings settings) {
        stringSerializer = manager.getStringSerializer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTarget(Class<?> valueClass) {
        return Number.class.isAssignableFrom(valueClass)
                || int.class.equals(valueClass)
                || short.class.equals(valueClass)
                || long.class.equals(valueClass)
                || byte.class.equals(valueClass)
                || float.class.equals(valueClass)
                || double.class.equals(valueClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Writer writer, Object value) throws IOException {
        if (value instanceof Float) {
            serializeFloat(writer, (Float) value);
        } else if (value instanceof Double) {
            serializeDouble(writer, (Double) value);
        } else {
            writer.append(value.toString());
        }
    }

    /**
     * Float値のシリアライズを行う。
     * @param writer シリアライズ結果を書き込むWriterオブジェクト
     * @param value シリアライズする値
     * @throws IOException Writerオブジェクトへの書き込みエラー
     */
    protected void serializeFloat(Writer writer, Float value)throws IOException {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            stringSerializer.serialize(writer, value.toString());
        } else {
            writer.append(value.toString());
        }
    }

    /**
     * Double値のシリアライズを行う。
     * @param writer シリアライズ結果を書き込むWriterオブジェクト
     * @param value シリアライズする値
     * @throws IOException Writerオブジェクトへの書き込みエラー
     */
    protected void serializeDouble(Writer writer, Double value)throws IOException {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            stringSerializer.serialize(writer, value.toString());
        } else {
            writer.append(value.toString());
        }
    }
}
