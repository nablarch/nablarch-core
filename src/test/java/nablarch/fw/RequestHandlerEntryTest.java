package nablarch.fw;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.hamcrest.Matcher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class RequestHandlerEntryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testHandleFail() {

        RequestHandlerEntry<MockRequest, String> entry = new RequestHandlerEntry<MockRequest, String>();
        expectedException.expect(IllegalStateException.class);
        entry.handle(new MockRequest(null), new ExecutionContext());
    }

    @Test
    public void testHandle() throws Exception {

        final Handler<MockRequest, String> handlerA = new Handler<MockRequest, String>() {
            @Override
            public String handle(final MockRequest request, final ExecutionContext context) {
                return "A";
            }
        };

        final Handler<MockRequest, String> handlerB = new Handler<MockRequest, String>() {
            @Override
            public String handle(final MockRequest request, final ExecutionContext context) {
                return "B";
            }
        };
        RequestHandlerEntry<MockRequest, String> entry1 = new RequestHandlerEntry<MockRequest, String>()
                .setRequestPattern("/baseUriA//")
                .setHandler(handlerA);

        RequestHandlerEntry<MockRequest, String> entry2 = new RequestHandlerEntry<MockRequest, String>()
                .setRequestPattern("/baseUriB//")
                .setHandler(handlerB);

        final ExecutionContext context = new ExecutionContext();
        context.addHandler(entry1)
               .addHandler(entry2);

        final String resultA = context.handleNext(new MockRequest("/baseUriA/test"));
        assertThat(resultA, is("A"));

        final String resultB = context.handleNext(new MockRequest("/baseUriB/hoge/fuga"));
        assertThat(resultB, is("B"));
    }

    @Test
    public void testOtherMethods() throws Exception {
        final Handler<MockRequest, String> handlerA = new Handler<MockRequest, String>() {
            @Override
            public String handle(final MockRequest request, final ExecutionContext context) {
                return "A";
            }
        };
        RequestHandlerEntry<MockRequest, String> entry1 = new RequestHandlerEntry<MockRequest, String>()
                .setRequestPattern("/baseUriA//")
                .setHandler(handlerA);


        assertThat(entry1.getDelegate(), sameInstance(handlerA));
        assertThat(entry1.getDelegates(new MockRequest("/baseUriA/1"), new ExecutionContext()),
                   (Matcher) contains(sameInstance(handlerA)));

        assertThat(entry1.getDelegates(new MockRequest("/baseUriB/1"), new ExecutionContext()),
                hasSize(0));
    }

    private static class MockRequest implements Request<String> {

        private final String requestPath;

        private MockRequest(final String path) {
            requestPath = path;
        }

        @Override
        public String getRequestPath() {
            return requestPath;
        }

        @Override
        public Request<String> setRequestPath(final String requestPath) {
            return this;
        }

        @Override
        public String getParam(final String name) {
            return null;
        }

        @Override
        public Map<String, String> getParamMap() {
            return null;
        }
    }
}
  