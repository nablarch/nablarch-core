package nablarch.fw;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class RequestPathMatchingHelperParameterizedTest {

    @Parameter(0)
    public String pattern;

    @Parameter(1)
    public String requestPath;

    @Parameter(2)
    public boolean expected;

    @Test
    public void test() throws Exception {
        RequestPathMatchingHelper sut = new RequestPathMatchingHelper(false);
        sut.setRequestPattern(pattern);

        MockRequest request = new MockRequest(requestPath);

        boolean actual = sut.isAppliedTo(request, null);
        assertEquals(expected, actual);

    }

    @Parameters(name = "pattern = {0}, requestPath = {1}, expected = {2}")
    public static Collection<Object[]> parameters() {
        String[] sources = {
                "/          , /                   , true ",
                "/          , /app                , false",
                "/          , /app/               , false",
                "/          , /index.jsp          , false",
                "//         , /                   , true ",
                "//         , /app                , true ",
                "//         , /app/               , true ",
                "//         , /index.jsp          , true ",
                "/*         , /                   , true ",
                "/*         , /app                , true ",
                "/*         , /app/               , false",
                "/*         , /index.jsp          , false",
                "/app/*     , /app/               , true ",
                "/app/*     , /app/abc            , true ",
                "/app/*     , /app/abc/           , false",
                "/app/*     , /app/index.jsp      , false",
                "/ab/cdef   , /ab/cdef            , true ",
                "/ab/cdef/  , /ab/cdef            , false",
                "/ab/cdef/  , /ab/cdef/           , true ",
                "/app/*.jsp , /app/index.jsp      , true ",
                "/app/*.jsp , /app/admin          , false",
                "/app/*/test, /app/admin/test     , true ",
                "/app/*/test, /app/admin/test/    , false",
                "/app/*/test, /app/admin/test/aa  , false",
                "/app/*/test, /app/test/          , false",
                "/app//     , /                   , false",
                "/app//     , /app/               , true ",
                "/app//     , /app/admin/         , true ",
                "/app//     , /app/admin/index.jsp, true ",
                "//*.jsp    , /app/index.jsp      , true ",
                "//*.jsp    , /app/admin/index.jsp, true ",
                "//*.jsp    , /app/index.html     , false",
        };
        List<Object[]> parameters = new ArrayList<Object[]>();
        for (String source : sources) {
            List<Object> parameter = new ArrayList<Object>();
            String[] splitted = source.split(",");
            parameter.add(splitted[0].trim());
            parameter.add(splitted[1].trim());
            parameter.add(Boolean.parseBoolean(splitted[2].trim()));
            parameters.add(parameter.toArray());
        }
        return parameters;
    }

}
