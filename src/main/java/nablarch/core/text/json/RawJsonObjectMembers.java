package nablarch.core.text.json;

/**
 * Jsonのobjectへの埋め込み用クラス。
 * <p>
 * {@link MapToJsonSerializer}で組み立て済みのJSON構文となる文字列を埋め込むために使用する。<br>
 * 組み立て済みのJSON構文は、objectのmember要素とし、前後にオブジェクトの開始、終了マーカーや値のセパレーターは含まないこと。<br>
 * 例：{@code "\"key1\":\"value1\",\"key2\":\"value2\""}
 * </p>
 * @author Shuji Kitamura
 */
public class RawJsonObjectMembers {

    /** 組み立て済みのJSON構文 */
    private final String rawJsonText;

    /**
     * コンストラクタ
     * @param rawJsonText 組み立て済みのJSON構文
     */
    public RawJsonObjectMembers(String rawJsonText) {
        this.rawJsonText = rawJsonText;
    }

    /**
     * 組み立て済みのJSON構文を取得する。
     * @return 組み立て済みのJSON構文
     */
    public String getRawJsonText() {
        return rawJsonText;
    }

    /**
     * 文字列がJsonのwhitespaceのみで構成されるか判定します。
     * @return 半角スペース, 水平タブ, 改行(Line feed), 復帰改行(Carriage return)のみで構成されるときtrue
     */
    public boolean isJsonWhitespace() {
        int len = rawJsonText.length();
        char c;
        for (int i = 0; i < len; i++) {
            c = rawJsonText.charAt(i);
            if (c != 0x20 && c != 0x09 && c != 0x0A && c!= 0x0d) {
                return false;
            }
        }
        return true;
    }
}
