package eu.goodlike.http;

import com.google.api.client.http.LowLevelHttpRequest;
import eu.goodlike.util.Require;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
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
        Response response = client.newCall(request.build()).execute();
        return new OkHttpResponse(response);
    }

    OkHttpRequest(OkHttpClient client, Request.Builder request, String method) {
        this.client = Require.notNull(client, titled("client"));
        this.request = Require.notNull(request, titled("request"));
        this.method = Require.notBlank(method, titled("method")).toUpperCase();
    }

    OkHttpClient getClient() {
        return client;
    }

    Request.Builder getRequest() {
        return request;
    }

    String getMethod() {
        return method;
    }

    private OkHttpClient client;
    private final Request.Builder request;
    private final String method;

}
