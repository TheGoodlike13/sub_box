package eu.goodlike.http;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static eu.goodlike.test.asserts.Asserts.assertInvalidNull;
import static eu.goodlike.test.mocks.OkHttpMocks.basicRequest;
import static eu.goodlike.test.mocks.OkHttpMocks.basicResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.refEq;

public class OkHttpRequestTest {

    private final OkHttpClient clientMock = Mockito.mock(OkHttpClient.class);
    private final Request.Builder request = basicRequest();

    private final Call callMock = Mockito.mock(Call.class);

    @BeforeEach
    public void setup() throws IOException {
        Mockito.when(clientMock.connectTimeoutMillis()).thenReturn((int)TimeUnit.SECONDS.toMillis(20));
        Mockito.when(clientMock.readTimeoutMillis()).thenReturn((int)TimeUnit.SECONDS.toMillis(20));

        Mockito.when(callMock.execute()).thenReturn(basicResponse().build());
    }

    @Test
    public void nullInputs() {
        assertInvalidNull("client", (OkHttpClient client) -> new OkHttpRequest(client, request));
        assertInvalidNull("request", (Request.Builder request) -> new OkHttpRequest(clientMock, request));
    }

    @Test
    public void addHeader() {
        newRequest().addHeader("x-header", "value");

        assertThat(request.build().header("x-header")).isEqualTo("value");
    }

    @Test
    public void execute() throws IOException {
        mockCallForClient(clientMock);

        newRequest().execute();

        Mockito.verify(clientMock).newCall(refEq(request.build()));
        Mockito.verify(callMock).execute();
    }

    @Test
    public void setTimeout() {
        OkHttpRequest okHttpRequest = new OkHttpRequest(new OkHttpClient(), request);

        setTimeout(okHttpRequest, 30, 40);

        assertThat(okHttpRequest.getClient())
                .extracting(OkHttpClient::connectTimeoutMillis, OkHttpClient::readTimeoutMillis)
                .containsExactly(30000, 40000);
    }

    private OkHttpRequest newRequest() {
        return new OkHttpRequest(clientMock, request);
    }

    private void mockCallForClient(OkHttpClient clientMock) {
        Mockito.when(clientMock.newCall(any(Request.class))).thenReturn(callMock);
    }

    private void setTimeout(OkHttpRequest okHttpRequest, int connectionTimeoutSeconds, int readTimeoutSeconds) {
        okHttpRequest.setTimeout((int) TimeUnit.SECONDS.toMillis(connectionTimeoutSeconds), (int)TimeUnit.SECONDS.toMillis(readTimeoutSeconds));
    }

}
