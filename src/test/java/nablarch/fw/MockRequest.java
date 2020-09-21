package nablarch.fw;

import java.util.Map;

class MockRequest implements Request<Object> {

    private String requestPath;

    public MockRequest(String requestPath) {
        this.requestPath = requestPath;
    }

    @Override
    public String getRequestPath() {
        return requestPath;
    }

    @Override
    public Request<Object> setRequestPath(String requestPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getParam(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getParamMap() {
        throw new UnsupportedOperationException();
    }
}
