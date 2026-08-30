package nablarch.fw;

import nablarch.core.util.annotation.Published;

/**
 * スレッドセーフなデータリーダであることを示すインタフェース。
 * <p>
 * このインタフェースを実装したデータリーダは、フレームワーク側で{@link SynchronizedDataReaderWrapper}で
 *
 * @param <TData> このクラスが読み込んだデータの型
 */
@Published(tag = "architect")
public interface ThreadSafeDataReader<TData> extends DataReader<TData> {
}
