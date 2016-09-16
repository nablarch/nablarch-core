package nablarch.core.date;

import static junit.framework.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

/**
 * {@link nablarch.core.date.BasicSystemTimeProvider}のテストクラス。
 *
 * @author hisaaki sioiri
 */
public class BasicSystemTimeProviderTest {

    /**
     * {@link BasicSystemTimeProvider#getDate()} のテスト。
     *
     * @throws Exception
     */
    @Test
    public void testGetDate() throws Exception {

        BasicSystemTimeProvider time = new BasicSystemTimeProvider();

        long start = System.currentTimeMillis();
        Date date = time.getDate();
        long end = System.currentTimeMillis();
        assertTrue("getDate()呼び出し前の時間と、呼び出し後の時間の間であること。", start <= date.getTime() && date.getTime() <= end);
    }

    /**
     * {@link BasicSystemTimeProvider#getTimestamp()} のテスト。
     *
     * @throws Exception
     */
    @Test
    public void testGetTimestamp() throws Exception {
        BasicSystemTimeProvider time = new BasicSystemTimeProvider();

        long start = System.currentTimeMillis();
        Date date = time.getTimestamp();
        long end = System.currentTimeMillis();
        assertTrue("getTimestamp()呼び出し前の時間と、呼び出し後の時間の間であること。", start <= date.getTime() && date.getTime() <= end);
    }
}
