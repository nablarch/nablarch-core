package nablarch.core.log.operation;

import nablarch.core.log.LogTestSupport;
import nablarch.core.log.MockLogger;
import nablarch.core.log.basic.LogLevel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * {@link OperationLogger}のテストクラス
 */
public class OperationLoggerTest extends LogTestSupport {

    private static MockedConstruction<MockLogger> mocked;
    private static MockLogger mockLogger;

    @BeforeClass
    public static void beforeClass() {
        mocked = Mockito.mockConstruction(MockLogger.class, (mock, context) -> {
            mockLogger = mock;
        });
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();
        System.setProperty("nablarch.log.filePath", "classpath:nablarch/core/log/log-mock.properties");
    }

    @Override
    @After
    public void tearDown() {
        super.tearDown();
        System.getProperties().remove("nablarch.log.filePath");
        reset(mockLogger);
    }

    @AfterClass
    public static void afterClass() {
        mocked.close();
    }

    /**
     * FATALレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_fatal() throws Exception {
        OperationLogger.write(LogLevel.FATAL, "テストメッセージ");

        verify(mockLogger).logFatal("テストメッセージ");
    }

    /**
     * ERRORレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_error() throws Exception {
        OperationLogger.write(LogLevel.ERROR, "テストメッセージ");

        verify(mockLogger).logError("テストメッセージ");
    }

    /**
     * WARNレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_warn() throws Exception {
        OperationLogger.write(LogLevel.WARN, "テストメッセージ");

        verify(mockLogger).logWarn("テストメッセージ");
    }

    /**
     * INFOレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_info() throws Exception {
        OperationLogger.write(LogLevel.INFO, "テストメッセージ");

        verify(mockLogger).logInfo("テストメッセージ");
    }

    /**
     * DEBUGレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_debug() throws Exception {
        OperationLogger.write(LogLevel.DEBUG, "テストメッセージ");

        verify(mockLogger).logDebug("テストメッセージ");
    }

    /**
     * TRACEレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_trace() throws Exception {
        OperationLogger.write(LogLevel.TRACE, "テストメッセージ");

        verify(mockLogger).logTrace("テストメッセージ");
    }

    /**
     * FATALレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_fatal_withThrowable() throws Exception {
        final RuntimeException exception = new RuntimeException("fatal");
        OperationLogger.write(LogLevel.FATAL, "テストメッセージ", exception);

        verify(mockLogger).logFatal("テストメッセージ", exception);
    }

    /**
     * ERRORレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_error_withThrowable() throws Exception {
        final RuntimeException exception = new RuntimeException("error");
        OperationLogger.write(LogLevel.ERROR, "テストメッセージ", exception);

        verify(mockLogger).logError("テストメッセージ", exception);
    }

    /**
     * WARNレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_warn_withThrowable() throws Exception {
        final RuntimeException exception = new RuntimeException("warn");
        OperationLogger.write(LogLevel.WARN, "テストメッセージ", exception);

        verify(mockLogger).logWarn("テストメッセージ", exception);
    }

    /**
     * INFOレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_info_withThrowable() throws Exception {
        final RuntimeException exception = new RuntimeException("info");
        OperationLogger.write(LogLevel.INFO, "テストメッセージ", exception);

        verify(mockLogger).logInfo("テストメッセージ", exception);
    }

    /**
     * DEBUGレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_debug_withThrowable() throws Exception {
        final RuntimeException exception = new RuntimeException("debug");
        OperationLogger.write(LogLevel.DEBUG, "テストメッセージ", exception);

        verify(mockLogger).logDebug("テストメッセージ", exception);
    }

    /**
     * TRACEレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_trace_withThrowable() throws Exception {
        final RuntimeException exception = new RuntimeException("trace");
        OperationLogger.write(LogLevel.TRACE, "テストメッセージ", exception);

        verify(mockLogger).logTrace("テストメッセージ", exception);
    }
}