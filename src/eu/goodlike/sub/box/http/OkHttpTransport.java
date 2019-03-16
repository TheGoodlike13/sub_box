package eu.goodlike.sub.box.http;

import com.google.api.client.http.HttpTransport;
import eu.goodlike.sub.box.util.require.Require;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import static eu.goodlike.sub.box.util.require.Require.titled;

/**
 * {@link HttpTransport} implementation which uses {@link OkHttpClient} to create and make HTTP requests. Just like
 * {@link OkHttpClient}, it is intended that a single instance of this class is reused for the entire application.
 * <p/>
 * This {@link HttpTransport} supports all HTTP methods.
 * <p/>
 * Upon shutdown, it aggressively releases all resources from containing {@link OkHttpClient} as described by its
 * documentation.
 */
public final class OkHttpTransport extends HttpTransport {

  @Override
  public boolean supportsMethod(String method) {
    return StringUtils.isNotBlank(method);
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
