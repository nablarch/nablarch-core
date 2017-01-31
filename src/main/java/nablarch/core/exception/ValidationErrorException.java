package nablarch.core.exception;

/**
 * バリデーションエラーを通知するための例外クラス。
 *
 * @author siosio
 */
public class ValidationErrorException extends RuntimeException {

    /**
     * メッセージを持たない例外クラスを構築する。
     */
    public ValidationErrorException() {
    }

    /**
     * メッセージを元に例外クラスを構築する。
     *
     * @param message メッセージ
     */
    public ValidationErrorException(final String message) {
        super(message);
    }

    /**
     * メッセージと原因を元に例外クラスを構築する。
     *
     * @param message メッセージ
     * @param cause 原因
     */
    public ValidationErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
