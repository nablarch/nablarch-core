package nablarch.core.text.json;

import nablarch.core.util.StringUtil;

import java.io.IOException;
import java.io.Writer;

/**
 * 文字列をシリアライズするクラス。<br>
 * 受入れ可能なオブジェクトの型は java.lang.String。
 * シリアライズによりJsonのstringとして出力する。
 * @author Shuji Kitamura
 */
public class StringToJsonSerializer implements JsonSerializer {

    private static final char QUOTATION_MARK = '\"';

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
        return String.class.isAssignableFrom(valueClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(Writer writer, Object value) throws IOException {
        writeEscapedString(writer, convertString(value));
    }

    /**
     * オブジェクトからシリアライズ対象の文字列に変換する。
     * java.lang.String以外のオブジェクトはtoString()メソッドの戻り値をシリアライズ対象の文字列とする。<br>
     * 書式化するなど、toString()メソッドの戻り値以外とする場合は、このメソッドをオーバーライドし、必要な変換を行う。
     */
    protected String convertString(Object value) {
        return (String)value;
    }

    /**
     * Escape処理を行ったJsonのstringを書き出す。
     * @param writer 書き込み先のWriterオブジェクト
     * @param s 書き込み対象の文字列
     * @throws IOException I/Oエラー
     */
    protected void writeEscapedString(Writer writer, String s) throws IOException {
        writer.append(QUOTATION_MARK);
        writeEscapedStringWithoutQuotation(writer, s);
        writer.append(QUOTATION_MARK);
    }

    /**
     * 前後のquotation-markを含まないEscape処理を行ったJsonのstringを書き出す。
     * @param writer 書き込み先のWriterオブジェクト
     * @param s 書き込み対象の文字列
     * @throws IOException I/Oエラー
     */
    protected void writeEscapedStringWithoutQuotation(Writer writer, String s) throws IOException {
        if (StringUtil.isNullOrEmpty(s)) {
            return;
        }

        int pos;
        char c;
        int len = s.length();
        boolean needEscape = false;
        String hexValue;

        for (pos = 0; pos < len; pos++) {
            c = s.charAt(pos);
            if (c < 0x20 || c == 0x22 || c == 0x5c) {
                needEscape = true;
                break;
            }
        }
        if (needEscape) {
            int start = 0;
            for (; pos < len; pos++) {
                c = s.charAt(pos);
                if (c < 0x20 || c == '\\' || c == '"') {
                    if (start != pos) {
                        writer.append(s, start, pos);
                    }
                    switch (c) {
                        case '\\':
                        case '"':
                            writer.append('\\').append(c);
                            break;
                        case '\b':
                            writer.append("\\b");
                            break;
                        case '\f':
                            writer.append("\\f");
                            break;
                        case '\n':
                            writer.append("\\n");
                            break;
                        case '\r':
                            writer.append("\\r");
                            break;
                        case '\t':
                            writer.append("\\t");
                            break;
                        default:
                            hexValue = "000" + Integer.toHexString(c);
                            writer.append("\\u").append(hexValue,hexValue.length() - 4, hexValue.length());
                    }
                    start = pos + 1;
                }
            }
            if (start != pos) {
                writer.append(s, start, pos);
            }
        } else {
            writer.append(s);
        }
    }
}
