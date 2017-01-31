package nablarch.core.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.*;

import org.hamcrest.CoreMatchers;

import org.junit.Test;

/**
 * {@link OperatorNoticeException}のテスト。
 */
public class OperatorNoticeExceptionTest {

    @Test
    public void fromMessage() throws Exception {
        final OperatorNoticeException sut = new OperatorNoticeException("メッセージ");
        
        assertThat(sut.getMessage(), is("メッセージ"));
        assertThat(sut.getCause(), is(nullValue()));
    }

    @Test
    public void fromMessageAndCause() throws Exception {
        final Throwable cause = new NullPointerException("null");
        final OperatorNoticeException sut = new OperatorNoticeException("message", cause);

        assertThat(sut.getMessage(), is("message"));
        assertThat(sut.getCause(), is(sameInstance(cause)));
    }
}