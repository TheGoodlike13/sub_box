package eu.goodlike.sub.box.http;

import com.google.api.client.http.HttpMethods;
import com.google.common.io.Files;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.internal.Internal;
import okhttp3.internal.connection.RealConnection;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static eu.goodlike.test.asserts.Asserts.assertInvalidNull;
import static eu.goodlike.test.mocks.OkHttpMocks.basicRequest;
import static eu.goodlike.test.mocks.OkHttpMocks.basicUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class OkHttpTransportTest {

    private final OkHttpClient clientMock = Mockito.mock(OkHttpClient.class);
    private final OkHttpTransport transport = new OkHttpTransport(clientMock);

    @Test
    public void nullInputs() {
        assertInvalidNull("client", OkHttpTransport::new);
    }

    @Test
    public void supportsMethod() {
        assertThat(transport.supportsMethod(null)).isFalse();
        assertThat(transport.supportsMethod("")).isFalse();
        assertThat(transport.supportsMethod(" ")).isTrue();
        assertThat(transport.supportsMethod("not_a_method")).isTrue();

        assertThat(transport.supportsMethod(HttpMethods.CONNECT)).isTrue();
        assertThat(transport.supportsMethod(HttpMethods.DELETE)).isTrue();
        assertThat(transport.supportsMethod(HttpMethods.GET)).isTrue();
        assertThat(transport.supportsMethod(HttpMethods.HEAD)).isTrue();
        assertThat(transport.supportsMethod(HttpMethods.OPTIONS)).isTrue();
        assertThat(transport.supportsMethod(HttpMethods.PATCH)).isTrue();
        assertThat(transport.supportsMethod(HttpMethods.POST)).isTrue();
        assertThat(transport.supportsMethod(HttpMethods.PUT)).isTrue();
        assertThat(transport.supportsMethod(HttpMethods.TRACE)).isTrue();
    }

    @Test
    public void buildRequestForUnsupportedMethod() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> transport.buildRequest(null, basicUrl()));
    }

    @Test
    public void buildRequest() {
        OkHttpRequest request = transport.buildRequest("get", basicUrl());

        assertThat(request.client).isEqualTo(clientMock);
        assertThat(request.method).isEqualTo("GET");
        assertThat(request.request).isEqualToComparingFieldByFieldRecursively(basicRequest());
    }

    @Test
    public void shutdown() throws IOException {
        OkHttpClient client = new OkHttpClient();
        initializeConnectionPoolUsingInternalAPI(client);

        new OkHttpTransport(client).shutdown();

        assertThat(client.dispatcher().executorService().isShutdown()).isTrue();
        assertThat(client.connectionPool().connectionCount()).isZero();
    }

    @Test
    public void shutdownWithCache() throws IOException {
        File tempDir = Files.createTempDir();
        tempDir.deleteOnExit();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .cache(new Cache(tempDir, 1024))
                .build();

        new OkHttpTransport(client).shutdown();

        assertThat(client.cache().isClosed()).isTrue();
    }

    private void initializeConnectionPoolUsingInternalAPI(OkHttpClient client) {
        synchronized (client.connectionPool()) {
            Internal.instance.put(client.connectionPool(), new RealConnection(client.connectionPool(), null));
        }
    }

}
