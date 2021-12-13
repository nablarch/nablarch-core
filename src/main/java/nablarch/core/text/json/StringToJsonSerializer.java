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

    protected static final char QUOTATION_MARK = '\"';

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

        // 1文字ずつ append すると性能が大きく劣化するため、
        // エスケープ不要な文字は可能な限りまとめて append するようにしている
        int index = 0;
        int startIndexOfNoEscape = 0;
        for (; index < s.length(); index++) {
            char c = s.charAt(index);
            if (needsEscape(c)) {
                if (startIndexOfNoEscape != index) {
                    writer.append(s, startIndexOfNoEscape, index);
                }
                writer.append(escape(c));
                startIndexOfNoEscape = index + 1;
            }
        }

        if (startIndexOfNoEscape != index) {
            writer.append(s, startIndexOfNoEscape, index);
        }
    }

    /**
     * 指定された文字が、エスケープが必要な文字かどうか判定する。
     * <p>
     * 以下のいずれかの文字が、エスケープが必要な文字に該当する。
     * <ul>
     *   <li>制御文字 (ASCIIコード上、半角スペースより前の文字)</li>
     *   <li>バックスラッシュ ('\')</li>
     *   <li>ダブルクォーテーション ('"')</li>
     * </ul>
     * </p>
     * @param c 判定対象の文字
     * @return エスケープが必要な場合は true
     */
    private boolean needsEscape(char c) {
        return c < ' ' || c == '\\' || c == '"';
    }

    /**
     * 指定された文字をエスケープする。
     * @param c エスケープ対象の文字
     * @return エスケープ後の文字列
     */
    private String escape(char c) {
        switch (c) {
            case '\\':
            case '"':
                return "\\" + c;
            case '\b':
                return "\\b";
            case '\f':
                return "\\f";
            case '\n':
                return "\\n";
            case '\r':
                return "\\r";
            case '\t':
                return "\\t";
            default:
                return String.format("\\u%04x", (int)c);
        }
    }
}
