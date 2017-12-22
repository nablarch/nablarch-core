package nablarch.core.util;

/**
 * 文字列を走査するクラス。
 *
 * @author T.Kawasaki
 */
abstract class StringIterator {

    /** 走査対象の文字列 */
    final String string;        // SUPPRESS CHECKSTYLE サブクラスに対してカプセル化の必要がないため

    /** 走査用のインデックス */
    int index = 0;              // SUPPRESS CHECKSTYLE サブクラスに対してカプセル化の必要がないため

    /**
     * コンストラクタ
     *
     * @param orig 走査対処文字列
     */
    StringIterator(String orig) {
        if (orig == null) {
            throw new IllegalArgumentException("argument must not be null.");
        }
        this.string = orig;
    }

    /**
     * 次の要素が存在するか判定する。
     *
     * @return 次の要素が存在する場合、真
     */
    abstract boolean hasNext();

    /**
     * 次の文字を取得する。
     *
     * @return 次の文字
     */
    abstract char next();

    /**
     * 正順のイテレータを生成する。
     *
     * @param string 走査対象となる文字列
     * @return 正順のイテレータ
     */
    static StringIterator iterator(String string) {
        return new ForwardIterator(string);
    }

    /**
     * 逆順のイテレータを生成する。
     *
     * @param string 操作対象となる文字列
     * @return 逆順のイテレータ
     */

    static StringIterator reverseIterator(String string) {
        return new ReverseIterator(string);
    }

    /** 正順のイテレータ */
    private static class ForwardIterator extends StringIterator {

        /**
         * コンストラクタ
         *
         * @param orig 走査対処文字列
         */
        ForwardIterator(String orig) {
            super(orig);
        }

        /** {@inheritDoc} */
        boolean hasNext() {
            return index < string.length();
        }

        /** {@inheritDoc} */
        char next() {
            return string.charAt(index++);
        }
    }


    /** 逆順のイテレータ。 */
    private static class ReverseIterator extends StringIterator {

        /**
         * コンストラクタ
         *
         * @param orig 走査対処文字列
         */
        ReverseIterator(String orig) {
            super(orig);
            index = orig.length() - 1;
        }

        /** {@inheritDoc} */
        boolean hasNext() {
            return index >= 0;
        }

        /** {@inheritDoc} */
        char next() {
            return string.charAt(index--);
        }
    }
}
