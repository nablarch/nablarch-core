package nablarch.core.text.json;

/**
 * Jsonのobjectへの埋め込み用クラス。<br>
 * {@link MapToJsonSerializer}で組み立て済みのJSON構文となる文字列を埋め込むために使用する。<br>
 * 組み立て済みのJSON構文は、objectのmember要素とし、前後にオブジェクトの開始、終了マーカーや値のセパレーターは含まないこと。
 * @author Shuji Kitamura
 */
public class InplaceMapEntries {

    /** 組み立て済みのJSON構文 */
    private final String rawJsonText;

    /**
     * コンストラクタ
     * @param rawJsonText 組み立て済みのJSON構文
     */
    public InplaceMapEntries(String rawJsonText) {
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
     * @param s 判定する文字列
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
