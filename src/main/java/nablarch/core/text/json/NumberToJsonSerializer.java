package nablarch.core.text.json;

import java.io.IOException;
import java.io.Writer;

/**
 * Booleanの値をシリアライズするクラス。<br>
 * 受入れ可能なオブジェクトの型は java.lang.Boolean。<br>
 * シリアライズによりJsonの真理値(true / false)として出力する。
 * @author Shuji Kitamura
 */
public class NumberToJsonSerializer extends StringToJsonSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(JsonSerializationSettings settings) {
        //NOOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTarget(Class<?> valueClass) {
        return Number.class.isAssignableFrom(valueClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Writer writer, Object value) throws IOException {
        if (value instanceof Float) serializeFloat(writer, (Float)value);
        else if (value instanceof Double) serializeDouble(writer, (Double)value);
        else writer.append(value.toString());
    }

    /**
     * Float値のシリアライズを行う。
     * @param writer シリアライズ結果を書き込むWriterオブジェクト
     * @param value シリアライズする値
     * @throws IOException Writerオブジェクトへの書き込みエラー
     */
    protected void serializeFloat(Writer writer, Float value)throws IOException {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            super.serialize(writer, value.toString());
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
            super.serialize(writer, value.toString());
        } else {
            writer.append(value.toString());
        }
    }
}
