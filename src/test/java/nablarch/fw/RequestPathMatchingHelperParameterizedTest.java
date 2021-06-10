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
                /* ディレクトリパスが "/" */
                // リソース名が空
                "/          , /                   , true ",
                "/          , /app                , false",
                "/          , /app/               , false",
                "/          , /index.jsp          , false",
                // リソース名が "*" のみ
                "/*         , /                   , true ",
                "/*         , /app                , true ",
                "/*         , /app/               , false",
                "/*         , /index.jsp          , false",
                // リソース名が "*" 以外のみ
                "/app       , /                   , false",
                "/app       , /app                , true ",
                "/app       , /app/               , false",
                "/app       , /index.jsp          , false",
                // リソース名が混合
                "/*.jsp     , /                   , false",
                "/*.jsp     , /jsp                , false",
                "/*.jsp     , /jsp/               , false",
                "/*.jsp     , /index.jsp          , true",

                /* ディレクトリパスが "//" */
                // リソース名が空
                "//         , /                   , true ",
                "//         , /app                , true ",
                "//         , /app/               , true ",
                "//         , /index.jsp          , true ",
                // リソース名が "*" のみ
                "//*        , /                   , true ",
                "//*        , /app                , true ",
                "//*        , /app/               , true ",
                "//*        , /index.jsp          , false",
                // リソース名が "*" 以外のみ
                "//app      , /                   , false",
                "//app      , /app                , true ",
                "//app      , /abc/app            , true ",
                "//app      , /app/abc            , false",
                "//app      , /app/               , false",
                "//app      , /index.jsp          , false",
                // リソース名が混合
                "//*.jsp    , /                   , false",
                "//*.jsp    , /jsp                , false",
                "//*.jsp    , /jsp/               , false",
                "//*.jsp    , /index.jsp          , true ",
                "//*.jsp    , /app/index.jsp      , true ",
                "//*.jsp    , /app/index.html     , false",
                "//*.jsp    , /app/admin/index.jsp, true ",

                /* ディレクトリパスが1つの階層を持ち "//" で終わる */
                // リソース名が空（ディレクトリパスの前方一致判定のみが行われる）
                "/app//     , /app                , false",
                "/app//     , /app/               , true ",
                "/app//     , /app/admin/         , true ",
                "/app//     , /app/admin/index.jsp, true ",
                "/app//     , /abc/app/admin/     , false",
                // リソース名が "*" のみ
                "/app//*    , /app                , false",
                "/app//*    , /app/               , true ",
                "/app//*    , /app/admin          , true ",
                "/app//*    , /app/admin/         , true ",
                "/app//*    , /app/admin/index    , true ",
                "/app//*    , /app/admin/index.jsp, false",
                "/app//*    , /abc/app/admin/     , false",
                // リソース名が "*" 以外のみ
                "/app//admin, /admin              , false",
                "/app//admin, /app                , false",
                "/app//admin, /app/               , false",
                "/app//admin, /app/admin          , true ",
                "/app//admin, /zzz/admin          , false",
                "/app//admin, /zzz/app/admin      , false",
                "/app//admin, /app/admin/         , false",
                "/app//admin, /app/admin.jsp      , false",
                "/app//admin, /app/zzz/admin      , true ",
                // リソース名が混合
                "/app//*.jsp, /app                , false",
                "/app//*.jsp, /app.jsp            , false",
                "/app//*.jsp, /app/               , false",
                "/app//*.jsp, /app/index.jsp      , true ",
                "/app//*.jsp, /app/index.html     , false",
                "/app//*.jsp, /zzz/index.jsp      , false",
                "/app//*.jsp, /zzz/app/index.jsp  , false",
                "/app//*.jsp, /app/zzz/index.jsp  , true ",

                /* ディレクトリパスが1つの階層を持ち "/" で終わり、 "*" を使用している */
                // リソース名が空
                "/*/       , /                  , false",
                "/*/       , /app               , false",
                "/*/       , /app/              , true ",
                "/*/       , /xyz/              , true ",
                "/*/       , /app/xyz           , false",
                "/*/       , /app/xyz/          , false",
                // リソース名が "*" のみ
                "/*/*      , /                  , false",
                "/*/*      , /app               , false",
                "/*/*      , /app/              , true ",
                "/*/*      , /xyz/              , true ",
                "/*/*      , /app/xyz           , true ",
                "/*/*      , /xyz/app           , true ",
                "/*/*      , /app/xyz/          , false",
                "/*/*      , /app/xyz.jsp       , false",
                "/*/*      , /app/xyz/zzz       , false",
                // リソース名が "*" 以外のみ
                "/*/test   , /                  , false",
                "/*/test   , /app               , false",
                "/*/test   , /app/              , false",
                "/*/test   , /app/test          , true ",
                "/*/test   , /xyz/test          , true ",
                "/*/test   , /app/test/         , false",
                "/*/test   , /app/test/test     , false",
                "/*/test   , /app/test.jsp      , false",
                // リソース名が混合
                "/*/*.jsp  , /                  , false",
                "/*/*.jsp  , /index.jsp         , false",
                "/*/*.jsp  , /app               , false",
                "/*/*.jsp  , /app/              , false",
                "/*/*.jsp  , /app/index.jsp     , true ",
                "/*/*.jsp  , /app/test.jsp      , true ",
                "/*/*.jsp  , /app/index.html    , false",
                "/*/*.jsp  , /xyz/test.jsp      , true ",
                "/*/*.jsp  , /app/xyz/index.jsp , false",

                /* ディレクトリパスが1つの階層を持ち "/" で終わり、 "*" を使用していない */
                // リソース名が空
                "/app/      , /app                    , false",
                "/app/      , /app/                   , true ",
                "/app/      , /xyz/                   , false",
                "/app/      , /app/xyz                , false",
                "/app/      , /xyz/app/               , false",
                // リソース名が "*" のみ
                "/app/*     , /app                    , false",
                "/app/*     , /app/                   , true ",
                "/app/*     , /xyz/                   , false",
                "/app/*     , /app/xyz                , true ",
                "/app/*     , /app/test               , true ",
                "/app/*     , /app/xyz.jsp            , false",
                "/app/*     , /app/xyz/               , false",
                "/app/*     , /xyz/app/xyz            , false",
                // リソース名が "*" 以外のみ
                "/app/admin , /app                    , false",
                "/app/admin , /app/admin              , true ",
                "/app/admin , /app/xyz                , false",
                "/app/admin , /app/admin/             , false",
                "/app/admin , /app/admin.jsp          , false",
                "/app/admin , /xyz/admin              , false",
                "/app/admin , /xyz/app/admin          , false",
                // リソース名が混合
                "/app/*.jsp , /app                    , false",
                "/app/*.jsp , /app/index.jsp          , true ",
                "/app/*.jsp , /app/test.jsp           , true ",
                "/app/*.jsp , /xyz/index.jsp          , false",
                "/app/*.jsp , /app/xyz/index.jsp      , false",
                "/app/*.jsp , /xyz/app/index.jsp      , false",

                /* ディレクトリパスが2つの階層を持ち "//" で終わる */
                // リソース名が空（ディレクトリパスの前方一致判定のみが行われる）
                "/app/xyz//     , /app/xyz                , false",
                "/app/xyz//     , /app/xyz/               , true ",
                "/app/xyz//     , /app/xyz/admin/         , true ",
                "/app/xyz//     , /app/xyz/admin/index.jsp, true ",
                "/app/xyz//     , /abc/xyz/app/admin/     , false",
                // リソース名が "*" のみ
                "/app/xyz//*    , /app/xyz                , false",
                "/app/xyz//*    , /app/xyz/               , true ",
                "/app/xyz//*    , /app/xyz/admin          , true ",
                "/app/xyz//*    , /app/xyz/admin/         , true ",
                "/app/xyz//*    , /app/xyz/admin/index    , true ",
                "/app/xyz//*    , /app/xyz/admin/index.jsp, false",
                "/app/xyz//*    , /abc/xyz/app/admin/     , false",
                // リソース名が "*" 以外のみ
                "/app/xyz//admin, /app/xyz                , false",
                "/app/xyz//admin, /app/xyz/               , false",
                "/app/xyz//admin, /app/xyz/admin          , true ",
                "/app/xyz//admin, /zzz/xyz/admin          , false",
                "/app/xyz//admin, /zzz/xyz/app/admin      , false",
                "/app/xyz//admin, /app/xyz/admin/         , false",
                "/app/xyz//admin, /app/xyz/admin.jsp      , false",
                "/app/xyz//admin, /app/xyz/zzz/admin      , true ",
                // リソース名が混合
                "/app/xyz//*.jsp, /app/xyz                , false",
                "/app/xyz//*.jsp, /app/xyz.jsp            , false",
                "/app/xyz//*.jsp, /app/xyz/               , false",
                "/app/xyz//*.jsp, /app/xyz/index.jsp      , true ",
                "/app/xyz//*.jsp, /app/xyz/test.jsp       , true ",
                "/app/xyz//*.jsp, /app/xyz/index.html     , false",
                "/app/xyz//*.jsp, /zzz/xyz/index.jsp      , false",
                "/app/xyz//*.jsp, /zzz/xyz/app/index.jsp  , false",
                "/app/xyz//*.jsp, /app/xyz/zzz/index.jsp  , true ",

                /* ディレクトリパスが2つの階層を持ち "/" で終わり、 "*" を1つ使用している */
                // リソース名が空
                "/app/*/       , /app                   , false",
                "/app/*/       , /app/                  , false",
                "/app/*/       , /app/xxx               , false",
                "/app/*/       , /app/xxx/              , true ",
                "/app/*/       , /app/xxx/yyy           , false",
                "/app/*/       , /app/xxx/yyy/          , false",
                "/app/*/       , /xxx/app/              , false",
                // リソース名が "*" のみ
                "/app/*/*      , /app                   , false",
                "/app/*/*      , /app/                  , false",
                "/app/*/*      , /app/xxx               , false",
                "/app/*/*      , /app/xxx/              , true ",
                "/app/*/*      , /app/xxx/yyy           , true ",
                "/app/*/*      , /app/xxx/yyy/          , false",
                "/app/*/*      , /app/xxx/yyy.jsp       , false",
                "/app/*/*      , /app/xxx/yyy/zzz       , false",
                "/app/*/*      , /xxx/app/              , false",
                // リソース名が "*" 以外のみ
                "/app/*/test   , /app                   , false",
                "/app/*/test   , /test                  , false",
                "/app/*/test   , /app/                  , false",
                "/app/*/test   , /app/test              , false",
                "/app/*/test   , /app/xxx               , false",
                "/app/*/test   , /app/xxx/              , false",
                "/app/*/test   , /app/xxx/test          , true ",
                "/app/*/test   , /app/xxx/test/         , false",
                "/app/*/test   , /app/xxx/test/yyy      , false",
                "/app/*/test   , /app/xxx/test.jsp      , false",
                "/app/*/test   , /app/xxx/yyy/test      , false",
                "/app/*/test   , /xxx/app/xxx/test      , false",
                // リソース名が混合
                "/app/*/*.jsp  , /app                   , false",
                "/app/*/*.jsp  , /index.jsp             , false",
                "/app/*/*.jsp  , /app/                  , false",
                "/app/*/*.jsp  , /app/index.jsp         , false",
                "/app/*/*.jsp  , /app/xxx               , false",
                "/app/*/*.jsp  , /app/xxx/              , false",
                "/app/*/*.jsp  , /app/xxx/index.jsp     , true ",
                "/app/*/*.jsp  , /app/xxx/test.jsp      , true ",
                "/app/*/*.jsp  , /app/xxx/index.html    , false",
                "/app/*/*.jsp  , /app/xxx/yyy/index.jsp , false",
                "/app/*/*.jsp  , /xxx/app/xxx/index.jsp , false",

                /* ディレクトリパスが2つの階層を持ち "/" で終わり、 "*" を2つ使用している */
                // リソース名が空
                "/*/*/       , /app                   , false",
                "/*/*/       , /app/                  , false",
                "/*/*/       , /app/xxx               , false",
                "/*/*/       , /app/xxx/              , true ",
                "/*/*/       , /xyz/abc/              , true ",
                "/*/*/       , /app/xxx/yyy           , false",
                "/*/*/       , /app/xxx/yyy/          , false",
                // リソース名が "*" のみ
                "/*/*/*      , /app                   , false",
                "/*/*/*      , /app/                  , false",
                "/*/*/*      , /app/xxx               , false",
                "/*/*/*      , /app/xxx/              , true ",
                "/*/*/*      , /xxx/app/              , true ",
                "/*/*/*      , /app/xxx/yyy           , true ",
                "/*/*/*      , /xxx/yyy/zzz           , true ",
                "/*/*/*      , /app/xxx/yyy/          , false",
                "/*/*/*      , /app/xxx/yyy.jsp       , false",
                "/*/*/*      , /app/xxx/yyy/zzz       , false",
                // リソース名が "*" 以外のみ
                "/*/*/test   , /app                   , false",
                "/*/*/test   , /test                  , false",
                "/*/*/test   , /app/                  , false",
                "/*/*/test   , /app/test              , false",
                "/*/*/test   , /app/xxx               , false",
                "/*/*/test   , /app/xxx/              , false",
                "/*/*/test   , /app/xxx/test          , true ",
                "/*/*/test   , /xxx/app/test          , true ",
                "/*/*/test   , /app/xxx/test/         , false",
                "/*/*/test   , /app/xxx/yyy           , false",
                "/*/*/test   , /app/xxx/test.jsp      , false",
                "/*/*/test   , /app/xxx/test/yyy      , false",
                "/*/*/test   , /app/xxx/yyy/test      , false",
                // リソース名が混合
                "/*/*/*.jsp  , /app                   , false",
                "/*/*/*.jsp  , /index.jsp             , false",
                "/*/*/*.jsp  , /app/                  , false",
                "/*/*/*.jsp  , /app/index.jsp         , false",
                "/*/*/*.jsp  , /app/xxx               , false",
                "/*/*/*.jsp  , /app/xxx/              , false",
                "/*/*/*.jsp  , /app/xxx/index.jsp     , true ",
                "/*/*/*.jsp  , /app/xxx/test.jsp      , true ",
                "/*/*/*.jsp  , /xxx/app/index.jsp     , true ",
                "/*/*/*.jsp  , /app/xxx/index.html    , false",
                "/*/*/*.jsp  , /app/xxx/yyy/index.jsp , false",

                /* ディレクトリパスが2つの階層を持ち "/" で終わり、 "*" を使用していない */
                // リソース名が空
                "/app/xyz/      , /app                    , false",
                "/app/xyz/      , /app/                   , false",
                "/app/xyz/      , /xyz/                   , false",
                "/app/xyz/      , /app/xyz                , false",
                "/app/xyz/      , /app/xyz/               , true ",
                "/app/xyz/      , /app/xyz/admin          , false",
                "/app/xyz/      , /app/zzz/xyz/           , false",
                "/app/xyz/      , /app/xyz/zzz/           , false",
                "/app/xyz/      , /zzz/app/xyz/           , false",
                // リソース名が "*" のみ
                "/app/xyz/*     , /app                    , false",
                "/app/xyz/*     , /app/                   , false",
                "/app/xyz/*     , /xyz/                   , false",
                "/app/xyz/*     , /app/xyz                , false",
                "/app/xyz/*     , /app/xyz/               , true ",
                "/app/xyz/*     , /app/xyz/admin          , true ",
                "/app/xyz/*     , /app/xyz/admin.jsp      , false",
                "/app/xyz/*     , /app/xyz/admin/         , false",
                "/app/xyz/*     , /zzz/app/xyz/admin      , false",
                "/app/xyz/*     , /app/zzz/admin          , false",
                "/app/xyz/*     , /app/zzz/xyz/admin      , false",
                // リソース名が "*" 以外のみ
                "/app/xyz/admin , /app                    , false",
                "/app/xyz/admin , /app/admin              , false",
                "/app/xyz/admin , /xyz/admin              , false",
                "/app/xyz/admin , /app/xyz/               , false",
                "/app/xyz/admin , /app/xyz/admin          , true ",
                "/app/xyz/admin , /app/xyz/admin.jsp      , false",
                "/app/xyz/admin , /app/xyz/admin/         , false",
                "/app/xyz/admin , /app/xyz/zzz            , false",
                "/app/xyz/admin , /zzz/app/xyz/admin      , false",
                "/app/xyz/admin , /app/zzz/admin          , false",
                "/app/xyz/admin , /app/zzz/xyz/admin      , false",
                // リソース名が混合
                "/app/xyz/*.jsp , /app                    , false",
                "/app/xyz/*.jsp , /app/index.jsp          , false",
                "/app/xyz/*.jsp , /xyz/index.jsp          , false",
                "/app/xyz/*.jsp , /app/xyz/               , false",
                "/app/xyz/*.jsp , /app/xyz/index          , false",
                "/app/xyz/*.jsp , /app/xyz/index.jsp      , true ",
                "/app/xyz/*.jsp , /app/xyz/admin.jsp      , true ",
                "/app/xyz/*.jsp , /app/xyz/index.html     , false",
                "/app/xyz/*.jsp , /zzz/app/xyz/index.jsp  , false",
                "/app/xyz/*.jsp , /app/zzz/index.jsp      , false",
                "/app/xyz/*.jsp , /app/xyz/zzz/index.jsp  , false",
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
