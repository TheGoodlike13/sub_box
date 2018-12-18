import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okhttp3.internal.http.StatusLine;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public final class OkHttpMethodSpike {

  public static void main(String... args) throws IOException {
    OkHttpClient client = new OkHttpClient();

    Request request = new Request.Builder().url("https://www.google.lt/").method(" ", Util.EMPTY_REQUEST).build();
    Response response = client.newCall(request).execute();
    System.out.println(StatusLine.get(response));
    ResponseBody body = response.body();
    if (body == null)
      System.out.println("No response body.");
    else
      System.out.println(IOUtils.toString(body.byteStream(), Charset.forName("UTF-8")));
  }

}
