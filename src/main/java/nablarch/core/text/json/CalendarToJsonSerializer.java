package nablarch.core.text.json;

import java.util.Calendar;
import java.util.Date;

/**
 * Calendarの値をシリアライズするクラス。<br>
 * 受入れ可能なオブジェクトの型は java.util.Calendar。<br>
 * シリアライズによりJsonのstringとして出力する。
 * @author Shuji Kitamura
 */
public class CalendarToJsonSerializer extends DateToJsonSerializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTarget(Class<?> valueClass) {
        return Calendar.class.isAssignableFrom(valueClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Date convertDate(Object value) {
        return ((Calendar)value).getTime();
    }
}
