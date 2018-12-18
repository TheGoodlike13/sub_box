package eu.goodlike.http;

import com.google.api.client.http.LowLevelHttpResponse;
import eu.goodlike.util.Require;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.StatusLine;

import java.io.InputStream;
import java.util.Optional;

import static eu.goodlike.util.Require.titled;

public final class OkHttpLowLevelResponse extends LowLevelHttpResponse {

    @Override
    public InputStream getContent() {
        return getBody()
                .map(ResponseBody::byteStream)
                .orElse(null);
    }

    @Override
    public String getContentEncoding() {
        return response.header(HEADER_CONTENT_ENCODING);
    }

    @Override
    public long getContentLength() {
        return getBody()
                .map(ResponseBody::contentLength)
                .orElse(0L);
    }

    @Override
    public String getContentType() {
        return getBody()
                .map(ResponseBody::contentType)
                .map(MediaType::toString)
                .orElse(null);
    }

    @Override
    public String getStatusLine() {
        return StatusLine.get(response).toString();
    }

    @Override
    public int getStatusCode() {
        return response.code();
    }

    @Override
    public String getReasonPhrase() {
        return response.message();
    }

    @Override
    public int getHeaderCount() {
        return response.headers().size();
    }

    @Override
    public String getHeaderName(int index) {
        return response.headers().name(index);
    }

    @Override
    public String getHeaderValue(int index) {
        return response.headers().value(index);
    }

    @Override
    public void disconnect() {
        getBody().ifPresent(ResponseBody::close);
    }

    public OkHttpLowLevelResponse(Response response) {
        this.response = Require.notNull(response, titled("response"));
    }

    private final Response response;

    private Optional<ResponseBody> getBody() {
        return Optional.ofNullable(response.body());
    }

    private static final String HEADER_CONTENT_ENCODING = "content-encoding";

}
