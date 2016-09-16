package nablarch.core.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

public class GlobTest {

    @Test
    public void testEmptyPattern() {
        Pattern empty = Glob.compile("");
        assertTrue(empty.matcher("yth115yea;yu")
                        .matches());
        assertTrue(empty.matcher("")
                        .matches());
        assertTrue(empty.matcher("test.txt")
                        .matches());
        assertFalse(empty.matcher("test/hoge")
                         .matches());

        empty = Glob.compile(null);
        assertTrue(empty.matcher("yth115yea;yu")
                        .matches());
        assertTrue(empty.matcher("")
                        .matches());
        assertTrue(empty.matcher("test.txt")
                        .matches());
        assertFalse(empty.matcher("test/hoge")
                         .matches());
    }

    @Test
    public void testPatternExpressionIncludingWildCardForString() {
        Pattern glob = Glob.compile("*.txt");
        assertTrue(glob.matcher("hoge.txt")
                       .matches());
        assertTrue(glob.matcher(".txt")
                       .matches());
        assertTrue(glob.matcher("hog ee.txt")
                       .matches());

        assertFalse(glob.matcher("hoge.text")
                        .matches());
        assertTrue(glob.matcher("hoge.bak.txt")
                       .matches());
        assertFalse(glob.matcher("/fuga/hoge.txt")
                        .matches());
        assertFalse(glob.matcher("")
                        .matches());

        glob = Glob.compile("/aba/G*");
        assertTrue(glob.matcher("/aba/G7777")
                       .matches());
        assertTrue(glob.matcher("/aba/G7777.jsp")
                       .matches());
        assertFalse(glob.matcher("/aba/F7777")
                        .matches());
    }

    @Test
    public void testPatternExpressionIncludingWildCardForOneCharacter() {
        Pattern glob = Glob.compile("*.t?t");
        assertTrue(glob.matcher("hoge.txt")
                       .matches());
        assertTrue(glob.matcher("hoge.tyt")
                       .matches());
        assertTrue(glob.matcher("hoge.t0t")
                       .matches());
        assertFalse(glob.matcher("hoge.tt")
                        .matches());

        glob = Glob.compile("/aba?/*/*");
        assertTrue(glob.matcher("/aba1/hoge/test.txt")
                       .matches());
        assertTrue(glob.matcher("/aba2/hoge/test.txt")
                       .matches());
        assertFalse(glob.matcher("/aba/hoge/test.txt")
                        .matches());
        assertFalse(glob.matcher("/aba3/test.txt")
                        .matches());
    }
}
