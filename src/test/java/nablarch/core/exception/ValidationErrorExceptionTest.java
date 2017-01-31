package nablarch.core.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * {@link ValidationErrorException}のテストクラス。
 */
public class ValidationErrorExceptionTest {

    @Test
    public void fromNone() throws Exception {
        final ValidationErrorException sut = new ValidationErrorException();

        assertThat(sut.getMessage(), is(nullValue()));
        assertThat(sut.getCause(), is(nullValue()));
    }

    @Test
    public void fromMessage() throws Exception {
        final ValidationErrorException sut = new ValidationErrorException("メッセージ");
        assertThat(sut.getMessage(), is("メッセージ"));
        assertThat(sut.getCause(), is(nullValue()));
    }

    @Test
    public void fromMessageAndCause() throws Exception {
        final Throwable cause = new IllegalArgumentException("error");
        final ValidationErrorException sut = new ValidationErrorException("めっせーじ", cause);

        assertThat(sut.getMessage(), is("めっせーじ"));
        assertThat(sut.getCause(), is(sameInstance(cause)));
    }
}