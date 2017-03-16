package nablarch.core.log.operation;

import nablarch.core.log.LogTestSupport;
import nablarch.core.log.MockLogger;
import nablarch.core.log.basic.LogLevel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mockit.Mocked;
import mockit.Verifications;

/**
 * {@link OperationLogger}のテストクラス
 */
public class OperationLoggerTest extends LogTestSupport {

    @Mocked
    private MockLogger mockLogger;

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
    }

    /**
     * FATALレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_fatal() throws Exception {
        OperationLogger.write(LogLevel.FATAL, "テストメッセージ");

        new Verifications() {{
           mockLogger.logFatal("テストメッセージ");
           times = 1;
        }};
    }

    /**
     * ERRORレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_error() throws Exception {
        OperationLogger.write(LogLevel.ERROR, "テストメッセージ");

        new Verifications() {{
            mockLogger.logError("テストメッセージ");
            times = 1;
        }};
    }

    /**
     * WARNレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_warn() throws Exception {
        OperationLogger.write(LogLevel.WARN, "テストメッセージ");

        new Verifications() {{
            mockLogger.logWarn("テストメッセージ");
            times = 1;
        }};
    }

    /**
     * INFOレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_info() throws Exception {
        OperationLogger.write(LogLevel.INFO, "テストメッセージ");

        new Verifications() {{
            mockLogger.logInfo("テストメッセージ");
            times = 1;
        }};
    }

    /**
     * DEBUGレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_debug() throws Exception {
        OperationLogger.write(LogLevel.DEBUG, "テストメッセージ");

        new Verifications() {{
            mockLogger.logDebug("テストメッセージ");
            times = 1;
        }};
    }

    /**
     * TRACEレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_trace() throws Exception {
        OperationLogger.write(LogLevel.TRACE, "テストメッセージ");

        new Verifications() {{
            mockLogger.logTrace("テストメッセージ");
            times = 1;
        }};
    }

    /**
     * FATALレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_fatal_withThrowable() throws Exception {
        final RuntimeException exception = new RuntimeException("fatal");
        OperationLogger.write(LogLevel.FATAL, "テストメッセージ", exception);

        new Verifications() {{
            mockLogger.logFatal("テストメッセージ", exception);
            times = 1;
        }};
    }

    /**
     * ERRORレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_error_withThrowable() throws Exception {
        final RuntimeException exception = new RuntimeException("error");
        OperationLogger.write(LogLevel.ERROR, "テストメッセージ", exception);

        new Verifications() {{
            mockLogger.logError("テストメッセージ", exception);
            times = 1;
        }};
    }

    /**
     * WARNレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_warn_withThrowable() throws Exception {
        final RuntimeException exception = new RuntimeException("warn");
        OperationLogger.write(LogLevel.WARN, "テストメッセージ", exception);

        new Verifications() {{
            mockLogger.logWarn("テストメッセージ", exception);
            times = 1;
        }};
    }

    /**
     * INFOレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_info_withThrowable() throws Exception {
        final RuntimeException exception = new RuntimeException("info");
        OperationLogger.write(LogLevel.INFO, "テストメッセージ", exception);

        new Verifications() {{
            mockLogger.logInfo("テストメッセージ", exception);
            times = 1;
        }};
    }

    /**
     * DEBUGレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_debug_withThrowable() throws Exception {
        final RuntimeException exception = new RuntimeException("debug");
        OperationLogger.write(LogLevel.DEBUG, "テストメッセージ", exception);

        new Verifications() {{
            mockLogger.logDebug("テストメッセージ", exception);
            times = 1;
        }};
    }

    /**
     * TRACEレベルで運用通知ログを出力すること
     * @throws Exception
     */
    @Test
    public void testWrite_trace_withThrowable() throws Exception {
        final RuntimeException exception = new RuntimeException("trace");
        OperationLogger.write(LogLevel.TRACE, "テストメッセージ", exception);

        new Verifications() {{
            mockLogger.logTrace("テストメッセージ", exception);
            times = 1;
        }};
    }
}