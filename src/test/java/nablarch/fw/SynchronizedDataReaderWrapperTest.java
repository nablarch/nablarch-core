package nablarch.fw;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("NonAsciiCharacters")
public class SynchronizedDataReaderWrapperTest {

    private DataReader<Integer> sut;

    @Test
    public void readメソッドの呼び出しタイミングが同期されていること() throws Exception {

        int nThreads = 2;

        sut = new SynchronizedDataReaderWrapper<>(new MockDataReader(2, new CountDownLatch(nThreads), new CountDownLatch(nThreads), 500));
        ExecutionContext ctx = new ExecutionContext();

        List<Callable<Integer>> tasks = new ArrayList<>();
        Callable<Integer> task = () -> sut.read(ctx);
        for (int i = 0; i < nThreads; i++) {
            tasks.add(task);
        }

        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        List<Future<Integer>> result = executor.invokeAll(tasks);

        Set<Integer> actual = new HashSet<>();
        for (Future<Integer> r : result) {
            actual.add(r.get());
        }

        assertEquals(2, actual.size());
        assertTrue(actual.containsAll(List.of(0,1)));
    }

    @Test
    public void hasNextメソッドの呼び出しタイミングが同期されていること() throws Exception {

        int nThreads = 2;

        sut = new SynchronizedDataReaderWrapper<>(new MockDataReader(1, new CountDownLatch(nThreads), new CountDownLatch(nThreads), 500));
        ExecutionContext ctx = new ExecutionContext();

        List<Callable<Boolean>> tasks = new ArrayList<>();
        Callable<Boolean> task = () -> sut.hasNext(ctx);
        for (int i = 0; i < nThreads; i++) {
            tasks.add(task);
        }

        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        List<Future<Boolean>> result = executor.invokeAll(tasks);

        Set<Boolean> actual = new HashSet<>();
        for (Future<Boolean> r : result) {
            actual.add(r.get());
        }

        assertEquals(2, actual.size());
        assertTrue(actual.containsAll(List.of(true, false)));
    }


    @Test
    public void closeメソッドの呼び出しタイミングが同期されていること() throws Exception {

        int nThreads = 2;

        sut = new SynchronizedDataReaderWrapper<>(new MockDataReader(1, new CountDownLatch(nThreads), new CountDownLatch(nThreads), 500));
        ExecutionContext ctx = new ExecutionContext();

        List<Callable<Void>> tasks = new ArrayList<>();
        Callable<Void> task = () -> {
            sut.close(ctx);
            return null;
        };

        for (int i = 0; i < nThreads; i++) {
            tasks.add(task);
        }

        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        List<Future<Void>> result = executor.invokeAll(tasks);

        // closeへのアクセスが同期化されていたら、1つ目のスレッドでcloseが呼ばれているので、2つ目のスレッドでIndexOutOfBoundsExceptionが発生する
        ExecutionException rr = assertThrows(ExecutionException.class, () -> {
            for (Future<Void> r : result) {
                r.get();
            }
        });
        assertEquals(ArrayIndexOutOfBoundsException.class, rr.getCause().getClass());
        assertEquals("Index 1 out of bounds for length 1", rr.getCause().getMessage());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void nullのデータリーダを設定した場合例外が発生すること() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class,
                () -> sut = new SynchronizedDataReaderWrapper<>(null));
        assertEquals("originalReader must not be null.", result.getMessage());
    }

    @Test
    public void synchronized付与前のDataReaderオブジェクトを取得できること() {
        DataReader<Integer> originalReader = new MockDataReader(0,null,null,0);
        sut = new SynchronizedDataReaderWrapper<>(originalReader);

        assertEquals(originalReader, ((SynchronizedDataReaderWrapper<Integer>)sut).getOriginalReader());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static class MockDataReader implements DataReader<Integer> {

        private final int[] source;

        private final CountDownLatch firstGate;

        private final CountDownLatch secondGate;

        private final long waitMillis;

        private int index = 0;

        public MockDataReader(int sourceSize, CountDownLatch firstGate, CountDownLatch secondGate, long waitMillis) {
            source = new int[sourceSize];
            for (int i = 0; i < sourceSize; i++) {
                source[i] = i;
            }

            this.firstGate = firstGate;
            this.secondGate = secondGate;
            this.waitMillis = waitMillis;
        }

        @Override
        public Integer read(ExecutionContext ctx) {
            if(index >= source.length) {
                return null;
            }

            latch(firstGate);
            int result = source[index];
            latch(secondGate);
            index++;
            return result;
        }

        /**
         * {@inheritDoc}
         *
         * 本来{@code hasNext()}内で状態を変更してはならないが、同期化のテストのためあえて状態変更している。
         *
         * @param ctx 実行コンテキスト
         * @return
         */
        @Override
        public boolean hasNext(ExecutionContext ctx) {
            latch(firstGate);
            boolean result = index < source.length;
            latch(secondGate);
            index++;
            return result;
        }

        @Override
        public void close(ExecutionContext ctx) {
            latch(firstGate);
            int result = source[index];
            latch(secondGate);
            index++;
        }

        private void latch(CountDownLatch gate) {
            gate.countDown();
            try {
                gate.await(waitMillis, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}