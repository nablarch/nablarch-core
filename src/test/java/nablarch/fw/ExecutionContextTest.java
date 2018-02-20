package nablarch.fw;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.CoreMatchers;

import nablarch.core.message.ApplicationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ExecutionContextTest {
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void handlerQueue() throws Exception {

        class Handler1 implements Handler<Object, Object> {
            @Override
            public Object handle(final Object o, final ExecutionContext context) {
                return null;
            }
        }

        class Handler2 implements Handler<Object, Object> {
            @Override
            public Object handle(final Object o, final ExecutionContext context) {
                return null;
            }
        }
        
        final ExecutionContext sut = new ExecutionContext();
        assertThat(sut.getHandlerQueue(), hasSize(0));
        sut.addHandler(new Handler1());
        assertThat(sut.getHandlerQueue(), contains(
                instanceOf(Handler1.class)
        ));
        sut.clearHandlers();
        assertThat(sut.getHandlerQueue(), hasSize(0));

        final Handler1 handler1 = new Handler1();
        sut.addHandlers(Arrays.asList(new Handler2(), handler1));
        assertThat(sut.getHandlerQueue(), hasSize(2));
        assertThat(sut.getHandlerOf(Handler1.class), sameInstance(handler1));

        assertThat(sut.addHandlers(null)
                      .getHandlerQueue(), hasSize(2));


        sut.setHandlerQueue(Arrays.asList(new Handler1(), new Handler2()));
        assertThat(sut.getHandlerQueue(), contains(
                instanceOf(Handler1.class),
                instanceOf(Handler2.class)
        ));
    }

    @Test
    public void addNullHandler() throws Exception {
        final ExecutionContext sut = new ExecutionContext();

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("handler must not be null.");
        sut.addHandler(null);
    }

    @Test
    public void testSessionContext() {
        ExecutionContext ctx = new ExecutionContext();
        ctx.setSessionScopedVar("key1", "val1");
        ctx.setSessionScopedVar("key2", "val2");
        assertThat(ctx.getSessionScopeMap()
                      .size(), is(2));
        ctx.invalidateSession();
        assertThat(ctx.getSessionScopeMap()
                      .size(), is(0));
    }

    @Test
    public void testIsNewSession() {
        // デフォルト実装では常にfalseを返す。
        assertThat(new ExecutionContext().isNewSession(), is(false));
    }

    @Test
    public void testHasSession() {
        assertThat("デフォルトでは常にtrue", new ExecutionContext().hasSession(), is(true));
    }

    /**
     * 例外を保持する下記メソッドのテスト。
     * <p/>
     * {@link ExecutionContext#setException(Throwable)}<br />
     * {@link ExecutionContext#getException()}<br />
     * {@link ExecutionContext#getApplicationException()}
     */
    @Test
    public void testException() {

        // 例外が設定されていない場合

        ExecutionContext context = new ExecutionContext();

        assertThat(context.getException(), nullValue());
        assertThat(context.getApplicationException(), nullValue());

        // RuntimeExceptionが設定された場合

        context = new ExecutionContext();

        RuntimeException runtimeException = new RuntimeException("runtime_test");
        context.setException(runtimeException);
        assertThat(context.getException(), CoreMatchers.<Throwable>is(runtimeException));
        assertThat(context.getApplicationException(), nullValue());

        // ApplicationExceptionが設定された場合

        context = new ExecutionContext();

        ApplicationException applicationException = new ApplicationException();
        context.setException(applicationException);
        assertThat(context.getException(), CoreMatchers.<Throwable>is(applicationException));
        assertThat(context.getApplicationException(), is(applicationException));

        // RuntimeException->RuntimeExceptionの順に設定された場合

        context = new ExecutionContext();

        RuntimeException runtimeException1st = new RuntimeException("runtime_test_1st");
        context.setException(runtimeException1st);
        assertThat(context.getException(), CoreMatchers.<Throwable>is(runtimeException1st));
        assertThat(context.getApplicationException(), nullValue());

        RuntimeException runtimeException2nd = new RuntimeException("runtime_test_2nd");
        context.setException(runtimeException2nd);
        assertThat(context.getException(), CoreMatchers.<Throwable>is(runtimeException2nd));
        assertThat(context.getApplicationException(), nullValue());

        // RuntimeException->ApplicationExceptionの順に設定された場合

        context = new ExecutionContext();

        runtimeException = new RuntimeException("runtime_test");
        context.setException(runtimeException);
        assertThat(context.getException(), CoreMatchers.<Throwable>is(runtimeException));
        assertThat(context.getApplicationException(), nullValue());

        applicationException = new ApplicationException();
        context.setException(applicationException);
        assertThat(context.getException(), CoreMatchers.<Throwable>is(applicationException));
        assertThat(context.getApplicationException(), is(applicationException));

        // ApplicationException->ApplicationExceptionの順に設定された場合

        context = new ExecutionContext();

        ApplicationException applicationException1st = new ApplicationException();
        context.setException(applicationException1st);
        assertThat(context.getException(), CoreMatchers.<Throwable>is(applicationException1st));
        assertThat(context.getApplicationException(), is(applicationException1st));

        ApplicationException applicationException2nd = new ApplicationException();
        context.setException(applicationException2nd);
        assertThat(context.getException(), CoreMatchers.<Throwable>is(applicationException2nd));
        assertThat(context.getApplicationException(), is(applicationException2nd));

        // ApplicationException->RuntimeExceptionの順に設定された場合

        context = new ExecutionContext();

        applicationException = new ApplicationException();
        context.setException(applicationException);
        assertThat(context.getException(), CoreMatchers.<Throwable>is(applicationException));
        assertThat(context.getApplicationException(), is(applicationException));

        runtimeException = new RuntimeException("runtime_test");
        context.setException(runtimeException);
        assertThat(context.getException(), CoreMatchers.<Throwable>is(runtimeException)); // ApplicationException以外が設定された場合は上書きされる。
        assertThat(context.getApplicationException(), is(applicationException));
    }

    @Test
    public void testEmptyHandler() {
        class Handler1 implements Handler<String, String> {
            @Override
            public String handle(String s, ExecutionContext context) {
                return null;
            }
        }
        class Handler2 implements Handler<String, String> {
            @Override
            public String handle(String s, ExecutionContext context) {
                return null;
            }
        }
        
        ExecutionContext context = new ExecutionContext();
        assertThat(context.findHandler(new Object(), Handler1.class, null), is(nullValue()));

        context.addHandler(new Handler1());
        context.addHandler(new Handler2());
        assertThat(context.findHandler("1", Handler2.class, Handler2.class), is(instanceOf(Handler2.class)));
        assertThat(context.findHandler("1", Handler2.class, Handler1.class), is(nullValue()));
    }

    @Test
    public void testReader() {
        ExecutionContext context = new ExecutionContext();
        assertThat("リーダが設定されていないのでnull", context.readNextData(), is(nullValue()));
        context.closeReader();

        context.setDataReader(new DataReader<Object>() {
            boolean hasNext = true;
            @Override
            public Object read(ExecutionContext ctx) {
                return "hoge";
            }
            @Override
            public boolean hasNext(ExecutionContext ctx) {
                boolean result = hasNext;
                hasNext = false;
                return result;
            }

            @Override
            public void close(ExecutionContext ctx) {
            }
        });

        assertThat((String) context.readNextData(), is("hoge"));
        assertThat(context.readNextData(), is(nullValue()));

        context.setDataReader(new DataReader<Object>() {
            @Override
            public Object read(ExecutionContext ctx) {
                return null;
            }

            @Override
            public boolean hasNext(ExecutionContext ctx) {
                return false;
            }

            @Override
            public void close(ExecutionContext ctx) {
                throw new RuntimeException();
            }
        });
        // エラーがでても正常
        context.closeReader();

        context.setDataReader(new DataReader<Object>() {
            @Override
            public Object read(ExecutionContext ctx) {
                return null;
            }

            @Override
            public boolean hasNext(ExecutionContext ctx) {
                return false;
            }

            @Override
            public void close(ExecutionContext ctx) {
                throw new Error();
            }
        });
        // エラーがでても正常
        context.closeReader();
    }

    @Test
    public void testRequestScopeMap() {
        ExecutionContext context = new ExecutionContext();

        assertThat(context.getRequestScopeMap().isEmpty(), is(true));
        context.setRequestScopeMap(new HashMap<String, Object>() {{
            put("1", "2");
        }});
        assertThat(context.getRequestScopeMap().isEmpty(), is(false));
    }

    @Test
    public void testSessionScopeMap() throws Exception {
        ExecutionContext context = new ExecutionContext();

        assertThat(context.getSessionScopeMap().isEmpty(), is(true));
        
        context.setSessionScopeMap(new HashMap<String, Object>() {{
            put("1", "2");
        }});
        assertThat(context.getSessionScopeMap().isEmpty(), is(false));
        context.setSessionScopedVar("3", "4");
        assertThat(context.getSessionScopedVar("3"), CoreMatchers.<Object>is("4"));
    }

    @Test
    public void testSessionStoreMap() {
        ExecutionContext ctx = new ExecutionContext();
        assertThat(ctx.getSessionStoreMap().isEmpty(), is(true));
        ctx.setSessionStoreMap(new HashMap<String, Object>() {{
            put("3", "4");
        }});
        assertThat(ctx.getSessionStoreMap().size(), is(1));
        ctx.setSessionStoredVar("5", "6");
        assertThat(ctx.getSessionStoreMap().size(), is(2));
    }

    /**
     * ExecutionContextがコピーできることを確認する。
     */
    @Test
    public void testCopy() {
        ExecutionContext orgCtx = new ExecutionContext();

        //コピー後について、requestScopeMapについては、新規オブジェクトが生成されていることを確認したい。
        //確認できるようにするため、コピー元に中身の詰まったmapを設定する。
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("hoge","hoge");
        orgCtx.setRequestScopeMap(map);

        //テスト用のダミー実装をおこなったdataReader
        final DataReader<Object> dataReader = new DataReader<Object>(){

            @Override
            public Object read(ExecutionContext ctx) {
                return null;
            }

            @Override
            public boolean hasNext(ExecutionContext ctx) {
                return false;
            }

            @Override
            public void close(ExecutionContext ctx) {

            }
        };

        orgCtx.setDataReaderFactory(new DataReaderFactory<Object>() {
            @Override
            public DataReader<Object> createReader(ExecutionContext context) {
                //同一のDataReaderFactoryのインスタンスを使用すると、同一のdataReaderが返るように実装
                return dataReader;
            }
        });

        ExecutionContext newCtx = orgCtx.copy();
        assertEquals(newCtx.getClass(), ExecutionContext.class);
        assertThat(newCtx.getHandlerQueue(), is(orgCtx.getHandlerQueue()));
        assertThat(newCtx.getSessionScopeMap(), is(orgCtx.getSessionScopeMap()));
        assertThat(newCtx.getSessionStoreMap(), is(orgCtx.getSessionStoreMap()));
        assertThat(newCtx.getMethodBinder(), is(orgCtx.getMethodBinder()));
        //requestScopeMapについては、新規オブジェクトが生成されているはず。空であることを確認する。
        assertThat(newCtx.getRequestScopeMap().isEmpty(), is(true));
        //readerFactoryの確認（dataReaderが同一であれば、readerFactoryも同一である）
        assertThat(newCtx.getDataReader(), is(orgCtx.getDataReader()));
    }

    /**
     * copyInternalがでたらめな実装だと、コピー時に例外が送出されることを確認する。
     */
    @Test
    public void testFailCopyInternal() {
        ExecutionContext orgCtx = new ExecutionContext(){
            //ExecutionContextの派生クラスを作成するが、copyInternal実装しない。
            //(実装しないことで、結果的にcopyInternalが不適切な実装になる)
        };

        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("copyInternal method is not properly implemented.");
        orgCtx.copy();
    }

}
