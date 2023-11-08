package nablarch.fw;

/**
 * {@link DataReader}の実装に{@code synchronized}を付与するためのラッパークラス。
 *
 * @param <TData> このクラスが読み込んだデータの型
 */
public class SynchronizedDataReaderWrapper<TData> implements DataReader<TData> {

    /**
     * {@code synchronized}付与前の{@link DataReader}オブジェクト
     */
    private final DataReader<TData> originalReader;

    /**
     * コンストラクタ。
     * @param originalReader {@link DataReader}オブジェクト
     */
    public SynchronizedDataReaderWrapper(DataReader<TData> originalReader) {
        if(originalReader == null) {
            throw new IllegalArgumentException("originalReader must not be null.");
        }
        this.originalReader = originalReader;
    }

    @Override
    public synchronized TData read(ExecutionContext ctx) {
        return originalReader.read(ctx);
    }

    @Override
    public synchronized boolean hasNext(ExecutionContext ctx) {
        return originalReader.hasNext(ctx);
    }

    @Override
    public synchronized void close(ExecutionContext ctx) {
        originalReader.close(ctx);
    }

    /**
     * {@code synchronized}付与前の{@link DataReader}オブジェクトを返却する。
     *
     * @return {@code synchronized}付与前の{@link DataReader}オブジェクト
     */
    public DataReader<TData> getOriginalReader() {
        return originalReader;
    }
}
