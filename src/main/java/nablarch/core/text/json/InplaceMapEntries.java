package nablarch.core.text.json;

/**
 * Mapへの埋め込み用クラス。<br>
 * {@link MapToJsonSerializer}で組み立て済みのJSON構文となる文字列を埋め込むために使用する。<br>
 * 組み立て済みのJSON構文には、前後にオブジェクトの開始、終了マーカーや値のセパレーターは含まないこと。
 * @author Shuji Kitamura
 */
public class InplaceMapEntries {

    /** 組み立て済みのJSON構文 */
    private final String structuredString;

    /**
     * コンストラクタ
     * @param structuredString 組み立て済みのJSON構文
     */
    public InplaceMapEntries(String structuredString) {
        this.structuredString = structuredString;
    }

    /**
     * 組み立て済みのJSON構文を取得する。
     * @return 組み立て済みのJSON構文
     */
    public String toString() {
        return structuredString;
    }
}
