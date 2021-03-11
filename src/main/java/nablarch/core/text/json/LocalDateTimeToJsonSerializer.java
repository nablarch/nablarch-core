package nablarch.core.text.json;

import nablarch.core.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Java8以降のjava.time.LocalDateTimeをシリアライズするクラス。<br>
 * 受入れ可能なオブジェクトの型は java.time.LocalDateTime。<br>
 * シリアライズによりJsonのstringとして出力する。
 * @author Shuji Kitamura
 */
public class LocalDateTimeToJsonSerializer extends StringToJsonSerializer {

    /** 設定から取得する日時フォーマットのプロパティ名 */
    private static final String DATE_PATTERN_PROPERTY = "datePattern";

    /** デフォルトの日時フォーマット */
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /** 日時のフォーマッタ */
    private Object formatter;

    /** 日時のフォーマットに使用するメソッド */
    private Method formatMethod;

    private String datePattern;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(JsonSerializationSettings settings) {
        formatter = getFormatter(settings);
        if (formatter != null) formatMethod = getFormatMethod(formatter.getClass());
        // (coverage) Java7以前の場合に formatter == null が成立する
    }

    /**
     * 日時フォーマッタのインスタンスを取得する。
     *
     * @param settings シリアライズ設定
     * @return 日時フォーマッタのインスタンス
     */
    protected Object getFormatter(JsonSerializationSettings settings) {
        String prop = settings.getProp(DATE_PATTERN_PROPERTY);
        datePattern = !StringUtil.isNullOrEmpty(prop) ? prop : DEFAULT_DATE_PATTERN;
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
                    "illegal date pattern. pattern = [" + datePattern
                            + "], property name = [" + DATE_PATTERN_PROPERTY + "]", e);
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
     * フォーマットメソッドを取得する。
     *
     * @param clazz フォーマッタのクラス
     * @return フォーマットメソッド
     */
    protected Method getFormatMethod(Class<?> clazz) {
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
    protected String getValueClassName() {
        return  "java.time.LocalDateTime";
    }

    /**
     * {@inheritDoc}<br>
     * <br>
     * java.time.format.DateTimeFormatter#format(TemporalAccessor)において、
     * 書式設定中にエラーが発生した場合は、DateTimeExceptionがスローされるが、
     * 本クラスでは代替として、IllegalArgumentExceptionをスローする。
     */
    @Override
    protected String convertString(Object value) {
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
