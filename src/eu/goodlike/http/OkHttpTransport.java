package eu.goodlike.http;

import com.google.api.client.http.HttpTransport;
import eu.goodlike.util.Require;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import static eu.goodlike.util.Require.titled;

public final class OkHttpTransport extends HttpTransport {

    @Override
    public boolean supportsMethod(String method) {
        return StringUtils.isNotEmpty(method);
    }

    @Override
    protected OkHttpRequest buildRequest(String method, String url) {
        if (!supportsMethod(method))
            throw new IllegalArgumentException("Unsupported HTTP method: " + method);

        return new OkHttpRequest(client, new Request.Builder().url(url), method);
    }

    @Override
    public void shutdown() throws IOException {
        shutdownExecutorService();
        shutdownConnectionPool();
        shutdownCache();
    }

    public OkHttpTransport(OkHttpClient client) {
        this.client = Require.notNull(client, titled("client"));
    }

    private final OkHttpClient client;

    private void shutdownExecutorService() {
        client.dispatcher().executorService().shutdown();
    }

    private void shutdownConnectionPool() {
        client.connectionPool().evictAll();
    }

    private void shutdownCache() throws IOException {
        Cache cache = client.cache();
        if (cache != null)
            cache.close();
    }

}
