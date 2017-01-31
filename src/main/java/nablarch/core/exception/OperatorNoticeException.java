package nablarch.core.exception;

import nablarch.core.util.annotation.Published;

/**
 * 運用(オペレータ)にエラー内容と復旧方法を通知することを目的とした例外クラス。
 *
 * @author siosio
 */
@Published
public class OperatorNoticeException extends RuntimeException {

    /**
     * メッセージを元に例外クラスを構築する。
     *
     * @param message メッセージ
     */
    public OperatorNoticeException(final String message) {
        super(message);
    }

    /**
     * メッセージと原因を元に例外クラスを構築する。
     *
     * @param message メッセージ
     * @param cause 原因
     */
    public OperatorNoticeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
