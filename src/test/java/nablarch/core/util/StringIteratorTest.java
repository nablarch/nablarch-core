package nablarch.core.util;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * {@link StringIterator}のテストクラス。
 *
 * @author Tsuyoshi Kawasaki
 */
public class StringIteratorTest {

    private static final String BEER = "\ud83c\udf63";
    private static final String SUSHI = "\ud83c\udf7a";
    private static final String OK = "\ud83c\udd97";

    @Test
    public void 文字列を正順で走査できること() {
        StringIterator sut = StringIterator.forward(SUSHI + "a" + BEER + "0" + OK);
        assertThat(String.valueOf(sut.next()), is(SUSHI));
        assertThat(String.valueOf(sut.next()), is("a"));
        assertThat(String.valueOf(sut.next()), is(BEER));
        assertThat(String.valueOf(sut.next()), is("0"));
        assertThat(String.valueOf(sut.next()), is(OK));
        assertThat(sut.hasNext(), is(false));
        assertHasNoElements(sut);
    }


    @Test
    public void 文字列を逆順で走査できること() {
        StringIterator sut = StringIterator.reverse(SUSHI + "a" + BEER + "0" + OK);
        assertThat(String.valueOf(sut.next()), is(OK));
        assertThat(String.valueOf(sut.next()), is("0"));
        assertThat(String.valueOf(sut.next()), is(BEER));
        assertThat(String.valueOf(sut.next()), is("a"));
        assertThat(String.valueOf(sut.next()), is(SUSHI));
        assertThat(sut.hasNext(), is(false));
        assertHasNoElements(sut);
    }

    @Test
    public void 指定した文字数の部分文字列を取得できること() {
        StringIterator sut = StringIterator.forward(SUSHI + "a" + BEER + "0" + OK);
        assertThat((sut.next(1)), is(SUSHI));
        assertThat(sut.next(2), is("a" + BEER));
        assertThat(sut.next(3), is("0" + OK)); // 1文字足りないが、最後まで取得できる
        assertHasNoElements(sut);
    }

    @Test
    public void 残りの文字列を取得できること() {
        StringIterator sut = StringIterator.forward(SUSHI + "a" + BEER + "0" + OK);
        assertThat((sut.next(2)), is(SUSHI + "a"));
        assertThat(sut.rest(), is(BEER + "0" + OK));
        assertThat("残りの文字が無い場合は空文字",
                   sut.rest(), is(""));
        assertHasNoElements(sut);
    }

    @Test(expected = IllegalArgumentException.class)
    public void 正順のIteratorを生成する際_引数がnullの時_例外が発生すること() {
        StringIterator.forward(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void 逆順のIteratorを生成する際_引数がnullの時_例外が発生すること() {
        StringIterator.reverse(null);
    }


    /**
     * 与えられた{@link StringIterator}インスタンスが、
     * 残りの文字を持たないこと（全ての文字を走査済みであること）を表明する。
     *
     * @param sut 表明の対象となるインスタンス
     */
    private void assertHasNoElements(StringIterator sut) {
        assertThat(sut.hasNext(), is(false));
        try {
            sut.next();
        } catch (NoSuchElementException e) {
            return;
        }
        fail();
    }
}