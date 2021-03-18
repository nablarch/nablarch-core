package nablarch.core.text.json;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Date and Time APIの日時を扱うクラスをシリアライズするための抽象クラス。
 * <p>
 * java.time.TemporalAccessorの実装クラスを対象としたシリアライザの共通機能を提供する。<br>
 * シリアライズによりJsonのstringとして出力する。
 * </p>
 * @author Shuji Kitamura
 */
public abstract class JavaTimeToJsonSerializer implements JsonSerializer {

    /** 日時のフォーマット */
    private String datePattern;

    /** 日時のフォーマッタ */
    private Object formatter;

    /** 日時のフォーマットに使用するメソッド */
    private Method formatMethod;

    /** シリアライズ管理クラス */
    private final JsonSerializationManager manager;

    /** stringシリアライザ */
    private JsonSerializer stringSerializer;

    /**
     * コンストラクタ。
     * @param manager シリアライズ管理クラス
     */
    public JavaTimeToJsonSerializer(JsonSerializationManager manager) {
        this.manager = manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(JsonSerializationSettings settings) {
        formatter = getFormatter(settings);
        if (formatter != null) {
            formatMethod = getFormatMethod(formatter.getClass());
        }
        // (coverage) Java7以前の場合に formatter == null が成立する

        stringSerializer = manager.getStringSerializer();
    }

    /**
     * 日時フォーマッタのインスタンスを取得する。
     *
     * @param settings シリアライズ設定
     * @return 日時フォーマッタのインスタンス
     */
    private Object getFormatter(JsonSerializationSettings settings) {
        datePattern = getDatePattern(settings);
        Object formatter = null;
        try {
            Class<?> clazz = Class.forName("java.time.format.DateTimeFormatter");
            Method method = clazz.getDeclaredMethod("ofPattern", String.class);
            formatter = method.invoke(null, datePattern);
        } catch (ClassNotFoundException e) {
            // (coverage) Java7以前の場合に通過する
            // NOOP この例外は想定の動作の為、何もしない
        } catch (NoSuchMethodException e) {
            // (coverage) 到達しえない例外
            // ofPattern() は Java 8 以上であれば必ず存在する。
            // Java 7 以前であれば上の ClassNotFoundException を先に通過するので、
            // この catch 句に到達するケースは存在しない。
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "illegal date pattern. pattern = [" + datePattern + "]", e);
        } catch (IllegalAccessException e) {
            // (coverage) 到達しえない例外
            // NoSuchMethodException と同様で、 ofPattern() は
            // Java 8 以上であれば必ずアクセス可能であり、
            // Java 7 以前であれば先に ClassNotFoundException が発生するため、
            // この catch 句に到達するケースは存在しない
            throw new RuntimeException(e);
        }

        return formatter;
    }

    /**
     * 日時フォーマットを取得する。
     * @return 日時フォーマット
     */
    protected abstract String getDatePattern(JsonSerializationSettings settings);

    /**
     * フォーマットメソッドを取得する。
     *
     * @param clazz フォーマッタのクラス
     * @return フォーマットメソッド
     */
    private Method getFormatMethod(Class<?> clazz) {
        Method formatMethod;
        try {
            formatMethod = clazz.getMethod("format", Class.forName("java.time.temporal.TemporalAccessor"));
        } catch (ClassNotFoundException e) {
            // (coverage) 到達しえない例外
            // java.time.temporal.TemporalAccessor は
            // Java 8 以上であればあれば必ず存在する。
            // Java 7 以前であれば初期化処理において 本メソッドの実行がスキップされ、
            // この catch 句に到達するケースは存在しない
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            // (coverage) 到達しえない例外
            // 本メソッドに引数として渡されるクラスは、java.time.format.DateTimeFormatterであり。
            // format(TemporalAccessor) は Java 8 以上であれば必ず存在する。
            // Java 7 以前であれば初期化処理において 本メソッドの実行がスキップされ、
            // この catch 句に到達するケースは存在しない
            throw new RuntimeException(e);
        }

        return formatMethod;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTarget(Class<?> valueClass) {
        return formatMethod != null && valueClass.getName().equals(getValueClassName());
        // (coverage) Java7以前の場合に formatMethod != null が成立する
    }

    /**
     * このクラスで処理する値のクラス名を取得する。
     * @return クラス名
     */
    protected abstract String getValueClassName();

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Writer writer, Object value) throws IOException {
        stringSerializer.serialize(writer, format(value));
    }

    /**
     * TemporalAccessorのオブジェクトをフォーマットする。<br>
     * <br>
     * java.time.format.DateTimeFormatter#format(TemporalAccessor)において、
     * 書式設定中にエラーが発生した場合は、DateTimeExceptionがスローされるが、
     * 本クラスでは代替として、IllegalArgumentExceptionをスローする。<br>
     * @param value フォーマットするオブジェクト(java.time.TemporalAccessor)
     * @return フォーマットした文字列
     */
    private String format(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        try {
            return (String)formatMethod.invoke(formatter, value);
        } catch (IllegalAccessException e) {
            // (coverage) 到達しえない例外
            // format(TemporalAccessor) メソッドは
            // Java 8 以上であれば必ずアクセス可能であり、
            // Java 7 以前であれば初期化処理において null となる為、
            // この catch 句に到達するケースは存在しない
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "mismatched date pattern. pattern = [" + datePattern + "]", e);
        }
    }

}
