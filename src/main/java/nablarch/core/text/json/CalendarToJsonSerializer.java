package nablarch.core.text.json;

import java.util.Calendar;
import java.util.Date;

/**
 * Booleanの値をシリアライズするクラス。<br>
 * 受入れ可能なオブジェクトの型は java.util.Calendar。<br>
 * シリアライズによりJsonのstringとして出力する。
 * @author Shuji Kitamura
 */
public class CalendarToJsonSerializer extends DateToJsonSerializer {

    /**
     * {@inheritDoc}
     */
    public boolean isTarget(Class<?> valueClass) {
        return Calendar.class.isAssignableFrom(valueClass);
    }

    /**
     * {@inheritDoc}
     */
    protected Date convertDate(Object value) {
        return ((Calendar)value).getTime();
    }
}
