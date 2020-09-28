package nablarch.fw;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.Map;

import nablarch.core.util.Builder;
import org.junit.Test;


/**
 * {@link RequestPathMatchingHelper}のテスト。
 * @author Kiyohito Itoh
 */
public class RequestPathMatchingHelperTest {

    @Test
    public void testURISyntaxException() {
        RequestPathMatchingHelper helper = new RequestPathMatchingHelper(true);
        try {
            helper.setRequestPattern("hoge hoge");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("invalid requestPath: hoge hoge"));
            assertThat(e.getCause().getClass().getSimpleName(), is(URISyntaxException.class.getSimpleName()));
        }
    }

    @Test
    public void testInvalidPatternFormatException() {
        RequestPathMatchingHelper helper = new RequestPathMatchingHelper(true);
        try {
            helper.setRequestPattern("hogehoge");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("Invalid pattern format: hogehoge"));
            assertThat(e.getClass().getSimpleName(), is(IllegalArgumentException.class.getSimpleName()));
        }
    }

    @Test
    public void testInvalidInvoking() {
        try {
            new RequestPathMatchingHelper(true).isAppliedTo(null, null);
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is("requestPattern must be set."));
        }
    }

    @Test
    public void testToString() {
        String expect = Builder.linesf(
                "directoryPath         : /hoge/"
                , "resourceName          : foo.jsp"
                , "hasResourceNamePattern: true"
                , "resourceNamePattern   : ^\\Qfoo.jsp\\E$"
        );
        RequestPathMatchingHelper helper = new RequestPathMatchingHelper(true);
        helper.setRequestPattern("/hoge/foo.jsp");
        assertThat(helper.toString(), is(expect));
    }

    @Test
    public void testToStringWithAffectsDescendantNodes() {
        String expect = Builder.linesf(
                "directoryPath         : /hoge//"
                , "resourceName          : null"
                , "hasResourceNamePattern: false"
                , "resourceNamePattern   : null"
        );
        RequestPathMatchingHelper helper = new RequestPathMatchingHelper(true);
        helper.setRequestPattern("/hoge//");
        assertThat(helper.toString(), is(expect));
    }
}
