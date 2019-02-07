package nablarch.core.util;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;


/**
 * {@link Base64Util}のテストクラス。
 *
 * @author Kiyohito Itoh
 */
public class Base64UtilTest {
    
    /**
     * {@link Base64Util#encode(byte[])}のテスト。
     */
    @Test
    public void testEncode() {
        assertNull(Base64Util.encode(null));
        assertThat(Base64Util.encode("".getBytes()), is(""));
        assertThat(Base64Util.encode("ABCDEFG".getBytes()), is("QUJDREVGRw=="));
        assertThat(Base64Util.encode("ABCDEFGH".getBytes()), is("QUJDREVGR0g="));
        assertThat(Base64Util.encode("ABCDEFGHI".getBytes()), is("QUJDREVGR0hJ"));
    }
    
    /**
     * {@link Base64Util#decode(String)}のテスト。
     */
    @Test
    public void testDecode() {
        assertNull(Base64Util.decode(null));
        assertThat(Base64Util.decode(""), is("".getBytes()));
        assertThat(Base64Util.decode("QUJDREVGRw=="), is("ABCDEFG".getBytes()));
        assertThat(Base64Util.decode("QUJDREVGR0g="), is("ABCDEFGH".getBytes()));
        assertThat(Base64Util.decode("QUJDREVGR0hJ"), is("ABCDEFGHI".getBytes()));
        try {
            Base64Util.decode("123");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("length of base64 was invalid. base64 = [123], length = [3]"));
        }
        try {
            Base64Util.decode("12=45678");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("position of '=' in base64 was invalid. base64 = [12=45678]"));
        }
        try {
            Base64Util.decode("123456=8");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("position of '=' in base64 was invalid. base64 = [123456=8]"));
        }
        try {
            Base64Util.decode("123456?8");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("base64 contained invalid character. base64 = [123456?8]"));
        }
    }
}
