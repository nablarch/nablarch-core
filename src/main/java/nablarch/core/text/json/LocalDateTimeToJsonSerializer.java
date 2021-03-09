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
    private Object formatter = null;

    /** 日時のフォーマットに使用するメソッド */
    private Method formatMethod = null;

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
        String datePattern = !StringUtil.isNullOrEmpty(prop) ? prop : DEFAULT_DATE_PATTERN;
        Object formatter = null;
        try {
            Class<?> clazz = Class.forName("java.time.format.DateTimeFormatter");
            Method method = clazz.getDeclaredMethod("ofPattern", String.class);
            formatter = method.invoke(null, datePattern);
        } catch (ClassNotFoundException e) {
            // (coverage) Java7以前の場合に通貨する
            // NOOP この例外は想定の動作の為、何もしない
        } catch (NoSuchMethodException e) {
            // (coverage) 到達しえない例外
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // (coverage) 到達しえない例外
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // (coverage) 到達しえない例外
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("illegal date pattern. property name = " + DATE_PATTERN_PROPERTY, e);
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
        Method formatMethod = null;
        try {
            formatMethod = clazz.getMethod("format", Class.forName("java.time.temporal.TemporalAccessor"));
        } catch (ClassNotFoundException e) {
            // (coverage) 到達しえない例外
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // (coverage) 到達しえない例外
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // (coverage) 到達しえない例外
            e.printStackTrace();
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
     * {@inheritDoc}
     */
    @Override
    protected String convertString(Object value) {
        try {
            return (String)formatMethod.invoke(formatter, value);
        } catch (IllegalAccessException e) {
            // (coverage) 到達しえない例外
            e.printStackTrace();
            return "format error : " + value.toString();
        } catch (InvocationTargetException e) {
            // (coverage) 到達しえない例外
            e.printStackTrace();
            return "format error : " + value.toString();
        }
    }
}
