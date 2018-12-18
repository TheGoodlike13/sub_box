package eu.goodlike.http;

import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Function;

import static eu.goodlike.asserts.Asserts.assertInvalidNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class OkHttpLowLevelResponseTest {

    @Test
    public void nullInputs() {
        assertInvalidNull("response", OkHttpLowLevelResponse::new);
    }

    @Test
    public void getContent() {
        assertThat(newResponse().getContent()).isNull();
        assertThat(newResponse(b -> b.body(toBody("text"))).getContent()).hasContent("text");
    }

    @Test
    public void getContentEncoding() {
        assertThat(newResponse().getContentEncoding()).isNull();
        assertThat(newResponse(b -> b.addHeader("content-encoding", "gzip")).getContentEncoding()).isEqualTo("gzip");
    }

    @Test
    public void getContentLength() {
        assertThat(newResponse().getContentLength()).isEqualTo(0);
        assertThat(newResponse(b -> b.body(toBody("text"))).getContentLength()).isEqualTo(4);
    }

    @Test
    public void getContentType() {
        assertThat(newResponse().getContentType()).isNull();
        assertThat(newResponse(b -> b.body(toBody("any"))).getContentType()).isEqualTo("text/plain; charset=utf-8");
    }

    @Test
    public void getStatusLine() {
        assertThat(newResponse().getStatusLine()).isEqualTo("HTTP/1.1 200 OK");
    }

    @Test
    public void getStatusCode() {
        assertThat(newResponse().getStatusCode()).isEqualTo(200);
    }

    @Test
    public void getReasonPhrase() {
        assertThat(newResponse().getReasonPhrase()).isEqualTo("OK");
    }

    @Test
    public void getHeaderCount() {
        assertThat(newResponse().getHeaderCount()).isEqualTo(0);
        assertThat(newResponse(b -> b.addHeader("content-encoding", "gzip")).getHeaderCount()).isEqualTo(1);
        assertThat(newResponse(b -> b.addHeader("x-double", "1; 2").addHeader("x-double", "3")).getHeaderCount()).isEqualTo(2);
    }

    @Test
    public void getHeaderName() {
        assertThatExceptionOfType(ArrayIndexOutOfBoundsException.class)
                .isThrownBy(() -> newResponse().getHeaderName(0));

        assertThat(newResponse(b -> b.addHeader("content-encoding", "gzip")).getHeaderName(0)).isEqualTo("content-encoding");
        assertThat(newResponse(b -> b.addHeader("x-double", "1; 2").addHeader("x-double", "3")).getHeaderName(0)).isEqualTo("x-double");
        assertThat(newResponse(b -> b.addHeader("x-double", "1; 2").addHeader("x-double", "3")).getHeaderName(1)).isEqualTo("x-double");
    }

    @Test
    public void getHeaderValue() {
        assertThatExceptionOfType(ArrayIndexOutOfBoundsException.class)
                .isThrownBy(() -> newResponse().getHeaderValue(0));

        assertThat(newResponse(b -> b.addHeader("content-encoding", "gzip")).getHeaderValue(0)).isEqualTo("gzip");
        assertThat(newResponse(b -> b.addHeader("x-double", "1; 2").addHeader("x-double", "3")).getHeaderValue(0)).isEqualTo("1; 2");
        assertThat(newResponse(b -> b.addHeader("x-double", "1; 2").addHeader("x-double", "3")).getHeaderValue(1)).isEqualTo("3");
    }

    @Test
    public void contentIsClosedWithDisconnect() {
        ResponseBody mockBody = Mockito.mock(ResponseBody.class);

        newResponse(b -> b.body(mockBody)).disconnect();

        Mockito.verify(mockBody).close();
    }

    private OkHttpLowLevelResponse newResponse() {
        return newResponse(Function.identity());
    }

    private OkHttpLowLevelResponse newResponse(Function<Response.Builder, Response.Builder> steps) {
        Response.Builder builder = createResponseWithBasicValues();
        return new OkHttpLowLevelResponse(steps.apply(builder).build());
    }

    private Response.Builder createResponseWithBasicValues() {
        return new Response.Builder()
                .request(new Request.Builder().url("https://www.google.com/").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK");
    }

    private ResponseBody toBody(String text) {
        return ResponseBody.create(MediaType.get("text/plain"), text);
    }

}
