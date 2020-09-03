package nablarch.core.repository.disposal;

import nablarch.core.util.annotation.Published;

/**
 * 廃棄処理を行うインタフェース。<br>
 * 廃棄処理を必要とするクラスは本インタフェースを実装すること。
 *
 * @author Tanaka Tomoyuki
 */
@Published(tag = "architect")
public interface Disposable {

    /**
     * 廃棄処理を行う。
     * @throws Exception 廃棄処理中に例外が発生した場合
     */
    void dispose() throws Exception;
}
