package eu.goodlike.http;

import com.google.api.client.http.LowLevelHttpRequest;
import eu.goodlike.util.Require;
import okhttp3.*;
import okio.BufferedSink;

import java.io.IOException;
import java.nio.channels.Channels;
import java.util.concurrent.TimeUnit;

import static eu.goodlike.util.Require.titled;

public final class OkHttpRequest extends LowLevelHttpRequest {

    @Override
    public void addHeader(String name, String value) {
        request.addHeader(name, value);
    }

    @Override
    public void setTimeout(int connectTimeout, int readTimeout) {
        if (connectTimeout != client.connectTimeoutMillis() || readTimeout != client.readTimeoutMillis())
            client = client.newBuilder()
                    .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                    .build();
    }

    @Override
    public OkHttpResponse execute() throws IOException {
        Response response = client.newCall(createRequest()).execute();
        return new OkHttpResponse(response);
    }

    OkHttpRequest(OkHttpClient client, Request.Builder request, String method) {
        this.client = Require.notNull(client, titled("client"));
        this.request = Require.notNull(request, titled("request"));
        this.method = Require.notBlank(method, titled("method")).toUpperCase();
    }

    OkHttpClient client;

    final Request.Builder request;
    final String method;

    private Request createRequest() {
        return request.method(method, getRequestBody()).build();
    }

    private RequestBody getRequestBody() {
        return getStreamingContent() == null
                ? null
                : new OkHttpRequestBody();
    }

    private final class OkHttpRequestBody extends RequestBody {
        @Override
        public MediaType contentType() {
            return MediaType.parse(getContentType());
        }

        @Override
        public long contentLength() {
            return getContentLength();
        }

        @Override
        @SuppressWarnings("NullableProblems")
        public void writeTo(BufferedSink sink) throws IOException {
            getStreamingContent().writeTo(Channels.newOutputStream(sink));
        }
    }

}
